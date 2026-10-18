package com.agendasalas.service;

import com.agendasalas.entity.EstadoReserva;
import com.agendasalas.entity.Reserva;
import com.agendasalas.entity.Sala;
import com.agendasalas.entity.Usuario;
import com.agendasalas.repository.ReservaRepository;
import com.agendasalas.repository.SalaRepository;
import com.agendasalas.repository.UsuarioRepository;
import com.agendasalas.dto.ReservaRequest;
import com.agendasalas.dto.ReservaResponse;
import com.agendasalas.mapper.ReservaMapper;
import com.agendasalas.exception.ConflictoException;
import com.agendasalas.exception.RecursoNoEncontradoException;

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
    public List<Reserva> listarTodas() {
        return reservaRepository.findAll();
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
        validarReserva(reserva, null);
        return reservaRepository.save(reserva);
    }

    @Transactional
    public Reserva actualizar(Long id, Reserva datos) {
        Reserva existente = buscarPorId(id);

        datos.setUsuario(existente.getUsuario());
        datos.setSala(existente.getSala());

        validarReserva(datos, id);

        existente.setFecha(datos.getFecha());
        existente.setHoraInicio(datos.getHoraInicio());
        existente.setHoraFin(datos.getHoraFin());
        existente.setEstado(datos.getEstado());

        return reservaRepository.save(existente);
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
                        ));

        Sala sala = salaRepository.findById(request.salaId())
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe la sala indicada"
                        ));

        Reserva reserva = ReservaMapper.toEntity(request, usuario, sala);

        return ReservaMapper.toResponse(crear(reserva));
    }

    @Transactional
    public ReservaResponse actualizarDesdeRequest(
            Long id, ReservaRequest request) {
        buscarPorId(id);

        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe el usuario indicado"
                        ));

        Sala sala = salaRepository.findById(request.salaId())
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe la sala indicada"
                        ));

        Reserva datos = ReservaMapper.toEntity(request, usuario, sala);
        validarReserva(datos, id);

        Reserva existente = buscarPorId(id);
        existente.setFecha(datos.getFecha());
        existente.setHoraInicio(datos.getHoraInicio());
        existente.setHoraFin(datos.getHoraFin());
        existente.setUsuario(usuario);
        existente.setSala(sala);
        existente.setEstado(datos.getEstado());

        Reserva guardada = reservaRepository.save(existente);
        return ReservaMapper.toResponse(guardada);
    }

    private void validarReserva(Reserva reserva, Long reservaId) {

        if (reserva.getFecha() == null
                || reserva.getHoraInicio() == null
                || reserva.getHoraFin() == null
                || reserva.getEstado() == null
                || reserva.getSala() == null
                || reserva.getUsuario() == null) {
            throw new IllegalArgumentException(
                    "Todos los datos de la reserva son obligatorios");
        }

        if (!reserva.getHoraInicio().isBefore(reserva.getHoraFin())) {
            throw new IllegalArgumentException(
                    "La hora de inicio debe ser anterior a la hora de fin");
        }

        Long salaId = reserva.getSala().getId();
        Long usuarioId = reserva.getUsuario().getId();

        if (salaId == null || !salaRepository.existsById(salaId)) {
            throw new IllegalArgumentException(
                    "La sala indicada no existe");
        }

        if (usuarioId == null || !usuarioRepository.existsById(usuarioId)) {
            throw new IllegalArgumentException(
                    "El usuario indicado no existe");
        }

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
}