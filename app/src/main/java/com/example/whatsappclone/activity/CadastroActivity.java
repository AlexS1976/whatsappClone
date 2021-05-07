package com.example.whatsappclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.whatsappclone.R;
import com.example.whatsappclone.config.ConfiguracaoFirebase;
import com.example.whatsappclone.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {

    private ImageView foto;
    private TextInputEditText nome;
    private TextInputEditText email;
    private TextInputEditText senha;
    private Button enviar;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        foto = findViewById(R.id.imageViewFoto);
        nome = findViewById(R.id.editNome);
        email = findViewById(R.id.editEmail);
        senha = findViewById(R.id.editSenha);
        enviar = findViewById(R.id.buttonCadastrar);




    }
    public void cadastrarUsuario(Usuario usuario){

        autenticacao = ConfiguracaoFirebase.getAuth();
        autenticacao.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                String excecao = "";
                if (task.isSuccessful()){
                    Toast.makeText(CadastroActivity.this, "Cdastro realizado com sucesso", Toast.LENGTH_SHORT).show();
                }else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        excecao = "Digite uma senha mais forte";
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "Digite um email válido";
                    } catch (FirebaseAuthUserCollisionException e){
                        excecao = "Email já cadastrado";
                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                    Toast.makeText(CadastroActivity.this, excecao, Toast.LENGTH_SHORT).show();

                }

            }
        });




    }

    public void validarCadastrarUsuario(View view){

        //Recuperar textos dos campos

        String textoNome = nome.getText().toString();
        String textoEmail = email.getText().toString();
        String textoSenha = senha.getText().toString();

        if (! textoNome.isEmpty()){
            if (! textoEmail.isEmpty()){
                if (! textoSenha.isEmpty()){
                    Usuario usuario = new Usuario();
                    usuario.setNome(textoNome);
                    usuario.setEmail(textoEmail);
                    usuario.setSenha(textoSenha);

                    cadastrarUsuario(usuario);

                }else{
                    Toast.makeText(CadastroActivity.this, "Insira uma senha", Toast.LENGTH_SHORT).show();
                }
                
            }else{
                Toast.makeText(CadastroActivity.this, "Insisra um email", Toast.LENGTH_SHORT).show();
            }
            
        }else{
            Toast.makeText(CadastroActivity.this, "Preencha o nome", Toast.LENGTH_SHORT).show();
            
        }


    }
}