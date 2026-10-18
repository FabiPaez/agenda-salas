package com.agendasalas.service;

import com.agendasalas.entity.Sala;
import com.agendasalas.repository.SalaRepository;
import com.agendasalas.dto.SalaRequest;
import com.agendasalas.dto.SalaResponse;
import com.agendasalas.mapper.SalaMapper;
import com.agendasalas.exception.ConflictoException;
import com.agendasalas.exception.RecursoNoEncontradoException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SalaService {

    private final SalaRepository salaRepository;

    public SalaService(SalaRepository salaRepository) {
        this.salaRepository = salaRepository;
    }

    @Transactional(readOnly = true)
    public List<Sala> listarTodas() {
        return salaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Sala buscarPorId(Long id) {
        return salaRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe la sala con ID " + id
                        )
                );
    }

    @Transactional
    public Sala crear(Sala sala) {
        validarSala(sala, null);
        return salaRepository.save(sala);
    }

    @Transactional
    public Sala actualizar(Long id, Sala datos) {
        Sala existente = buscarPorId(id);

        validarSala(datos, id);

        existente.setNumero(datos.getNumero());
        existente.setCapacidad(datos.getCapacidad());
        existente.setEstado(datos.getEstado());

        return salaRepository.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {
        Sala sala = buscarPorId(id);
        salaRepository.delete(sala);
    }

    @Transactional(readOnly = true)
    public List<SalaResponse> listarRespuestas() {
        return salaRepository.findAll()
                .stream()
                .map(SalaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SalaResponse buscarRespuestaPorId(Long id) {
        return SalaMapper.toResponse(buscarPorId(id));
    }

    @Transactional
    public SalaResponse crearDesdeRequest(SalaRequest request) {
        Sala sala = SalaMapper.toEntity(request);
        return SalaMapper.toResponse(crear(sala));
    }

    @Transactional
    public SalaResponse actualizarDesdeRequest(
            Long id, SalaRequest request) {
        Sala datos = SalaMapper.toEntity(request);
        return SalaMapper.toResponse(actualizar(id, datos));
    }

    private void validarSala(Sala sala, Long salaId) {
        if (sala.getNumero() == null
                || sala.getCapacidad() == null
                || sala.getEstado() == null) {
            throw new IllegalArgumentException(
                    "El número, la capacidad y el estado son obligatorios");
        }

        if (sala.getNumero() <= 0) {
            throw new IllegalArgumentException(
                    "El número de sala debe ser mayor que cero");
        }

        if (sala.getCapacidad() <= 0) {
            throw new IllegalArgumentException(
                    "La capacidad debe ser mayor que cero");
        }

        salaRepository.findByNumero(sala.getNumero())
                .ifPresent(otraSala -> {
                    if (!otraSala.getId().equals(salaId)) {
                        throw new ConflictoException(
                                "Ya existe una sala con ese número"
                        );
                    }
                });
    }
}