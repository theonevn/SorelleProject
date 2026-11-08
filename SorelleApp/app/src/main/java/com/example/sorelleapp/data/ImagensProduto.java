package com.example.sorelleapp.data;

import com.example.sorelleapp.R;

import java.text.Normalizer;

public class ImagensProduto {

    public static int paraNome(String nome) {
        String n = Normalizer.normalize(nome == null ? "" : nome, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();

        if (n.contains("serum")) return R.drawable.serum_acai;
        if (n.contains("creme")) return R.drawable.creme_castanha;
        if (n.contains("oleo")) return R.drawable.oleo_andiroba;
        if (n.contains("kit")) return R.drawable.kit_ritual;
        return R.drawable.logoessence;
    }
}