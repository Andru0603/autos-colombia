CREATE DATABASE IF NOT EXISTS parqueadero_autoscolombia;
USE parqueadero_autoscolombia;

CREATE TABLE usuario (
  id_usuario INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL,
  apellido VARCHAR(50) NOT NULL,
  username VARCHAR(30) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  rol ENUM('OPERARIO','ADMINISTRADOR') NOT NULL
);

CREATE TABLE cliente (
  id_cliente INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL,
  documento VARCHAR(20) NOT NULL UNIQUE,
  telefono VARCHAR(20),
  fecha_vencimiento_mensualidad DATE
);

CREATE TABLE vehiculo (
  id_vehiculo INT AUTO_INCREMENT PRIMARY KEY,
  placa VARCHAR(10) NOT NULL UNIQUE,
  tipo ENUM('CARRO','MOTO') NOT NULL,
  marca VARCHAR(30),
  modelo VARCHAR(30),
  color VARCHAR(20),
  id_cliente INT,
  FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)
);

CREATE TABLE celda (
  id_celda INT AUTO_INCREMENT PRIMARY KEY,
  numero VARCHAR(10) NOT NULL,
  tipo ENUM('CARRO','MOTO') NOT NULL,
  estado ENUM('LIBRE','OCUPADA') DEFAULT 'LIBRE'
);

CREATE TABLE registro (
  id_registro INT AUTO_INCREMENT PRIMARY KEY,
  id_vehiculo INT NOT NULL,
  id_celda INT NOT NULL,
  id_usuario INT NOT NULL,
  fecha_entrada DATETIME NOT NULL,
  fecha_salida DATETIME NULL,
  FOREIGN KEY (id_vehiculo) REFERENCES vehiculo(id_vehiculo),
  FOREIGN KEY (id_celda) REFERENCES celda(id_celda),
  FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

CREATE TABLE novedad (
  id_novedad INT AUTO_INCREMENT PRIMARY KEY,
  id_registro INT NOT NULL,
  descripcion VARCHAR(255) NOT NULL,
  fecha DATETIME NOT NULL,
  FOREIGN KEY (id_registro) REFERENCES registro(id_registro)
);