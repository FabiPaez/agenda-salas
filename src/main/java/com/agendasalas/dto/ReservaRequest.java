package com.agendasalas.dto;

import com.agendasalas.entity.EstadoReserva;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.FutureOrPresent;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservaRequest(

        @NotNull(message = "La fecha es obligatoria")
        @FutureOrPresent(message = "La fecha no puede ser anterior a hoy")
        LocalDate fecha,

        @NotNull(message = "La hora de inicio es obligatoria")
        LocalTime horaInicio,

        @NotNull(message = "La hora de fin es obligatoria")
        LocalTime horaFin,

        @NotNull(message = "El ID del usuario es obligatorio")
        @Positive(message = "El ID del usuario debe ser positivo")
        Long usuarioId,

        @NotNull(message = "El ID de la sala es obligatorio")
        @Positive(message = "El ID de la sala debe ser positivo")
        Long salaId,

        @NotNull(message = "El estado de la reserva es obligatorio")
        EstadoReserva estado
) {}
