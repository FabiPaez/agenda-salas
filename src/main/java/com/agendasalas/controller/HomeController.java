package com.agendasalas.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE)
    public String inicio() {
        return """
                AGENDA DE SALAS
                API REST - Gestion de reservas de salas de cine

                BASE URL:
                https://agenda-salas.onrender.com (la versión gratuita de render suele demorarse cuando arranca en frio)

                USUARIOS
                GET    /api/usuarios
                GET    /api/usuarios/{id}
                POST   /api/usuarios
                PUT    /api/usuarios/{id}
                DELETE /api/usuarios/{id}

                SALAS
                GET    /api/salas
                GET    /api/salas/{id}
                POST   /api/salas
                PUT    /api/salas/{id}
                DELETE /api/salas/{id}

                RESERVAS
                GET    /api/reservas
                GET    /api/reservas/{id}
                GET    /api/reservas/usuario/{usuarioId}
                GET    /api/reservas/sala/{salaId}
                POST   /api/reservas
                PUT    /api/reservas/{id}
                DELETE /api/reservas/{id}

                NOTAS
                - POST crea un recurso.
                - PUT actualiza un recurso completo.
                - DELETE elimina un recurso.
                - Los POST y PUT requieren JSON.
                - Las reservas confirmadas no pueden superponerse
                  en una misma sala, fecha y horario.
                """;
    }
}