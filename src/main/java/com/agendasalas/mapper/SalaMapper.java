package com.agendasalas.mapper;

import com.agendasalas.dto.SalaRequest;
import com.agendasalas.dto.SalaResponse;
import com.agendasalas.entity.Sala;

public class SalaMapper {

    private SalaMapper() {
    }

    public static Sala toEntity(SalaRequest request) {
        Sala sala = new Sala();

        sala.setNumero(request.numero());
        sala.setCapacidad(request.capacidad());
        sala.setEstado(request.estado());

        return sala;
    }

    public static SalaResponse toResponse(Sala sala) {
        return new SalaResponse(
                sala.getId(),
                sala.getNumero(),
                sala.getCapacidad(),
                sala.getEstado()
        );
    }
}