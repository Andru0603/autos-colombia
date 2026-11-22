-- =====================================================
-- Ejecutar en Workbench antes de correr la colección de Postman.
-- NO borra usuarios, celdas ni vehículos.
-- =====================================================
USE parqueadero_autoscolombia;
SET SQL_SAFE_UPDATES = 0;          -- Workbench bloquea UPDATE/DELETE sin llave

DELETE FROM pago;
ALTER TABLE pago AUTO_INCREMENT = 1;   -- el primer comprobante vuelve a ser REC-000001

UPDATE tarifa SET valor_mensual = 150000.00 WHERE tipo = 'CARRO';
UPDATE tarifa SET valor_mensual =  70000.00 WHERE tipo = 'MOTO';

-- Juan Pérez: mensualidad vigente (30 días). María López: vencida hace 10 días.
UPDATE cliente SET fecha_vencimiento_mensualidad = DATE_ADD(CURDATE(), INTERVAL 30 DAY) WHERE documento = '1010101010';
UPDATE cliente SET fecha_vencimiento_mensualidad = DATE_SUB(CURDATE(), INTERVAL 10 DAY) WHERE documento = '2020202020';

-- Saca los vehículos que hayan quedado dentro en pruebas anteriores y libera sus celdas
UPDATE registro SET fecha_salida = NOW() WHERE fecha_salida IS NULL;
UPDATE celda SET estado = 'LIBRE' WHERE estado = 'OCUPADA';

SET SQL_SAFE_UPDATES = 1;

-- Verificación
SELECT documento, nombre, fecha_vencimiento_mensualidad FROM cliente;
SELECT * FROM tarifa;
SELECT COUNT(*) AS pagos FROM pago;
