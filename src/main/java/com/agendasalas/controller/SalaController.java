package com.agendasalas.controller;

import com.agendasalas.dto.SalaRequest;
import com.agendasalas.dto.SalaResponse;
import com.agendasalas.service.SalaService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salas")
public class SalaController {

    private final SalaService salaService;

    public SalaController(SalaService salaService) {
        this.salaService = salaService;
    }

    @GetMapping
    public List<SalaResponse> listar() {
        return salaService.listarRespuestas();
    }

    @GetMapping("/{id}")
    public SalaResponse buscar(@PathVariable Long id) {
        return salaService.buscarRespuestaPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SalaResponse crear(@Valid @RequestBody SalaRequest request) {
        return salaService.crearDesdeRequest(request);
    }

    @PutMapping("/{id}")
    public SalaResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody SalaRequest request) {
        return salaService.actualizarDesdeRequest(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        salaService.eliminar(id);
    }
}