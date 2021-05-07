package com.example.whatsappclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsappclone.R;
import com.example.whatsappclone.config.ConfiguracaoFirebase;
import com.example.whatsappclone.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    private TextView textoCadastro;
    private EditText textSenha;
    private EditText textEmail;
    private Button botaoEnviar;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textSenha = findViewById(R.id.inputTextSenha);
        textEmail = findViewById(R.id.inputTextEmail);
        botaoEnviar = findViewById(R.id.buttonEnviar);
        textoCadastro = findViewById(R.id.textCadastro);

        textoCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirTelaCadastro();

            }
        });

    }



    public void logarUsuario (Usuario usuario){

        autenticacao = ConfiguracaoFirebase.getAuth();
        autenticacao.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    Toast.makeText(LoginActivity.this, "Usuario logado", Toast.LENGTH_SHORT).show();
                    abrirTelaPrincipal();
                } else{

                    String excecao = "";
                    try {
                        throw task.getException();
                    }   catch ( FirebaseAuthInvalidUserException e){
                        excecao = "usuário não cadastrado";
                    }   catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "E-mail ou senha incorretos";
                    }   catch (Exception e ){
                        excecao = "erro ao autenticar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(LoginActivity.this, excecao, Toast.LENGTH_SHORT).show();

                }

            }
        });



    }

    public void validarCadastrarUsuario(View view){

        //Recuperar textos dos campos


        String textoEmail = textEmail.getText().toString();
        String textoSenha = textSenha.getText().toString();


            if (! textoEmail.isEmpty()){
                if (! textoSenha.isEmpty()){
                    Usuario usuario = new Usuario();
                    usuario.setEmail(textoEmail);
                    usuario.setSenha(textoSenha);

                    logarUsuario(usuario);

                }else{
                    Toast.makeText(LoginActivity.this, "Insira uma senha", Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(LoginActivity.this, "Insisra um email", Toast.LENGTH_SHORT).show();
            }


    }

    public void abrirTelaPrincipal(){

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);

    }

    public void abrirTelaCadastro(){

        Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity(intent);

    }
}
