package com.agendasalas.dto;

import com.agendasalas.entity.EstadoSala;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SalaRequest(
        @NotNull(message = "El número de sala es obligatorio")
        @Positive(message = "El número debe ser mayor que cero")
        Integer numero,

        @NotNull(message = "La capacidad es obligatoria")
        @Positive(message = "La capacidad debe ser mayor que cero")
        Integer capacidad,

        @NotNull(message = "El estado es obligatorio")
        EstadoSala estado
) {}
