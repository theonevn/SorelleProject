package com.example.sorelleapp.data.api.dto;

public class CompraResponse {
    private int pedidoId;
    private String codigoDiploma;
    private int totalArvores;
    private double valorTotal;

    public int getPedidoId() { return pedidoId; }
    public String getCodigoDiploma() { return codigoDiploma; }
    public int getTotalArvores() { return totalArvores; }
    public double getValorTotal() { return valorTotal; }
}