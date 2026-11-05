package com.example.Parqueadero.service.impl;

import com.example.Parqueadero.model.entity.*;
import com.example.Parqueadero.repository.*;
import com.example.Parqueadero.service.RegistroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class RegistroServiceImpl implements RegistroService {

    // Carro: ABC123 | Moto: ABC12D
    private static final Pattern FORMATO_PLACA = Pattern.compile("^[A-Z]{3}[0-9]{3}$|^[A-Z]{3}[0-9]{2}[A-Z]$");

    @Autowired
    private RegistroRepository registroRepo;
    @Autowired
    private VehiculoRepository vehiculoRepo;
    @Autowired
    private CeldaRepository celdaRepo;
    @Autowired
    private UsuarioRepository usuarioRepo;
    @Autowired
    private NovedadRepository novedadRepo;

    // ---------- HU02: registrar entrada ----------
    @Override
    @Transactional
    public Registro registrarEntrada(String placa, int idUsuario) {
        String p = normalizar(placa);

        if (!FORMATO_PLACA.matcher(p).matches()) {
            throw new RuntimeException("Formato de placa inválido. Ejemplo: ABC123");
        }
        if (registroRepo.findByVehiculo_PlacaAndFechaSalidaIsNull(p).isPresent()) {
            throw new RuntimeException("Esta placa ya tiene una entrada activa");
        }
        if (!validarMensualidad(p)) {
            throw new RuntimeException("La placa no tiene mensualidad vigente. No se registró la entrada.");
        }

        Vehiculo vehiculo = vehiculoRepo.findByPlaca(p).orElseThrow();
        Usuario usuario = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Celda celda = asignarCelda(vehiculo.getTipo());

        Registro registro = new Registro();
        registro.setVehiculo(vehiculo);
        registro.setCelda(celda);
        registro.setUsuario(usuario);
        registro.setFechaEntrada(LocalDateTime.now());
        return registroRepo.save(registro);
    }

    // ---------- HU03: salida ----------
    @Override
    public Registro buscarActivo(String placa) {
        return registroRepo.findByVehiculo_PlacaAndFechaSalidaIsNull(normalizar(placa))
                .orElseThrow(() -> new RuntimeException("La placa no tiene una entrada activa"));
    }

    @Override
    @Transactional
    public Registro registrarSalida(String placa) {
        Registro registro = buscarActivo(placa);
        registro.setFechaSalida(LocalDateTime.now());

        Celda celda = registro.getCelda();
        celda.setEstado("LIBRE");
        celdaRepo.save(celda);

        return registroRepo.save(registro);
    }

    // ---------- HU04: vehículos dentro ----------
    @Override
    public List<Registro> listarDentro(String placa) {
        return registroRepo.findByFechaSalidaIsNullAndVehiculo_PlacaContainingIgnoreCase(normalizar(placa));
    }

    // ---------- HU05: historial ----------
    @Override
    public List<Registro> consultarHistorial(String placa, LocalDate desde, LocalDate hasta) {
        String filtroPlaca = normalizar(placa).isEmpty() ? "%" : normalizar(placa);
        LocalDateTime d = (desde != null) ? desde.atStartOfDay() : LocalDate.of(2000, 1, 1).atStartOfDay();
        LocalDateTime h = (hasta != null) ? hasta.atTime(LocalTime.MAX)
                : LocalDate.of(2100, 12, 31).atTime(LocalTime.MAX);
        return registroRepo.buscarHistorial(filtroPlaca, d, h);
    }

    // ---------- HU06: novedad ----------
    @Override
    @Transactional
    public Novedad registrarNovedad(String placa, String descripcion) {
        if (descripcion == null || descripcion.isBlank()) {
            throw new RuntimeException("La descripción no puede estar vacía");
        }
        Registro registro = buscarActivo(placa);

        Novedad novedad = new Novedad();
        novedad.setRegistro(registro);
        novedad.setDescripcion(descripcion.trim());
        novedad.setFecha(LocalDateTime.now());
        return novedadRepo.save(novedad);
    }

    // ---------- métodos privados (los dos «include» de tu caso de uso) ----------
    private boolean validarMensualidad(String placa) {
        return vehiculoRepo.findByPlaca(placa)
                .map(Vehiculo::getCliente)
                .map(Cliente::getFechaVencimientoMensualidad)
                .map(fecha -> !fecha.isBefore(LocalDate.now()))
                .orElse(false);
    }

    private Celda asignarCelda(String tipo) {
        Celda celda = celdaRepo.findFirstByTipoAndEstado(tipo, "LIBRE")
                .orElseThrow(() -> new RuntimeException("No hay celdas libres para este tipo de vehículo"));
        celda.setEstado("OCUPADA");
        return celdaRepo.save(celda);
    }

    private String normalizar(String placa) {
        return placa == null ? "" : placa.trim().toUpperCase();
    }
}
