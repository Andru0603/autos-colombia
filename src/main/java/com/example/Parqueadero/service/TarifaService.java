package com.example.Parqueadero.service;

import com.example.Parqueadero.model.entity.Tarifa;

import java.math.BigDecimal;
import java.util.List;

public interface TarifaService {

    List<Tarifa> listar(Integer idSolicitante); // HU15 (cualquier usuario activo)

    Tarifa actualizar(Integer idSolicitante, String tipo, BigDecimal valorMensual); // HU15 (solo administrador)
}
