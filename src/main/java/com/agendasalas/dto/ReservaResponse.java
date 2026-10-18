package com.agendasalas.dto;

import com.agendasalas.entity.EstadoReserva;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservaResponse(
        Long id,
        LocalDate fecha,
        LocalTime horaInicio,
        LocalTime horaFin,
        Long usuarioId,
        Long salaId,
        EstadoReserva estado
) {}
