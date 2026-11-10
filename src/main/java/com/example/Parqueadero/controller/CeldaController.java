package com.example.Parqueadero.controller;

import com.example.Parqueadero.model.entity.Celda;
import com.example.Parqueadero.service.CeldaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Gestión de celdas (Iteración 2). Crear, editar y cambiar estado: solo administrador.
 * Consultar: cualquier usuario autenticado.
 */
@RestController
@RequestMapping("/celdas")
@CrossOrigin("*")
public class CeldaController {

    @Autowired
    private CeldaService celdaService;

    public record CeldaRequest(String numero, String tipo) {
    }

    public record EstadoCeldaRequest(String estado) {
    }

    @PostMapping
    public Celda crear(@RequestHeader(value = "X-Usuario-Id", required = false) Integer idSolicitante,
            @RequestBody CeldaRequest req) {
        return celdaService.crear(idSolicitante, req.numero(), req.tipo());
    }

    @GetMapping
    public List<Celda> listar(@RequestParam(required = false) String tipo,
            @RequestParam(required = false) String estado) {
        return celdaService.listar(tipo, estado);
    }

    @GetMapping("/resumen")
    public Map<String, Map<String, Long>> resumen() {
        return celdaService.resumenOcupacion();
    }

    @PutMapping("/{id}")
    public Celda editar(@RequestHeader(value = "X-Usuario-Id", required = false) Integer idSolicitante,
            @PathVariable Integer id,
            @RequestBody CeldaRequest req) {
        return celdaService.editar(idSolicitante, id, req.numero(), req.tipo());
    }

    @PutMapping("/{id}/estado")
    public Celda cambiarEstado(@RequestHeader(value = "X-Usuario-Id", required = false) Integer idSolicitante,
            @PathVariable Integer id,
            @RequestBody EstadoCeldaRequest req) {
        return celdaService.cambiarEstado(idSolicitante, id, req.estado());
    }
}
