package com.example.Parqueadero.service;

import com.example.Parqueadero.model.entity.Novedad;
import com.example.Parqueadero.model.entity.Registro;

import java.time.LocalDate;
import java.util.List;

public interface RegistroService {

    Registro registrarEntrada(String placa, int idUsuario); // HU02

    Registro buscarActivo(String placa); // HU03 (buscar antes de confirmar)

    Registro registrarSalida(String placa); // HU03

    List<Registro> listarDentro(String placa); // HU04

    List<Registro> consultarHistorial(String placa, LocalDate desde, LocalDate hasta); // HU05

    Novedad registrarNovedad(String placa, String descripcion); // HU06
}
