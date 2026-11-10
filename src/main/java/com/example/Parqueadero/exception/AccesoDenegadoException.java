package com.example.Parqueadero.exception;

/**
 * Se lanza cuando quien hace la petición no es un administrador activo.
 * El GlobalExceptionHandler la convierte en una respuesta HTTP 403.
 */
public class AccesoDenegadoException extends RuntimeException {

    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }
}
