package com.example.Parqueadero.service.impl;

import com.example.Parqueadero.model.entity.Tarifa;
import com.example.Parqueadero.repository.TarifaRepository;
import com.example.Parqueadero.service.TarifaService;
import com.example.Parqueadero.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class TarifaServiceImpl implements TarifaService {

    private static final BigDecimal VALOR_MAXIMO = new BigDecimal("99999999.99");

    @Autowired
    private TarifaRepository tarifaRepo;
    @Autowired
    private UsuarioService usuarioService;

    // ---------- HU15: consultar tarifas ----------
    @Override
    public List<Tarifa> listar(Integer idSolicitante) {
        usuarioService.validarUsuarioActivo(idSolicitante);
        List<Tarifa> tarifas = new ArrayList<>(tarifaRepo.findAll());
        tarifas.sort(Comparator.comparing(Tarifa::getTipo));
        return tarifas;
    }

    // ---------- HU15: actualizar tarifa (solo administrador) ----------
    @Override
    @Transactional
    public Tarifa actualizar(Integer idSolicitante, String tipo, BigDecimal valorMensual) {
        usuarioService.validarAdministrador(idSolicitante);

        String t = (tipo == null) ? "" : tipo.trim().toUpperCase();
        if (!t.equals("CARRO") && !t.equals("MOTO")) {
            throw new RuntimeException("Tipo inválido. Use CARRO o MOTO");
        }
        if (valorMensual == null || valorMensual.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El valor mensual debe ser mayor que 0");
        }
        if (valorMensual.compareTo(VALOR_MAXIMO) > 0) {
            throw new RuntimeException("El valor mensual es demasiado alto");
        }

        Tarifa tarifa = tarifaRepo.findByTipo(t)
                .orElseThrow(() -> new RuntimeException("No existe tarifa para el tipo " + t));
        tarifa.setValorMensual(valorMensual.setScale(2, RoundingMode.HALF_UP));
        return tarifaRepo.save(tarifa);
    }
}
