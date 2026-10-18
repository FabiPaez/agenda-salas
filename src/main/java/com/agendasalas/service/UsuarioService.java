package com.agendasalas.service;

import com.agendasalas.entity.Usuario;
import com.agendasalas.repository.UsuarioRepository;
import com.agendasalas.dto.UsuarioRequest;
import com.agendasalas.dto.UsuarioResponse;
import com.agendasalas.mapper.UsuarioMapper;
import com.agendasalas.exception.ConflictoException;
import com.agendasalas.exception.RecursoNoEncontradoException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe el usuario con ID " + id
                        )
                );
    }

    @Transactional
    public Usuario crear(Usuario usuario) {
        validarUsuario(usuario, null);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario actualizar(Long id, Usuario datos) {
        Usuario existente = buscarPorId(id);

        validarUsuario(datos, id);

        existente.setNombre(datos.getNombre().trim());
        existente.setApellido(datos.getApellido().trim());
        existente.setEmail(datos.getEmail().trim());
        existente.setContrasena(datos.getContrasena());
        existente.setTipo(datos.getTipo());

        return usuarioRepository.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuarioRepository.delete(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarRespuestas() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarRespuestaPorId(Long id) {
        return UsuarioMapper.toResponse(buscarPorId(id));
    }

    @Transactional
    public UsuarioResponse crearDesdeRequest(UsuarioRequest request) {
        Usuario usuario = UsuarioMapper.toEntity(request);
        return UsuarioMapper.toResponse(crear(usuario));
    }

    @Transactional
    public UsuarioResponse actualizarDesdeRequest(
            Long id, UsuarioRequest request) {
        Usuario datos = UsuarioMapper.toEntity(request);
        return UsuarioMapper.toResponse(actualizar(id, datos));
    }

    private void validarUsuario(Usuario usuario, Long usuarioId) {
        if (usuario.getNombre() == null
                || usuario.getNombre().isBlank()
                || usuario.getApellido() == null
                || usuario.getApellido().isBlank()
                || usuario.getEmail() == null
                || usuario.getEmail().isBlank()
                || usuario.getContrasena() == null
                || usuario.getContrasena().isBlank()
                || usuario.getTipo() == null) {
            throw new IllegalArgumentException(
                    "Todos los campos del usuario son obligatorios");
        }

        String email = usuario.getEmail().trim();

        usuarioRepository.findByEmail(email)
                .ifPresent(otroUsuario -> {
                    if (!otroUsuario.getId().equals(usuarioId)) {
                        throw new ConflictoException(
                                "Ya existe un usuario registrado con ese correo electrónico"
                        );
                    }
                });
    }
}