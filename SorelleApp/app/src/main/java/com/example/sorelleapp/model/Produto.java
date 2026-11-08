package com.example.sorelleapp.model;

public class Produto {
    private final int id;
    private final String nome;
    private final String descricao;
    private final double preco;
    private final String tag;
    private final int arvores;
    private final int imagem;

    public Produto(int id, String nome, String descricao, double preco,
                   String tag, int arvores, int imagem) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.tag = tag;
        this.arvores = arvores;
        this.imagem = imagem;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public double getPreco() { return preco; }
    public String getTag() { return tag; }
    public int getArvores() { return arvores; }
    public int getImagem() { return imagem; }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) return true;
        if (!(outro instanceof Produto)) return false;
        return id == ((Produto) outro).id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}