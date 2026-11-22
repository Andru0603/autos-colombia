-- =====================================================
-- Parqueadero "Autos Colombia" - Iteración 3
-- Gestión de pagos
-- =====================================================
USE parqueadero_autoscolombia;

-- 1) TARIFA: valor mensual por tipo de vehículo (RF17)
CREATE TABLE tarifa (
  id_tarifa     INT AUTO_INCREMENT PRIMARY KEY,
  tipo          ENUM('CARRO','MOTO') NOT NULL UNIQUE,
  valor_mensual DECIMAL(10,2) NOT NULL,
  CONSTRAINT ck_tarifa_valor CHECK (valor_mensual > 0)
) ENGINE=InnoDB;

-- 2) PAGO: pago de mensualidad de un cliente (RF20)
--    El monto queda guardado para que el historial no cambie si luego cambia la tarifa.
CREATE TABLE pago (
  id_pago       INT AUTO_INCREMENT PRIMARY KEY,
  id_cliente    INT NOT NULL,
  id_usuario    INT NOT NULL,                 -- quién registró el pago
  fecha_pago    DATETIME NOT NULL,
  meses         INT NOT NULL,
  monto         DECIMAL(12,2) NOT NULL,
  metodo_pago   ENUM('EFECTIVO','TRANSFERENCIA','TARJETA') NOT NULL,
  vigente_desde DATE NOT NULL,
  vigente_hasta DATE NOT NULL,
  CONSTRAINT fk_pago_cliente FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
  CONSTRAINT fk_pago_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
  CONSTRAINT ck_pago_meses   CHECK (meses BETWEEN 1 AND 12),
  CONSTRAINT ck_pago_monto   CHECK (monto > 0)
) ENGINE=InnoDB;

-- Índices para las consultas de historial y recaudo (RNF17)
CREATE INDEX idx_pago_fecha   ON pago(fecha_pago);
CREATE INDEX idx_pago_cliente ON pago(id_cliente);

-- 3) Datos de prueba: tarifas de ejemplo en pesos colombianos
INSERT INTO tarifa (tipo, valor_mensual) VALUES
 ('CARRO', 150000.00),
 ('MOTO',   70000.00);


