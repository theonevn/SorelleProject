package com.example.sorelleapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sorelleapp.R;
import com.example.sorelleapp.data.Catalogo;
import com.example.sorelleapp.data.UsuarioStorage;
import com.example.sorelleapp.model.Produto;
import com.example.sorelleapp.ui.impacto.ImpactoActivity;
import com.example.sorelleapp.ui.login.LoginActivity;
import com.example.sorelleapp.ui.produtos.ProdutosActivity;
import com.example.sorelleapp.ui.sacola.SacolaActivity;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button btnProdutos = findViewById(R.id.btnProdutos);
        Button btnImpacto = findViewById(R.id.btnImpact);
        Button btnSacola = findViewById(R.id.btnSacola);
        TextView tvSair = findViewById(R.id.tvSair);

        btnProdutos.setOnClickListener(v -> abrir(ProdutosActivity.class));
        btnImpacto.setOnClickListener(v -> abrir(ImpactoActivity.class));
        btnSacola.setOnClickListener(v -> abrir(SacolaActivity.class));

        preencherDestaques();
        Catalogo.sincronizar(this, sucesso -> {
            if (!isFinishing() && !isDestroyed()) {
                preencherDestaques();
            }
        });

        tvSair.setOnClickListener(v -> {
            UsuarioStorage.limparSessao(this);
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void preencherDestaques() {
        List<Produto> lista = Catalogo.getProdutos();
        if (lista.size() < 2) return;

        preencherDestaque(lista.get(0),
                R.id.cardDestaque1, R.id.ivDestaque1, R.id.tvDestaqueNome1,
                R.id.tvDestaquePreco1, R.id.tvDestaqueArvores1);

        preencherDestaque(lista.get(1),
                R.id.cardDestaque2, R.id.ivDestaque2, R.id.tvDestaqueNome2,
                R.id.tvDestaquePreco2, R.id.tvDestaqueArvores2);
    }

    private void preencherDestaque(Produto p, int idCard, int idImagem, int idNome,
                                   int idPreco, int idArvores) {
        ((ImageView) findViewById(idImagem)).setImageResource(p.getImagem());
        ((TextView) findViewById(idNome)).setText(p.getNome());
        ((TextView) findViewById(idPreco)).setText(
                String.format(new Locale("pt", "BR"), "R$ %.2f", p.getPreco()));

        int arvores = p.getArvores();
        ((TextView) findViewById(idArvores)).setText(
                "+" + arvores + (arvores == 1 ? " árvore" : " árvores"));

        findViewById(idCard).setOnClickListener(v -> abrir(ProdutosActivity.class));
    }

    private void abrir(Class<?> destino) {
        startActivity(new Intent(this, destino));
    }
}