package com.example.Parqueadero.controller;

import com.example.Parqueadero.model.entity.Novedad;
import com.example.Parqueadero.model.entity.Registro;
import com.example.Parqueadero.service.RegistroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/registro")
@CrossOrigin("*")
public class RegistroController {

    @Autowired
    private RegistroService registroService;

    public record EntradaRequest(String placa, Integer idUsuario) {
    }

    public record NovedadRequest(String placa, String descripcion) {
    }

    @PostMapping("/entrada")
    public Registro entrada(@RequestBody EntradaRequest req) {
        return registroService.registrarEntrada(req.placa(), req.idUsuario());
    }

    @GetMapping("/activo/{placa}")
    public Registro activo(@PathVariable String placa) {
        return registroService.buscarActivo(placa);
    }

    @PutMapping("/salida/{placa}")
    public Registro salida(@PathVariable String placa) {
        return registroService.registrarSalida(placa);
    }

    @GetMapping("/dentro")
    public List<Registro> dentro(@RequestParam(required = false) String placa) {
        return registroService.listarDentro(placa);
    }

    @GetMapping("/historial")
    public List<Registro> historial(
            @RequestParam(required = false) String placa,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return registroService.consultarHistorial(placa, desde, hasta);
    }

    @PostMapping("/novedad")
    public Novedad novedad(@RequestBody NovedadRequest req) {
        return registroService.registrarNovedad(req.placa(), req.descripcion());
    }
}
