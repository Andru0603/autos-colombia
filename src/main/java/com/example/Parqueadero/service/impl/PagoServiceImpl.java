package com.example.Parqueadero.service.impl;

import com.example.Parqueadero.model.entity.*;
import com.example.Parqueadero.repository.*;
import com.example.Parqueadero.service.PagoService;
import com.example.Parqueadero.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class PagoServiceImpl implements PagoService {

    private static final List<String> METODOS = List.of("EFECTIVO", "TRANSFERENCIA", "TARJETA");
    private static final List<String> ESTADOS = List.of("VIGENTE", "POR_VENCER", "VENCIDA", "SIN_MENSUALIDAD");
    private static final int MIN_MESES = 1;
    private static final int MAX_MESES = 12;
    private static final int DIAS_POR_VENCER = 7;

    @Autowired
    private PagoRepository pagoRepo;
    @Autowired
    private ClienteRepository clienteRepo;
    @Autowired
    private VehiculoRepository vehiculoRepo;
    @Autowired
    private TarifaRepository tarifaRepo;
    @Autowired
    private UsuarioService usuarioService;

    // ---------- HU16: estado de mensualidades ----------
    @Override
    public List<Map<String, Object>> estadoMensualidades(Integer idSolicitante, String estado) {
        usuarioService.validarUsuarioActivo(idSolicitante);

        String filtro = (estado == null || estado.isBlank()) ? null : estado.trim().toUpperCase();
        if (filtro != null && !ESTADOS.contains(filtro)) {
            throw new RuntimeException("Estado inválido. Use VIGENTE, POR_VENCER, VENCIDA o SIN_MENSUALIDAD");
        }

        List<Cliente> clientes = new ArrayList<>(clienteRepo.findAll());
        clientes.sort(Comparator.comparing(Cliente::getNombre));

        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Cliente c : clientes) {
            String estadoCliente = calcularEstado(c.getFechaVencimientoMensualidad());
            if (filtro != null && !filtro.equals(estadoCliente)) {
                continue;
            }
            Map<String, Object> fila = new LinkedHashMap<>();
            fila.put("documento", c.getDocumento());
            fila.put("nombre", c.getNombre());
            fila.put("telefono", c.getTelefono());
            fila.put("fechaVencimiento", c.getFechaVencimientoMensualidad());
            fila.put("estado", estadoCliente);
            fila.put("diasRestantes", c.getFechaVencimientoMensualidad() == null ? null
                    : ChronoUnit.DAYS.between(LocalDate.now(), c.getFechaVencimientoMensualidad()));
            resultado.add(fila);
        }
        return resultado;
    }

    // ---------- HU17: calcular valor a pagar (no guarda nada) ----------
    @Override
    public Map<String, Object> calcular(Integer idSolicitante, String documento, Integer meses) {
        usuarioService.validarUsuarioActivo(idSolicitante);
        Cliente cliente = buscarCliente(documento);
        int m = validarMeses(meses);

        List<Vehiculo> vehiculos = vehiculosDe(cliente);
        BigDecimal valorMensual = valorMensualDe(vehiculos);
        BigDecimal total = valorMensual.multiply(BigDecimal.valueOf(m));
        LocalDate desde = inicioCobertura(cliente);

        List<String> placas = new ArrayList<>();
        for (Vehiculo v : vehiculos) {
            placas.add(v.getPlaca());
        }

        Map<String, Object> r = new LinkedHashMap<>();
        r.put("cliente", cliente.getNombre());
        r.put("documento", cliente.getDocumento());
        r.put("vehiculos", placas);
        r.put("meses", m);
        r.put("valorMensual", valorMensual);
        r.put("total", total);
        r.put("vencimientoActual", cliente.getFechaVencimientoMensualidad());
        r.put("vigenteDesde", desde);
        r.put("nuevoVencimiento", desde.plusMonths(m));
        return r;
    }

    // ---------- HU18: registrar pago ----------
    @Override
    @Transactional // el pago y el nuevo vencimiento se guardan juntos o no se guarda nada (RNF14)
    public Pago registrar(Integer idSolicitante, String documento, Integer meses, String metodoPago) {
        Usuario usuario = usuarioService.validarUsuarioActivo(idSolicitante);
        Cliente cliente = buscarCliente(documento);
        int m = validarMeses(meses);
        String metodo = normalizarMetodo(metodoPago);

        // El monto lo calcula el sistema: nunca viene del usuario (RNF14)
        BigDecimal total = valorMensualDe(vehiculosDe(cliente)).multiply(BigDecimal.valueOf(m));
        LocalDate desde = inicioCobertura(cliente);
        LocalDate hasta = desde.plusMonths(m);

        Pago pago = new Pago();
        pago.setCliente(cliente);
        pago.setUsuario(usuario);
        pago.setFechaPago(LocalDateTime.now());
        pago.setMeses(m);
        pago.setMonto(total);
        pago.setMetodoPago(metodo);
        pago.setVigenteDesde(desde);
        pago.setVigenteHasta(hasta);

        // Esta fecha es la que valida la entrada de vehículos (iteración 1, RF2)
        cliente.setFechaVencimientoMensualidad(hasta);
        clienteRepo.save(cliente);
        return pagoRepo.save(pago);
    }

    // ---------- HU19: comprobante ----------
    @Override
    public Map<String, Object> comprobante(Integer idSolicitante, Integer idPago) {
        usuarioService.validarUsuarioActivo(idSolicitante);
        Pago pago = pagoRepo.findById(idPago)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));

        Cliente cliente = pago.getCliente();
        List<String> placas = new ArrayList<>();
        for (Vehiculo v : vehiculoRepo.findByCliente_IdCliente(cliente.getIdCliente())) {
            placas.add(v.getPlaca());
        }

        Map<String, Object> r = new LinkedHashMap<>();
        r.put("numeroComprobante", pago.getNumeroComprobante());
        r.put("fechaPago", pago.getFechaPago());
        r.put("cliente", cliente.getNombre());
        r.put("documento", cliente.getDocumento());
        r.put("vehiculos", placas);
        r.put("meses", pago.getMeses());
        r.put("monto", pago.getMonto());
        r.put("metodoPago", pago.getMetodoPago());
        r.put("vigenteDesde", pago.getVigenteDesde());
        r.put("vigenteHasta", pago.getVigenteHasta());
        r.put("registradoPor", pago.getUsuario().getNombre() + " " + pago.getUsuario().getApellido());
        return r;
    }

    // ---------- HU20: historial ----------
    @Override
    public List<Pago> historial(Integer idSolicitante, String documento, LocalDate desde, LocalDate hasta) {
        usuarioService.validarUsuarioActivo(idSolicitante);
        validarRango(desde, hasta);

        String filtroDocumento = (documento == null || documento.isBlank()) ? "%" : documento.trim();
        LocalDateTime d = (desde != null) ? desde.atStartOfDay() : LocalDate.of(2000, 1, 1).atStartOfDay();
        LocalDateTime h = (hasta != null) ? hasta.atTime(LocalTime.MAX)
                : LocalDate.of(2100, 12, 31).atTime(LocalTime.MAX);
        return pagoRepo.buscar(filtroDocumento, d, h);
    }

    // ---------- HU21: recaudo por periodo (solo administrador) ----------
    @Override
    public Map<String, Object> recaudo(Integer idSolicitante, LocalDate desde, LocalDate hasta) {
        usuarioService.validarAdministrador(idSolicitante);
        if (desde == null || hasta == null) {
            throw new RuntimeException("Debe indicar la fecha inicial y la fecha final");
        }
        validarRango(desde, hasta);

        List<Pago> pagos = pagoRepo.buscar("%", desde.atStartOfDay(), hasta.atTime(LocalTime.MAX));

        BigDecimal total = BigDecimal.ZERO;
        Map<String, BigDecimal> porMetodo = new LinkedHashMap<>();
        for (String metodo : METODOS) {
            porMetodo.put(metodo, BigDecimal.ZERO);
        }
        for (Pago p : pagos) {
            total = total.add(p.getMonto());
            porMetodo.merge(p.getMetodoPago(), p.getMonto(), BigDecimal::add);
        }

        Map<String, Object> r = new LinkedHashMap<>();
        r.put("desde", desde);
        r.put("hasta", hasta);
        r.put("cantidadPagos", pagos.size());
        r.put("totalRecaudado", total);
        r.put("porMetodo", porMetodo);
        return r;
    }

    // ---------- métodos privados ----------
    private Cliente buscarCliente(String documento) {
        if (documento == null || documento.isBlank()) {
            throw new RuntimeException("El documento del cliente es obligatorio");
        }
        String doc = documento.trim();
        return clienteRepo.findByDocumento(doc)
                .orElseThrow(() -> new RuntimeException("No existe un cliente con el documento " + doc));
    }

    private int validarMeses(Integer meses) {
        if (meses == null) {
            throw new RuntimeException("Debe indicar la cantidad de meses");
        }
        if (meses < MIN_MESES || meses > MAX_MESES) {
            throw new RuntimeException("Los meses deben estar entre " + MIN_MESES + " y " + MAX_MESES);
        }
        return meses;
    }

    private String normalizarMetodo(String metodo) {
        String m = (metodo == null) ? "" : metodo.trim().toUpperCase();
        if (!METODOS.contains(m)) {
            throw new RuntimeException("Método de pago inválido. Use EFECTIVO, TRANSFERENCIA o TARJETA");
        }
        return m;
    }

    private void validarRango(LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new RuntimeException("La fecha inicial no puede ser posterior a la fecha final");
        }
    }

    private List<Vehiculo> vehiculosDe(Cliente cliente) {
        List<Vehiculo> vehiculos = vehiculoRepo.findByCliente_IdCliente(cliente.getIdCliente());
        if (vehiculos.isEmpty()) {
            throw new RuntimeException("El cliente no tiene vehículos registrados");
        }
        return vehiculos;
    }

    // La mensualidad del cliente cubre todos sus vehículos: suma la tarifa de cada uno
    private BigDecimal valorMensualDe(List<Vehiculo> vehiculos) {
        BigDecimal valor = BigDecimal.ZERO;
        for (Vehiculo v : vehiculos) {
            Tarifa tarifa = tarifaRepo.findByTipo(v.getTipo())
                    .orElseThrow(() -> new RuntimeException("No hay tarifa configurada para " + v.getTipo()));
            valor = valor.add(tarifa.getValorMensual());
        }
        return valor;
    }

    // Si la mensualidad sigue vigente, el nuevo periodo empieza donde termina; si venció, empieza hoy
    private LocalDate inicioCobertura(Cliente cliente) {
        LocalDate vencimiento = cliente.getFechaVencimientoMensualidad();
        LocalDate hoy = LocalDate.now();
        return (vencimiento != null && !vencimiento.isBefore(hoy)) ? vencimiento : hoy;
    }

    private String calcularEstado(LocalDate vencimiento) {
        if (vencimiento == null) {
            return "SIN_MENSUALIDAD";
        }
        long dias = ChronoUnit.DAYS.between(LocalDate.now(), vencimiento);
        if (dias < 0) {
            return "VENCIDA";
        }
        return dias <= DIAS_POR_VENCER ? "POR_VENCER" : "VIGENTE";
    }
}
