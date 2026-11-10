package com.example.sorelleapp.ui.login;

import static com.example.sorelleapp.ui.ApiHelper.lerErro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sorelleapp.R;
import com.example.sorelleapp.data.Catalogo;
import com.example.sorelleapp.data.UsuarioStorage;
import com.example.sorelleapp.data.api.ApiClient;
import com.example.sorelleapp.data.api.dto.AuthResponse;
import com.example.sorelleapp.data.api.dto.ErroResponse;
import com.example.sorelleapp.data.api.dto.LoginRequest;
import com.example.sorelleapp.data.api.dto.RegistroRequest;
import com.example.sorelleapp.ui.home.MainActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private boolean modoCadastro = false;

    private TextInputLayout tilNome, tilEmail, tilSenha;
    private TextInputEditText etNome, etEmail, etSenha;
    private TextView tvTitulo, tvSubtitulo, tvAlternar;
    private Button btnEntrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Catalogo.carregarDoCache(this);

        if (UsuarioStorage.isLogado(this)) {
            abrirHome();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        tilNome = findViewById(R.id.tilNome);
        tilEmail = findViewById(R.id.tilEmail);
        tilSenha = findViewById(R.id.tilSenha);
        etNome = findViewById(R.id.etNome);
        etEmail = findViewById(R.id.etEmail);
        etSenha = findViewById(R.id.etSenha);
        tvTitulo = findViewById(R.id.tvTitulo);
        tvSubtitulo = findViewById(R.id.tvSubtitulo);
        tvAlternar = findViewById(R.id.tvAlternar);
        btnEntrar = findViewById(R.id.btnEntrar);
        TextView tvEsqueci = findViewById(R.id.tvEsqueci);
        Button btnGoogle = findViewById(R.id.btnGoogle);

        atualizarModo();

        btnEntrar.setOnClickListener(v -> tentarEntrar());

        tvAlternar.setOnClickListener(v -> {
            modoCadastro = !modoCadastro;
            limparErros();
            atualizarModo();
        });

        tvEsqueci.setOnClickListener(v ->
                Toast.makeText(this, "Recuperação de senha em breve", Toast.LENGTH_SHORT).show());

        btnGoogle.setOnClickListener(v ->
                Toast.makeText(this, "Login com Google em breve", Toast.LENGTH_SHORT).show());
    }

    private void atualizarModo() {
        tilNome.setVisibility(modoCadastro ? View.VISIBLE : View.GONE);

        if (modoCadastro) {
            tvTitulo.setText("Criar conta");
            tvSubtitulo.setText("Cadastre-se para acompanhar seu impacto na floresta.");
            btnEntrar.setText("Criar conta");
            tvAlternar.setText("Já tem conta? Entrar");
        } else {
            tvTitulo.setText("Bem-vindo de volta");
            tvSubtitulo.setText("Entre para acompanhar seu impacto na floresta.");
            btnEntrar.setText("Entrar");
            tvAlternar.setText("Novo por aqui? Criar conta");
        }
    }

    private void limparErros() {
        tilNome.setError(null);
        tilEmail.setError(null);
        tilSenha.setError(null);
    }

    private void tentarEntrar() {
        String nome = etNome.getText() == null ? "" : etNome.getText().toString().trim();
        String email = etEmail.getText() == null ? "" : etEmail.getText().toString().trim();
        String senha = etSenha.getText() == null ? "" : etSenha.getText().toString();

        limparErros();

        if (modoCadastro && nome.length() < 2) {
            tilNome.setError("Informe seu nome");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("E-mail inválido");
            return;
        }
        if (senha.length() < 6) {
            tilSenha.setError("Mínimo de 6 caracteres");
            return;
        }

        setCarregando(true);

        Call<AuthResponse> chamada = modoCadastro
                ? ApiClient.get().registrar(new RegistroRequest(nome, email, senha))
                : ApiClient.get().login(new LoginRequest(email, senha));

        chamada.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> resposta) {
                setCarregando(false);

                if (resposta.isSuccessful() && resposta.body() != null) {
                    UsuarioStorage.salvarSessao(LoginActivity.this, resposta.body());
                    abrirHome();
                } else {
                    mostrarErro(resposta);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                setCarregando(false);
                android.util.Log.e("LOGIN", "Falha na chamada", t);
                Toast.makeText(LoginActivity.this,
                        "Não foi possível conectar ao servidor. Verifique sua conexão e " +
                                "tente novamente.",
                        Toast.LENGTH_LONG).show();
            }

            private void mostrarErro(Response<?> resposta) {
                switch (resposta.code()) {
                    case 401:
                        tilSenha.setError("E-mail ou senha incorretos");
                        break;
                    case 409:
                        tilEmail.setError(lerErro(resposta, "Já existe uma conta com esse " +
                                "e-mail."));
                        break;
                    case 400:
                        Toast.makeText(LoginActivity.this, lerErro(resposta, "Dados inválidos."),
                                Toast.LENGTH_LONG).show();
                        break;
                    default:
                        Toast.makeText(LoginActivity.this, "Erro no servidor (código " + resposta.code() + ")",
                                Toast.LENGTH_LONG).show();
                }
            }

            private String lerErro(Response<?> resposta, String padrao) {
                try {
                    if (resposta.errorBody() != null) {
                        ErroResponse erro = new Gson().fromJson(
                                resposta.errorBody().string(), ErroResponse.class);
                        if (erro != null && erro.getErro() != null) {
                            return erro.getErro();
                        }
                    }
                } catch (Exception ignorada) {
                }
                return padrao;
            }
        });
    }

            private void setCarregando(boolean carregando) {
                btnEntrar.setEnabled(!carregando);
                tvAlternar.setEnabled(!carregando);
                if (carregando) {
                    btnEntrar.setText("Aguarde...");
                } else {
                    btnEntrar.setText(modoCadastro ? "Criar conta" : "Entrar");
                }
            }

            private void abrirHome() {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        }