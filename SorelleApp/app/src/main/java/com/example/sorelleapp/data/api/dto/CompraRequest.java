package com.example.sorelleapp.data.api.dto;

import java.util.List;

public class CompraRequest {
    private final String nomeCertificado;
    private final List<ItemCompraRequest> itens;

    public CompraRequest(String nomeCertificado, List<ItemCompraRequest> itens) {
        this.nomeCertificado = nomeCertificado;
        this.itens = itens;
    }
}