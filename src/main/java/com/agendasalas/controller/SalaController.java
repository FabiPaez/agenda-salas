package com.agendasalas.controller;

import com.agendasalas.dto.SalaRequest;
import com.agendasalas.dto.SalaResponse;
import com.agendasalas.service.SalaService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<SalaResponse>> listar() {
        return ResponseEntity.ok(salaService.listarRespuestas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalaResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(salaService.buscarRespuestaPorId(id));
    }

    @PostMapping
    public ResponseEntity<SalaResponse> crear(
            @Valid @RequestBody SalaRequest request) {

        SalaResponse respuesta = salaService.crearDesdeRequest(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(respuesta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalaResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody SalaRequest request) {

        SalaResponse respuesta =
                salaService.actualizarDesdeRequest(id, request);

        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        salaService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}