package com.agendasalas.controller;

import com.agendasalas.dto.ReservaRequest;
import com.agendasalas.dto.ReservaResponse;
import com.agendasalas.service.ReservaService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    public List<ReservaResponse> listar() {
        return reservaService.listarRespuestas();
    }

    @GetMapping("/{id}")
    public ReservaResponse buscar(@PathVariable Long id) {
        return reservaService.buscarRespuestaPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservaResponse crear(
            @Valid @RequestBody ReservaRequest request) {
        return reservaService.crearDesdeRequest(request);
    }

    @PutMapping("/{id}")
    public ReservaResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ReservaRequest request) {
        return reservaService.actualizarDesdeRequest(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        reservaService.eliminar(id);
    }
}