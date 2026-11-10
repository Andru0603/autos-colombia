-- =====================================================
-- Parqueadero "Autos Colombia" - Iteración 2
-- Gestión de usuarios y gestión de celdas
-- =====================================================
USE parqueadero_autoscolombia;

-- 1) USUARIO: baja lógica (RF12). No se borran usuarios porque
--    la tabla registro los referencia (historial de accesos).
ALTER TABLE usuario
  ADD COLUMN activo TINYINT(1) NOT NULL DEFAULT 1 AFTER rol;

-- 2) CELDA: nuevo estado MANTENIMIENTO (RF16).
--    OCUPADA lo siguen manejando las entradas/salidas de vehículos.
ALTER TABLE celda
  MODIFY COLUMN estado ENUM('LIBRE','OCUPADA','MANTENIMIENTO') NOT NULL DEFAULT 'LIBRE';

-- 3) CELDA: el número de celda no puede repetirse (RF13, RNF10).
CREATE UNIQUE INDEX uq_celda_numero ON celda(numero);

-- 4) Datos de prueba para la iteración 2
INSERT INTO celda (numero, tipo) VALUES ('A-04','CARRO'), ('M-03','MOTO');


