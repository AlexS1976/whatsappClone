package com.example.whatsappclone.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.whatsappclone.R;

public class LoginActivity extends AppCompatActivity {

    private TextView textoCadastro;
    private EditText textSenha;
    private EditText textEmail;
    private Button botaoEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textoCadastro = findViewById(R.id.textCadastro);
        textSenha = findViewById(R.id.inputTextSenha);
        textEmail = findViewById(R.id.inputTextEmail);
        botaoEnviar = findViewById(R.id.buttonEnviar);

        textoCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirTelaCadastro();

            }
        });

    }

    public void abrirTelaCadastro(){

        Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity(intent);

    }
}