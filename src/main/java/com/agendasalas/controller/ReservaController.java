package com.agendasalas.controller;

import com.agendasalas.dto.ReservaRequest;
import com.agendasalas.dto.ReservaResponse;
import com.agendasalas.service.ReservaService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public ResponseEntity<List<ReservaResponse>> listar() {
        return ResponseEntity.ok(reservaService.listarRespuestas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.buscarRespuestaPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ReservaResponse>> listarPorUsuario(
            @PathVariable Long usuarioId) {

        return ResponseEntity.ok(
                reservaService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/sala/{salaId}")
    public ResponseEntity<List<ReservaResponse>> listarPorSala(
            @PathVariable Long salaId) {

        return ResponseEntity.ok(
                reservaService.listarPorSala(salaId));
    }

    @PostMapping
    public ResponseEntity<ReservaResponse> crear(
            @Valid @RequestBody ReservaRequest request) {

        ReservaResponse respuesta =
                reservaService.crearDesdeRequest(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(respuesta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ReservaRequest request) {

        ReservaResponse respuesta =
                reservaService.actualizarDesdeRequest(id, request);

        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        reservaService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}