package com.agendasalas.service;

import com.agendasalas.dto.ReservaRequest;
import com.agendasalas.dto.ReservaResponse;
import com.agendasalas.entity.EstadoReserva;
import com.agendasalas.entity.Reserva;
import com.agendasalas.entity.Sala;
import com.agendasalas.entity.Usuario;
import com.agendasalas.exception.ConflictoException;
import com.agendasalas.exception.RecursoNoEncontradoException;
import com.agendasalas.mapper.ReservaMapper;
import com.agendasalas.repository.ReservaRepository;
import com.agendasalas.repository.SalaRepository;
import com.agendasalas.repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final SalaRepository salaRepository;
    private final UsuarioRepository usuarioRepository;

    public ReservaService(
            ReservaRepository reservaRepository,
            SalaRepository salaRepository,
            UsuarioRepository usuarioRepository) {

        this.reservaRepository = reservaRepository;
        this.salaRepository = salaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public Reserva buscarPorId(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe la reserva con ID " + id
                        )
                );
    }

    @Transactional
    public Reserva crear(Reserva reserva) {
        validarReferencias(reserva);
        validarReserva(reserva, null);

        return reservaRepository.save(reserva);
    }

    @Transactional
    public void eliminar(Long id) {
        Reserva reserva = buscarPorId(id);
        reservaRepository.delete(reserva);
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> listarRespuestas() {
        return reservaRepository.findAll()
                .stream()
                .map(ReservaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservaResponse buscarRespuestaPorId(Long id) {
        return ReservaMapper.toResponse(buscarPorId(id));
    }

    @Transactional
    public ReservaResponse crearDesdeRequest(ReservaRequest request) {
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe el usuario indicado"
                        )
                );

        Sala sala = salaRepository.findById(request.salaId())
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe la sala indicada"
                        )
                );

        Reserva reserva = ReservaMapper.toEntity(request, usuario, sala);

        validarReserva(reserva, null);

        Reserva guardada = reservaRepository.save(reserva);

        return ReservaMapper.toResponse(guardada);
    }

    @Transactional
    public ReservaResponse actualizarDesdeRequest(
            Long id,
            ReservaRequest request) {

        // Buscamos la reserva una sola vez.
        Reserva existente = buscarPorId(id);

        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe el usuario indicado"
                        )
                );

        Sala sala = salaRepository.findById(request.salaId())
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe la sala indicada"
                        )
                );

        Reserva datos = ReservaMapper.toEntity(request, usuario, sala);

        validarReserva(datos, id);

        existente.setFecha(datos.getFecha());
        existente.setHoraInicio(datos.getHoraInicio());
        existente.setHoraFin(datos.getHoraFin());
        existente.setUsuario(usuario);
        existente.setSala(sala);
        existente.setEstado(datos.getEstado());

        Reserva guardada = reservaRepository.save(existente);

        return ReservaMapper.toResponse(guardada);
    }

    private void validarReferencias(Reserva reserva) {
        if (reserva == null
                || reserva.getSala() == null
                || reserva.getUsuario() == null
                || reserva.getSala().getId() == null
                || reserva.getUsuario().getId() == null) {

            throw new IllegalArgumentException(
                    "La reserva debe tener un usuario y una sala válidos"
            );
        }

        if (!salaRepository.existsById(reserva.getSala().getId())) {
            throw new RecursoNoEncontradoException(
                    "La sala indicada no existe"
            );
        }

        if (!usuarioRepository.existsById(reserva.getUsuario().getId())) {
            throw new RecursoNoEncontradoException(
                    "El usuario indicado no existe"
            );
        }
    }

    private void validarReserva(Reserva reserva, Long reservaId) {

        if (reserva.getFecha() == null
                || reserva.getHoraInicio() == null
                || reserva.getHoraFin() == null
                || reserva.getEstado() == null
                || reserva.getSala() == null
                || reserva.getUsuario() == null
                || reserva.getSala().getId() == null
                || reserva.getUsuario().getId() == null) {

            throw new IllegalArgumentException(
                    "Todos los datos de la reserva son obligatorios"
            );
        }

        if (!reserva.getHoraInicio().isBefore(reserva.getHoraFin())) {
            throw new IllegalArgumentException(
                    "La hora de inicio debe ser anterior a la hora de fin"
            );
        }

        Long salaId = reserva.getSala().getId();

        if (reserva.getEstado() == EstadoReserva.CONFIRMADA) {
            long superposiciones =
                    reservaRepository.contarSuperposiciones(
                            salaId,
                            reserva.getFecha(),
                            EstadoReserva.CONFIRMADA,
                            reserva.getHoraFin(),
                            reserva.getHoraInicio(),
                            reservaId
                    );

            if (superposiciones > 0) {
                throw new ConflictoException(
                        "La sala ya tiene una reserva confirmada en ese horario"
                );
            }
        }
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> listarPorUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new RecursoNoEncontradoException(
                    "No existe el usuario con ID " + usuarioId
            );
        }

        return reservaRepository.findByUsuario_Id(usuarioId)
                .stream()
                .map(ReservaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> listarPorSala(Long salaId) {
        if (!salaRepository.existsById(salaId)) {
            throw new RecursoNoEncontradoException(
                    "No existe la sala con ID " + salaId
            );
        }

        return reservaRepository.findBySala_Id(salaId)
                .stream()
                .map(ReservaMapper::toResponse)
                .toList();
    }
}