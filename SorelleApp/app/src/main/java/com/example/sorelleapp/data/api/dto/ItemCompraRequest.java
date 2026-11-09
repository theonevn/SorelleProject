package com.example.sorelleapp.data.api.dto;

public class ItemCompraRequest {
    private final int produtoId;
    private final int quantidade;

    public ItemCompraRequest(int produtoId, int quantidade) {
        this.produtoId = produtoId;
        this.quantidade = quantidade;
    }
}