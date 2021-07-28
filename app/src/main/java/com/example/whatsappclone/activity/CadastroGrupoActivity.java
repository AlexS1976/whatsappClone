package com.example.whatsappclone.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.example.whatsappclone.adapter.GrupoSelecionadoAdapter;
import com.example.whatsappclone.config.ConfiguracaoFirebase;
import com.example.whatsappclone.helper.RecyclerItemClickListener;
import com.example.whatsappclone.helper.UsuarioFirebase;
import com.example.whatsappclone.model.Grupo;
import com.example.whatsappclone.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsappclone.R;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CadastroGrupoActivity extends AppCompatActivity {

    private ArrayList<Usuario> listaMembrosSelecionados = new ArrayList<>();
    private TextView textNomeGrupo;
    private TextView totalMembrosGrupo;
    private RecyclerView menbrosSelecionados;
    private GrupoSelecionadoAdapter grupoSelecionadoAdapter;
    private CircleImageView imagemGrupo;
    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;
    private Grupo grupo;
    private FloatingActionButton fabSalvarGrupo;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_grupo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("novo grupo");
        toolbar.setSubtitle("Defina o nome");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //configuracoes iniciais

        storageReference = ConfiguracaoFirebase.getSoregeReference();

        // inicializar componentes
        textNomeGrupo = findViewById(R.id.editNomeGrupo);
        totalMembrosGrupo = findViewById(R.id.textTotalParticipantes);
        menbrosSelecionados = findViewById(R.id.recyclerMembrosGrupo);
        imagemGrupo = findViewById(R.id.imageGrupo);
        fabSalvarGrupo= findViewById(R.id.fabGrupoSalvar);
        grupo = new Grupo();

        //evento de click da imagem do grupo

        imagemGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent, SELECAO_GALERIA);
                }

            }
        });



        //recuperar lista de membros passada da grupo activity
        if (getIntent().getExtras() != null ){
            List<Usuario> membros = (List<Usuario>) getIntent().getExtras().getSerializable("selecionados");
           listaMembrosSelecionados.addAll(membros);
           totalMembrosGrupo.setText("Participantes: " + listaMembrosSelecionados.size());


        }

        //configuracao adapter grupo selecionado

        grupoSelecionadoAdapter = new GrupoSelecionadoAdapter(listaMembrosSelecionados, getApplicationContext());

        // configuracao recycler view
        RecyclerView.LayoutManager layoutManagerHorizontal = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        menbrosSelecionados.setLayoutManager(layoutManagerHorizontal);
        menbrosSelecionados.setHasFixedSize(true);
        menbrosSelecionados.setAdapter(grupoSelecionadoAdapter);

        fabSalvarGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nomeGrupo = textNomeGrupo.getText().toString();

                //adicionar ao grupo o usuário que esta logado
                listaMembrosSelecionados.add(UsuarioFirebase.getDadosUsuario());
                grupo.setMembros(listaMembrosSelecionados);
                grupo.setNome(nomeGrupo);
                grupo.salvar();


            }
        });



        menbrosSelecionados.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), menbrosSelecionados, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Usuario usuarioSelecionado = listaMembrosSelecionados.get(position);
                //remover listagem de membros selecionados
                listaMembrosSelecionados.remove(usuarioSelecionado);
                grupoSelecionadoAdapter.notifyDataSetChanged();

                // Adicionar a listagem de membros
                //listaMembros.add(usuarioSelecionado);
                //adapterContatos.notifyDataSetChanged();
                //atualizarMembrosToolbar();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        }));


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap imagem = null;

            try {

                Uri localImagemSelecionada = data.getData();
                imagem = MediaStore.Images.Media.getBitmap(getContentResolver(),localImagemSelecionada);
                imagemGrupo.setImageBitmap(imagem);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] dadosImagem = baos.toByteArray();

                //salvar imagem no firebase
                final StorageReference imagemRef = storageReference
                        .child("imagens")
                        .child("grupos")
                        .child( grupo.getId() + ".jpeg");
                UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Falha ao carregar a imagem",Toast.LENGTH_LONG).show();

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(),"SUCESSO",Toast.LENGTH_LONG).show();
                        //recuperar a url da imagem
                        imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                String  url = task.getResult().toString();
                                grupo.setFoto(url);

                            }
                        });


                    }
                });


            } catch (Exception e ){
                e.printStackTrace();
                Toast.makeText(this, "falha", Toast.LENGTH_SHORT).show();
            }


        }
    }
}