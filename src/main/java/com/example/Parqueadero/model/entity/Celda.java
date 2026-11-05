package com.example.Parqueadero.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "celda")
public class Celda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_celda")
    private Integer idCelda;

    @Column(name = "numero")
    private String numero;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "estado")
    private String estado;

    public Celda() {
    }

    public Integer getIdCelda() {
        return idCelda;
    }

    public void setIdCelda(Integer idCelda) {
        this.idCelda = idCelda;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
