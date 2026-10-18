package com.agendasalas.dto;

import com.agendasalas.entity.EstadoSala;

public record SalaResponse(
        Long id,
        Integer numero,
        Integer capacidad,
        EstadoSala estado
) {}
