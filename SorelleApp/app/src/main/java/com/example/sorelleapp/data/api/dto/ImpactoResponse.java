package com.example.sorelleapp.data.api.dto;

import java.util.List;

public class ImpactoResponse {
    private int totalArvores;
    private int co2RetidoKg;
    private String nivel;
    private List<CertificadoResponse> certificados;

    public int getTotalArvores() { return totalArvores; }
    public int getCo2RetidoKg() { return co2RetidoKg; }
    public String getNivel() { return nivel; }
    public List<CertificadoResponse> getCertificados() { return certificados; }
}