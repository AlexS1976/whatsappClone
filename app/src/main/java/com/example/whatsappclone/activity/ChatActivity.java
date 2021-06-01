package com.example.whatsappclone.activity;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.MensagensAdapter;
import com.example.whatsappclone.config.ConfiguracaoFirebase;
import com.example.whatsappclone.helper.Base64Custom;
import com.example.whatsappclone.helper.UsuarioFirebase;
import com.example.whatsappclone.model.Mensagem;
import com.example.whatsappclone.model.Usuario;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private TextView textViewNome;
    private CircleImageView circleImageViewFoto;
    private Usuario usuarioDestinatario;
    private TextView mensagemChat;
    private RecyclerView recyclerMensagens;
    private MensagensAdapter mensagensAdapter;
    private List<Mensagem> mensagens = new ArrayList<>();

    //identificador usuarios remetente e destinatario
    private String idUsuario;
    private  String idUsuarioDestinatario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //configuracao toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //configuracao inicial

        textViewNome = findViewById(R.id.textViewNomeUsuarioChat);
        circleImageViewFoto = findViewById(R.id.circleImagemFotoChat);
        mensagemChat = findViewById(R.id.editTextMensagensChat);
        recyclerMensagens = findViewById(R.id.recyclerViewChat);


        //recuperar dados do usuario remetente
        idUsuario = UsuarioFirebase.getIdentificadorUsuario();

        // recuperar dados do usuario

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            usuarioDestinatario = (Usuario) bundle.getSerializable("contato");
            textViewNome.setText(usuarioDestinatario.getNome());

            String foto = usuarioDestinatario.getFoto();

            if (foto != null){
                Uri uri = Uri.parse(usuarioDestinatario.getFoto());
                Glide.with(ChatActivity.this)
                        .load(uri)
                        .into(circleImageViewFoto);
            }else{
                circleImageViewFoto.setImageResource(R.drawable.padrao);
            }
            //recuperar dados do usuario destinatario
            idUsuarioDestinatario = Base64Custom.codificarBase64(usuarioDestinatario.getEmail());
        }


        //configuracao recyclerview

        // configuracao adapter
        mensagensAdapter = new MensagensAdapter(mensagens, getApplicationContext());


        // configuracao recyclerview

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMensagens.setLayoutManager(layoutManager);
        recyclerMensagens.setHasFixedSize(true);
        recyclerMensagens.setAdapter(mensagensAdapter);



    }

    public  void enviarMensagem(View view){
        String mensagem = mensagemChat.getText().toString();



        if (!mensagem.isEmpty()){
            Mensagem msg = new Mensagem();
            msg.setIdUsuario(idUsuario);
            msg.setTexto(mensagem);

            //salvar mensagem
            salvarMendsagem(idUsuario, idUsuarioDestinatario, msg);


        }else {
            Toast.makeText(ChatActivity.this, "Digite uma mensagem", Toast.LENGTH_LONG).show();
        }

    }

    private void salvarMendsagem(String idRemetente, String idDestinatario, Mensagem msg ){
        DatabaseReference database = ConfiguracaoFirebase.getDatabaseReference();
        DatabaseReference mensagemRef = database.child("mensagens");

        mensagemRef.child(idRemetente)
                .child(idDestinatario)
                .push()
                .setValue(msg);
        // limpar texto
        mensagemChat.setText("");


    }
}