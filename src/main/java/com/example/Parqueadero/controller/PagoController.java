package com.example.Parqueadero.controller;

import com.example.Parqueadero.model.entity.Pago;
import com.example.Parqueadero.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Gestión de pagos de mensualidad (Iteración 3).
 * Todos los endpoints exigen el header X-Usuario-Id de un usuario activo;
 * el recaudo además exige que sea administrador.
 */
@RestController
@RequestMapping("/pagos")
@CrossOrigin("*")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    // El monto NO viene en la petición: lo calcula el sistema
    public record PagoRequest(String documento, Integer meses, String metodoPago) {
    }

    @GetMapping("/mensualidades")
    public List<Map<String, Object>> mensualidades(
            @RequestHeader(value = "X-Usuario-Id", required = false) Integer idSolicitante,
            @RequestParam(required = false) String estado) {
        return pagoService.estadoMensualidades(idSolicitante, estado);
    }

    @GetMapping("/calcular")
    public Map<String, Object> calcular(
            @RequestHeader(value = "X-Usuario-Id", required = false) Integer idSolicitante,
            @RequestParam(required = false) String documento,
            @RequestParam(required = false) Integer meses) {
        return pagoService.calcular(idSolicitante, documento, meses);
    }

    @PostMapping
    public Pago registrar(@RequestHeader(value = "X-Usuario-Id", required = false) Integer idSolicitante,
            @RequestBody PagoRequest req) {
        return pagoService.registrar(idSolicitante, req.documento(), req.meses(), req.metodoPago());
    }

    @GetMapping("/{id}/comprobante")
    public Map<String, Object> comprobante(
            @RequestHeader(value = "X-Usuario-Id", required = false) Integer idSolicitante,
            @PathVariable Integer id) {
        return pagoService.comprobante(idSolicitante, id);
    }

    @GetMapping
    public List<Pago> historial(@RequestHeader(value = "X-Usuario-Id", required = false) Integer idSolicitante,
            @RequestParam(required = false) String documento,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return pagoService.historial(idSolicitante, documento, desde, hasta);
    }

    @GetMapping("/recaudo")
    public Map<String, Object> recaudo(@RequestHeader(value = "X-Usuario-Id", required = false) Integer idSolicitante,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return pagoService.recaudo(idSolicitante, desde, hasta);
    }
}
