USE parqueadero_autoscolombia;

-- admin / admin123   |   operario / oper123  (solo para pruebas)
INSERT INTO usuario (nombre, apellido, username, password_hash, rol) VALUES
('Ana',    'Admin',    'admin',    '$2b$10$GiWKAaReShrHwSqSArD6HO7CLjlqOgfUYLKbRGqooRMB9jwO0S8RW', 'ADMINISTRADOR'),
('Carlos', 'Operario', 'operario', '$2b$10$RmDxwLz/7.l.i9B8SVccSuR89h004TFiQ9oTIZlj50xVXPcVCjuQG', 'OPERARIO');

-- Cliente 1: mensualidad vigente. Cliente 2: mensualidad vencida
INSERT INTO cliente (nombre, documento, telefono, fecha_vencimiento_mensualidad) VALUES
('Juan Pérez',  '1010101010', '3001234567', DATE_ADD(CURDATE(), INTERVAL 30 DAY)),
('María López', '2020202020', '3109876543', DATE_SUB(CURDATE(), INTERVAL 10 DAY));

INSERT INTO vehiculo (placa, tipo, marca, modelo, color, id_cliente) VALUES
('ABC123', 'CARRO', 'Mazda',  '2020', 'Rojo',  (SELECT id_cliente FROM cliente WHERE documento = '1010101010')),
('XYZ98A', 'MOTO',  'Yamaha', '2019', 'Negro', (SELECT id_cliente FROM cliente WHERE documento = '1010101010')),
('DEF456', 'CARRO', 'Renault','2018', 'Gris',  (SELECT id_cliente FROM cliente WHERE documento = '2020202020'));

INSERT INTO celda (numero, tipo) VALUES
('A-01','CARRO'), ('A-02','CARRO'), ('A-03','CARRO'),
('M-01','MOTO'),  ('M-02','MOTO');