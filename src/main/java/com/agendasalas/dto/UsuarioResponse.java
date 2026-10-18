package com.agendasalas.dto;

import com.agendasalas.entity.TipoUsuario;

public record UsuarioResponse(
        Long id,
        String nombre,
        String apellido,
        String email,
        TipoUsuario tipo
) {}
