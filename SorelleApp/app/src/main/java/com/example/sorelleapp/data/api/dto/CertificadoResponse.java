package com.example.sorelleapp.data.api.dto;

public class CertificadoResponse {
    private String nomeCertificado;
    private String codigoDiploma;
    private int totalArvoresPlantadas;
    private String dataEmissao;

    public String getNomeCertificado() { return nomeCertificado; }
    public String getCodigoDiploma() { return codigoDiploma; }
    public int getTotalArvoresPlantadas() { return totalArvoresPlantadas; }
    public String getDataEmissao() { return dataEmissao; }
}