package com.example.whatsappclone.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.MensagensAdapter;
import com.example.whatsappclone.config.ConfiguracaoFirebase;
import com.example.whatsappclone.helper.Base64Custom;
import com.example.whatsappclone.helper.UsuarioFirebase;
import com.example.whatsappclone.model.Conversa;
import com.example.whatsappclone.model.Grupo;
import com.example.whatsappclone.model.Mensagem;
import com.example.whatsappclone.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private TextView textViewNome;
    private ImageView imageCamera;
    private static final int SELECAO_CAMERA = 100;
    private CircleImageView circleImageViewFoto;
    private Usuario usuarioDestinatario;
    private TextView mensagemChat;
    private RecyclerView recyclerMensagens;
    private MensagensAdapter mensagensAdapter;
    private List<Mensagem> mensagens = new ArrayList<>();
    private ChildEventListener childEventListenerMensagens;
    private Grupo grupo;
    private Usuario usuarioRemetente;

    //firebese reference
    private  DatabaseReference database;
    private DatabaseReference mensagensRef;
    private StorageReference storege;

    //identificador usuarios remetente e destinatario
    private String idUsuarioRemetente;
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
        imageCamera = findViewById(R.id.imageViewCameraChat);


        //recuperar dados do usuario remetente
        idUsuarioRemetente = UsuarioFirebase.getIdentificadorUsuario();
        usuarioRemetente = UsuarioFirebase.getDadosUsuario();

        // recuperar dados do usuario

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            if (bundle.containsKey("chatGrupo")){
                grupo = (Grupo) bundle.getSerializable("chatGrupo");
                idUsuarioDestinatario = grupo.getId();

                textViewNome.setText(grupo.getNome());

                String foto = grupo.getFoto();

                if (foto != null){
                    Uri uri = Uri.parse(foto);
                    Glide.with(ChatActivity.this)
                            .load(uri)
                            .into(circleImageViewFoto);
                }else{
                    circleImageViewFoto.setImageResource(R.drawable.padrao);
                }
            }else {

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


        }


        // configuracao adapter
        mensagensAdapter = new MensagensAdapter(mensagens, getApplicationContext());


        // configuracao recyclerview

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMensagens.setLayoutManager(layoutManager);
        recyclerMensagens.setHasFixedSize(true);
        recyclerMensagens.setAdapter(mensagensAdapter);

        database = ConfiguracaoFirebase.getDatabaseReference();
        storege = ConfiguracaoFirebase.getSoregeReference();
        mensagensRef = database.child("mensagens")
                .child(idUsuarioRemetente)
                .child(idUsuarioDestinatario);

        //evento de clck da camera
        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent, SELECAO_CAMERA );

                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            Bitmap imagem = null;

            try {
                switch (requestCode){
                    case SELECAO_CAMERA:
                        imagem = (Bitmap) data.getExtras().get("data");
                        break;
                }
                if (imagem != null){

                    //Recuperar dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //configurar id da imagem
                    String nomeImagem = UUID.randomUUID().toString();

                    //configurar referencias do firebase
                    final StorageReference imageRef = storege.child("imagens")
                            .child("fotos")
                            .child(idUsuarioRemetente)
                            .child(nomeImagem);

                    UploadTask uploadTask = imageRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChatActivity.this, "falha ao salvar a imagem", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                   String url = task.getResult().toString();
                                   Mensagem mensagem = new Mensagem();
                                   mensagem.setIdUsuario(idUsuarioRemetente);
                                   mensagem.setTexto("mensagem que deve ser ocultada");
                                   mensagem.setImagem(url);

                                   // salvar mensagem para usuario remetente
                                   salvarMendsagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);

                                   // salvar mansagem para usuario destinatario
                                    salvarMendsagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);

                                }
                            });

                        }
                    });


                }

            }catch (Exception e){
                e.printStackTrace();
            }


        }
    }

    public  void enviarMensagem(View view){
        String mensagem = mensagemChat.getText().toString();

        if (!mensagem.isEmpty()){

            if (usuarioDestinatario != null){
                Mensagem msg = new Mensagem();
                msg.setIdUsuario(idUsuarioRemetente);
                msg.setTexto(mensagem);

                //salvar mensagem remetente
                salvarMendsagem(idUsuarioRemetente, idUsuarioDestinatario, msg);

                //salvar mensagem destinatario
                salvarMendsagem(idUsuarioDestinatario, idUsuarioRemetente , msg);

                // salvar conversa remetente no fragment conversas
                salvarConveras(idUsuarioRemetente, idUsuarioDestinatario, usuarioRemetente, msg, false);
                // salvar conversa destinatario

                salvarConveras( idUsuarioDestinatario, idUsuarioRemetente, usuarioDestinatario, msg, false);


            }else{
                for (Usuario membro : grupo.getMembros()){
                    String idRemetenteGrupo = Base64Custom.codificarBase64(membro.getEmail());
                    String idUsuarioLogadoGrupo = UsuarioFirebase.getIdentificadorUsuario();

                    Mensagem msg = new Mensagem();
                    msg.setIdUsuario(idUsuarioLogadoGrupo);
                    msg.setTexto(mensagem);
                    msg.setNome(usuarioRemetente.getNome());

                    //salvar mensagem para o membro
                    salvarMendsagem(idRemetenteGrupo, idUsuarioDestinatario, msg);

                    // salvar conversa no fragment conversas
                    salvarConveras(idRemetenteGrupo, idUsuarioDestinatario, usuarioDestinatario, msg, true);
                }

            }
        }else {
                Toast.makeText(ChatActivity.this, "Digite uma mensagem", Toast.LENGTH_LONG).show();
        }
    }

    private void salvarConveras(String idRemetente, String idDestinatario, Usuario usuarioExibicao,  Mensagem msg, boolean isGroup ){

        Conversa conversaRemetente = new Conversa();
        conversaRemetente.setIdRemetente(idRemetente);
        conversaRemetente.setIdDestinatario(idDestinatario);
        conversaRemetente.setUltimaMensagem(msg.getTexto());

        if (isGroup){

           conversaRemetente.setIsGroup("true");
           conversaRemetente.setGrupo(grupo);

        }else{//conversa normal

            conversaRemetente.setUsuarioExibido(usuarioExibicao);
            conversaRemetente.setIsGroup("false");

        }
        conversaRemetente.salvar();

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

    @Override
    protected void onStart() {
        super.onStart();
        recuperarMensagens();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mensagensRef.removeEventListener(childEventListenerMensagens);
    }

    private void recuperarMensagens(){

     childEventListenerMensagens = mensagensRef.addChildEventListener(new ChildEventListener() {
         @Override
         public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
             Mensagem mensagem = snapshot.getValue(Mensagem.class);
             mensagens.add(mensagem);
             mensagensAdapter.notifyDataSetChanged();
         }

         @Override
         public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

         }

         @Override
         public void onChildRemoved(@NonNull DataSnapshot snapshot) {

         }

         @Override
         public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

         }

         @Override
         public void onCancelled(@NonNull DatabaseError error) {

         }
     });


    }

}