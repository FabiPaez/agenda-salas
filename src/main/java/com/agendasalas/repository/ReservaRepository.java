package com.agendasalas.repository;

import com.agendasalas.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import com.agendasalas.entity.EstadoReserva;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    @Query("""
    SELECT COUNT(r)
    FROM Reserva r
    WHERE r.sala.id = :salaId
      AND r.fecha = :fecha
      AND r.estado = :estado
      AND r.horaInicio < :horaFin
      AND r.horaFin > :horaInicio
      AND (:reservaId IS NULL OR r.id <> :reservaId)
    """)
    long contarSuperposiciones(
            @Param("salaId") Long salaId,
            @Param("fecha") LocalDate fecha,
            @Param("estado") EstadoReserva estado,
            @Param("horaFin") LocalTime horaFin,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("reservaId") Long reservaId
    );
}