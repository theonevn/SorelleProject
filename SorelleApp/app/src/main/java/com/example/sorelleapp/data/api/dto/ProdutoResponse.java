package com.example.sorelleapp.data.api.dto;

public class ProdutoResponse {
    private int id;
    private String nome;
    private String descricao;
    private double preco;
    private int arvores;
    private String tag;

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public double getPreco() { return preco; }
    public int getArvores() { return arvores; }
    public String getTag() { return tag; }
}