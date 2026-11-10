package com.example.Parqueadero.service.impl;

import com.example.Parqueadero.model.entity.Celda;
import com.example.Parqueadero.repository.CeldaRepository;
import com.example.Parqueadero.service.CeldaService;
import com.example.Parqueadero.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CeldaServiceImpl implements CeldaService {

    private static final List<String> TIPOS = List.of("CARRO", "MOTO");
    private static final List<String> ESTADOS = List.of("LIBRE", "OCUPADA", "MANTENIMIENTO");

    @Autowired
    private CeldaRepository celdaRepo;
    @Autowired
    private UsuarioService usuarioService;

    // ---------- HU11: registrar celda ----------
    @Override
    @Transactional
    public Celda crear(Integer idSolicitante, String numero, String tipo) {
        usuarioService.validarAdministrador(idSolicitante);
        String num = normalizarNumero(numero);
        String tipoNormalizado = normalizarTipo(tipo);

        if (celdaRepo.existsByNumero(num)) {
            throw new RuntimeException("Ya existe una celda con el número " + num);
        }

        Celda celda = new Celda();
        celda.setNumero(num);
        celda.setTipo(tipoNormalizado);
        celda.setEstado("LIBRE"); // toda celda nueva nace libre
        return celdaRepo.save(celda);
    }

    // ---------- HU12: consultar celdas ----------
    @Override
    public List<Celda> listar(String tipo, String estado) {
        String filtroTipo = "%";
        if (tipo != null && !tipo.isBlank()) {
            filtroTipo = normalizarTipo(tipo);
        }
        String filtroEstado = "%";
        if (estado != null && !estado.isBlank()) {
            filtroEstado = estado.trim().toUpperCase();
            if (!ESTADOS.contains(filtroEstado)) {
                throw new RuntimeException("Estado inválido. Use LIBRE, OCUPADA o MANTENIMIENTO");
            }
        }
        return celdaRepo.filtrar(filtroTipo, filtroEstado);
    }

    @Override
    public Map<String, Map<String, Long>> resumenOcupacion() {
        Map<String, Map<String, Long>> resumen = new LinkedHashMap<>();
        for (String tipo : TIPOS) {
            Map<String, Long> porEstado = new LinkedHashMap<>();
            for (String estado : ESTADOS) {
                porEstado.put(estado, celdaRepo.countByTipoAndEstado(tipo, estado));
            }
            resumen.put(tipo, porEstado);
        }
        return resumen;
    }

    // ---------- HU13: editar celda ----------
    @Override
    @Transactional
    public Celda editar(Integer idSolicitante, Integer idCelda, String numero, String tipo) {
        usuarioService.validarAdministrador(idSolicitante);
        Celda celda = buscarPorId(idCelda);

        // Una celda con un vehículo dentro no se modifica
        if ("OCUPADA".equals(celda.getEstado())) {
            throw new RuntimeException("No se puede editar una celda ocupada");
        }

        String num = normalizarNumero(numero);
        String tipoNormalizado = normalizarTipo(tipo);
        if (celdaRepo.existsByNumeroAndIdCeldaNot(num, idCelda)) {
            throw new RuntimeException("Ya existe una celda con el número " + num);
        }

        celda.setNumero(num);
        celda.setTipo(tipoNormalizado);
        return celdaRepo.save(celda);
    }

    // ---------- HU14: habilitar / deshabilitar celda ----------
    @Override
    @Transactional
    public Celda cambiarEstado(Integer idSolicitante, Integer idCelda, String estado) {
        usuarioService.validarAdministrador(idSolicitante);
        Celda celda = buscarPorId(idCelda);
        String nuevoEstado = (estado == null) ? "" : estado.trim().toUpperCase();

        if (!nuevoEstado.equals("LIBRE") && !nuevoEstado.equals("MANTENIMIENTO")) {
            throw new RuntimeException("Solo se puede cambiar a LIBRE o MANTENIMIENTO");
        }
        // OCUPADA solo la manejan las entradas y salidas de vehículos
        if ("OCUPADA".equals(celda.getEstado())) {
            throw new RuntimeException("No se puede cambiar el estado de una celda ocupada");
        }
        celda.setEstado(nuevoEstado);
        return celdaRepo.save(celda);
    }

    // ---------- métodos privados ----------
    private Celda buscarPorId(Integer idCelda) {
        return celdaRepo.findById(idCelda)
                .orElseThrow(() -> new RuntimeException("Celda no encontrada"));
    }

    private String normalizarNumero(String numero) {
        if (numero == null || numero.isBlank()) {
            throw new RuntimeException("El número de celda es obligatorio");
        }
        String n = numero.trim().toUpperCase();
        if (n.length() > 10) {
            throw new RuntimeException("El número de celda no puede superar 10 caracteres");
        }
        return n;
    }

    private String normalizarTipo(String tipo) {
        String t = (tipo == null) ? "" : tipo.trim().toUpperCase();
        if (!TIPOS.contains(t)) {
            throw new RuntimeException("Tipo inválido. Use CARRO o MOTO");
        }
        return t;
    }
}
