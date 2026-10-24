package com.agendasalas.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE)
    public String inicio() {
        return """
                ========================================
                     AGENDA DE SALAS
                     API REST - Gestión de reservas
                ========================================

                Bienvenido a la API REST de Agenda de Salas.

                Permite consultar salas, administrar usuarios
                y gestionar reservas de salas de cine.

                DOCUMENTACIÓN INTERACTIVA
                Swagger UI:
                /swagger-ui/index.html

                Especificación OpenAPI:
                /v3/api-docs

                RECURSOS DISPONIBLES

                USUARIOS
                GET     /api/usuarios
                GET     /api/usuarios/{id}
                POST    /api/usuarios
                PUT     /api/usuarios/{id}
                DELETE  /api/usuarios/{id}

                REGISTRO
                POST    /api/auth/registro

                SALAS
                GET     /api/salas
                GET     /api/salas/{id}
                POST    /api/salas
                PUT     /api/salas/{id}
                DELETE  /api/salas/{id}

                RESERVAS
                GET     /api/reservas
                GET     /api/reservas/{id}
                GET     /api/reservas/usuario/{usuarioId}
                GET     /api/reservas/sala/{salaId}
                POST    /api/reservas
                PUT     /api/reservas/{id}
                DELETE  /api/reservas/{id}

                AUTENTICACIÓN Y PERMISOS

                - El registro público crea usuarios CLIENTE.
                - Las operaciones protegidas utilizan HTTP Basic.
                - Los permisos dependen del rol del usuario.
                - Las operaciones administrativas requieren ADMIN.

                REGLAS DE RESERVAS

                - La hora de inicio debe ser anterior a la hora de fin.
                - Las reservas confirmadas de una misma sala
                  no pueden superponerse en fecha y horario.
                - Las validaciones definitivas se realizan en el backend.

                NOTAS

                - POST crea un recurso.
                - PUT actualiza un recurso.
                - DELETE elimina un recurso.
                - Las solicitudes con cuerpo deben enviar JSON.

                BASE URL DE PRODUCCIÓN
                https://agenda-salas.onrender.com

                Nota: el servicio gratuito de Render puede tardar
                en responder cuando se inicia después de un período
                de inactividad.

                ========================================
                """;
    }
}