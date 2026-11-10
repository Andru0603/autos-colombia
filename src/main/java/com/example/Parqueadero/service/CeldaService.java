package com.example.Parqueadero.service;

import com.example.Parqueadero.model.entity.Celda;

import java.util.List;
import java.util.Map;

public interface CeldaService {

    Celda crear(Integer idSolicitante, String numero, String tipo); // HU11

    List<Celda> listar(String tipo, String estado); // HU12

    Map<String, Map<String, Long>> resumenOcupacion(); // HU12

    Celda editar(Integer idSolicitante, Integer idCelda, String numero, String tipo); // HU13

    Celda cambiarEstado(Integer idSolicitante, Integer idCelda, String estado); // HU14
}
