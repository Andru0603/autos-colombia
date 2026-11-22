package com.example.Parqueadero.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tarifa")
public class Tarifa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarifa")
    private Integer idTarifa;

    @Column(name = "tipo")
    private String tipo; // CARRO o MOTO

    @Column(name = "valor_mensual")
    private BigDecimal valorMensual;

    public Tarifa() {
    }

    public Integer getIdTarifa() {
        return idTarifa;
    }

    public void setIdTarifa(Integer idTarifa) {
        this.idTarifa = idTarifa;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getValorMensual() {
        return valorMensual;
    }

    public void setValorMensual(BigDecimal valorMensual) {
        this.valorMensual = valorMensual;
    }
}
