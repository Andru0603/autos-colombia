package com.example.Parqueadero.controller;

import com.example.Parqueadero.model.entity.Tarifa;
import com.example.Parqueadero.service.TarifaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Tarifas mensuales (Iteración 3). Consultar: cualquier usuario activo.
 * Actualizar: solo administrador. El front envía el header X-Usuario-Id.
 */
@RestController
@RequestMapping("/tarifas")
@CrossOrigin("*")
public class TarifaController {

    @Autowired
    private TarifaService tarifaService;

    public record TarifaRequest(BigDecimal valorMensual) {
    }

    @GetMapping
    public List<Tarifa> listar(@RequestHeader(value = "X-Usuario-Id", required = false) Integer idSolicitante) {
        return tarifaService.listar(idSolicitante);
    }

    @PutMapping("/{tipo}")
    public Tarifa actualizar(@RequestHeader(value = "X-Usuario-Id", required = false) Integer idSolicitante,
            @PathVariable String tipo,
            @RequestBody TarifaRequest req) {
        return tarifaService.actualizar(idSolicitante, tipo, req.valorMensual());
    }
}
