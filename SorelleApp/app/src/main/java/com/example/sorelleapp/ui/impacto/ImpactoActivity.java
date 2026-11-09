package com.example.sorelleapp.ui.impacto;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sorelleapp.R;
import com.example.sorelleapp.data.UsuarioStorage;
import com.example.sorelleapp.data.api.ApiClient;
import com.example.sorelleapp.data.api.dto.CertificadoResponse;
import com.example.sorelleapp.data.api.dto.ImpactoResponse;
import com.example.sorelleapp.model.Certificado;
import com.example.sorelleapp.ui.ApiHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImpactoActivity extends AppCompatActivity {

    private TextView tvArvores, tvCo2, tvNivel, tvVazio;
    private RecyclerView rvCertificados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_impacto);

        tvArvores = findViewById(R.id.tvArvores);
        tvCo2 = findViewById(R.id.tvCo2);
        tvNivel = findViewById(R.id.tvNivel);
        tvVazio = findViewById(R.id.tvVazioImpacto);
        rvCertificados = findViewById(R.id.rvCertificados);

        rvCertificados.setLayoutManager(new LinearLayoutManager(this));

        tvArvores.setText("–");
        tvCo2.setText("–");
        tvNivel.setText("–");

        carregar();
    }

    private void carregar() {
        String token = UsuarioStorage.getToken(this);
        if (token == null) {
            ApiHelper.voltarAoLogin(this, null);
            return;
        }

        rvCertificados.setVisibility(View.GONE);
        tvVazio.setText("Carregando...");
        tvVazio.setVisibility(View.VISIBLE);

        ApiClient.get().impacto("Bearer " + token).enqueue(new Callback<ImpactoResponse>() {
            @Override
            public void onResponse(Call<ImpactoResponse> call, Response<ImpactoResponse> resposta) {
                if (isFinishing() || isDestroyed()) return;

                if (resposta.isSuccessful() && resposta.body() != null) {
                    mostrar(resposta.body());
                } else if (resposta.code() == 401) {
                    ApiHelper.voltarAoLogin(ImpactoActivity.this,
                            "Sua sessão expirou. Entre novamente.");
                } else {
                    tvVazio.setText("Não foi possível carregar o impacto (código "
                            + resposta.code() + ").");
                }
            }

            @Override
            public void onFailure(Call<ImpactoResponse> call, Throwable t) {
                if (isFinishing() || isDestroyed()) return;
                tvVazio.setText("Sem conexão com o servidor. Não foi possível carregar "
                        + "seu impacto.");
            }
        });
    }

    private void mostrar(ImpactoResponse r) {
        tvArvores.setText(String.valueOf(r.getTotalArvores()));
        tvCo2.setText(r.getCo2RetidoKg() + " Kg");
        tvNivel.setText(r.getNivel());

        List<Certificado> lista = new ArrayList<>();
        if (r.getCertificados() != null) {
            for (CertificadoResponse c : r.getCertificados()) {
                lista.add(new Certificado(
                        c.getNomeCertificado(),
                        formatarData(c.getDataEmissao()),
                        c.getTotalArvoresPlantadas(),
                        c.getCodigoDiploma()));
            }
        }

        rvCertificados.setAdapter(new CertificadoAdapter(lista));

        boolean vazio = lista.isEmpty();
        tvVazio.setText("Nenhuma ação certificada ainda.");
        tvVazio.setVisibility(vazio ? View.VISIBLE : View.GONE);
        rvCertificados.setVisibility(vazio ? View.GONE : View.VISIBLE);
    }

    // A API devolve "2026-10-29T14:10:55"; o app mostra "29/10/2026".
    private String formatarData(String iso) {
        if (iso == null || iso.length() < 10) return "";
        String[] partes = iso.substring(0, 10).split("-");
        if (partes.length != 3) return iso;
        return partes[2] + "/" + partes[1] + "/" + partes[0];
    }
}