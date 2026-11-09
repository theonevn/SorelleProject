package com.example.sorelleapp.ui.sacola;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sorelleapp.R;
import com.example.sorelleapp.data.Sacola;
import com.example.sorelleapp.data.UsuarioStorage;
import com.example.sorelleapp.data.api.ApiClient;
import com.example.sorelleapp.data.api.dto.CompraRequest;
import com.example.sorelleapp.data.api.dto.CompraResponse;
import com.example.sorelleapp.data.api.dto.ItemCompraRequest;
import com.example.sorelleapp.model.Produto;
import com.example.sorelleapp.ui.ApiHelper;
import com.example.sorelleapp.ui.impacto.ImpactoActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SacolaActivity extends AppCompatActivity {

    private RecyclerView rvSacola;
    private TextView tvTotal, tvVazio;
    private Button btnFinalizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activy_sacola);

        rvSacola = findViewById(R.id.rvSacola);
        tvTotal = findViewById(R.id.tvTotal);
        tvVazio = findViewById(R.id.tvVazio);
        btnFinalizar = findViewById(R.id.btnFinalizar);

        rvSacola.setLayoutManager(new LinearLayoutManager(this));
        rvSacola.setAdapter(new SacolaAdapter(this::atualizarTela));

        btnFinalizar.setOnClickListener(v -> {
            CertificadoBottomSheet sheet = new CertificadoBottomSheet();
            sheet.setOnConfirmarListener(this::finalizarCompra);
            sheet.show(getSupportFragmentManager(), "certificado");
        });

        atualizarTela();
    }

    private void finalizarCompra(String nomeCertificado) {
        String token = UsuarioStorage.getToken(this);
        if (token == null) {
            ApiHelper.voltarAoLogin(this, "Faça login para finalizar a compra.");
            return;
        }

        List<ItemCompraRequest> itens = new ArrayList<>();
        for (Map.Entry<Produto, Integer> item : Sacola.getInstancia().getItens().entrySet()) {
            itens.add(new ItemCompraRequest(item.getKey().getId(), item.getValue()));
        }

        setEnviando(true);

        ApiClient.get()
                .comprar("Bearer " + token, new CompraRequest(nomeCertificado, itens))
                .enqueue(new Callback<CompraResponse>() {
                    @Override
                    public void onResponse(Call<CompraResponse> call,
                                           Response<CompraResponse> resposta) {
                        setEnviando(false);

                        if (resposta.isSuccessful() && resposta.body() != null) {
                            Sacola.getInstancia().limpar();
                            Toast.makeText(SacolaActivity.this,
                                    "Compra confirmada! Certificado "
                                            + resposta.body().getCodigoDiploma(),
                                    Toast.LENGTH_LONG).show();
                            startActivity(new Intent(SacolaActivity.this, ImpactoActivity.class));
                            finish();
                        } else if (resposta.code() == 401) {
                            ApiHelper.voltarAoLogin(SacolaActivity.this,
                                    "Sua sessão expirou. Entre novamente.");
                        } else if (resposta.code() == 400) {
                            Toast.makeText(SacolaActivity.this,
                                    ApiHelper.lerErro(resposta, "Compra recusada."),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(SacolaActivity.this,
                                    "Erro no servidor (código " + resposta.code()
                                            + "). Sua sacola foi mantida.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CompraResponse> call, Throwable t) {
                        setEnviando(false);
                        Toast.makeText(SacolaActivity.this,
                                "Falha: " + t.getClass().getSimpleName() + " - " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setEnviando(boolean enviando) {
        btnFinalizar.setEnabled(!enviando);
        btnFinalizar.setText(enviando ? "Enviando..." : "Finalizar compra");
    }

    private void atualizarTela() {
        Sacola sacola = Sacola.getInstancia();
        boolean vazia = sacola.getItens().isEmpty();

        tvVazio.setVisibility(vazia ? View.VISIBLE : View.GONE);
        rvSacola.setVisibility(vazia ? View.GONE : View.VISIBLE);
        btnFinalizar.setEnabled(!vazia);
        tvTotal.setText(String.format(new Locale("pt", "BR"), "R$ %.2f", sacola.getTotal()));
    }
}