package com.example.sorelleapp.model;

public class Certificado {
    private final String nome;
    private final String data;
    private final int arvores;
    private final String codigo;

    public Certificado(String nome, String data, int arvores, String codigo) {
        this.nome = nome;
        this.data = data;
        this.arvores = arvores;
        this.codigo = codigo;
    }

    public String getNome() { return nome; }
    public String getData() { return data; }
    public int getArvores() { return arvores; }
    public String getCodigo() { return codigo; }
}