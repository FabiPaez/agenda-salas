package com.agendasalas.mapper;

import com.agendasalas.dto.UsuarioRequest;
import com.agendasalas.dto.UsuarioResponse;
import com.agendasalas.entity.Usuario;

public class UsuarioMapper {

    private UsuarioMapper() {
    }

    public static Usuario toEntity(UsuarioRequest request) {
        Usuario usuario = new Usuario();

        usuario.setNombre(request.nombre().trim());
        usuario.setApellido(request.apellido().trim());
        usuario.setEmail(request.email().trim());
        usuario.setContrasena(request.contrasena());
        usuario.setTipo(request.tipo());

        return usuario;
    }

    public static UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getTipo()
        );
    }
}