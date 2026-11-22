package com.example.Parqueadero.service;

import com.example.Parqueadero.model.entity.Pago;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface PagoService {

    List<Map<String, Object>> estadoMensualidades(Integer idSolicitante, String estado); // HU16

    Map<String, Object> calcular(Integer idSolicitante, String documento, Integer meses); // HU17

    Pago registrar(Integer idSolicitante, String documento, Integer meses, String metodoPago); // HU18

    Map<String, Object> comprobante(Integer idSolicitante, Integer idPago); // HU19

    List<Pago> historial(Integer idSolicitante, String documento, LocalDate desde, LocalDate hasta); // HU20

    Map<String, Object> recaudo(Integer idSolicitante, LocalDate desde, LocalDate hasta); // HU21 (solo administrador)
}
