package com.agendasalas.mapper;

import com.agendasalas.dto.ReservaRequest;
import com.agendasalas.dto.ReservaResponse;
import com.agendasalas.entity.Reserva;
import com.agendasalas.entity.Sala;
import com.agendasalas.entity.Usuario;

public class ReservaMapper {

    private ReservaMapper() {
    }

    public static Reserva toEntity(
            ReservaRequest request,
            Usuario usuario,
            Sala sala) {

        Reserva reserva = new Reserva();

        reserva.setFecha(request.fecha());
        reserva.setHoraInicio(request.horaInicio());
        reserva.setHoraFin(request.horaFin());
        reserva.setUsuario(usuario);
        reserva.setSala(sala);
        reserva.setEstado(request.estado());

        return reserva;
    }

    public static ReservaResponse toResponse(Reserva reserva) {
        return new ReservaResponse(
                reserva.getId(),
                reserva.getFecha(),
                reserva.getHoraInicio(),
                reserva.getHoraFin(),
                reserva.getUsuario().getId(),
                reserva.getSala().getId(),
                reserva.getEstado()
        );
    }
}