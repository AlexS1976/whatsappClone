package com.example.whatsappclone.activity;

import android.os.Bundle;

import com.example.whatsappclone.adapter.AdapterContatos;
import com.example.whatsappclone.adapter.GrupoSelecionadoAdapter;
import com.example.whatsappclone.config.ConfiguracaoFirebase;
import com.example.whatsappclone.helper.RecyclerItemClickListener;
import com.example.whatsappclone.helper.UsuarioFirebase;
import com.example.whatsappclone.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.AdapterView;

import com.example.whatsappclone.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GrupoActivity extends AppCompatActivity {

    private RecyclerView membrosSelecionados, recyclerMembros;
    private AdapterContatos adapterContatos;
    private GrupoSelecionadoAdapter grupoSelecionadoAdapter;
    private ArrayList <Usuario> listaMembros = new ArrayList<>();
    private ArrayList <Usuario> listaMembrosSelecionados = new ArrayList<>();
    private ValueEventListener valueEventListenerMembros;
    private FirebaseUser usuarioAtual;
    private  Toolbar toolbar;

    //referencias
    DatabaseReference usuariosRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo grupo");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);



        //configuracao inicial

        recyclerMembros = findViewById(R.id.membros);
        membrosSelecionados = findViewById(R.id.membrosSelecionados);

        usuariosRef = ConfiguracaoFirebase.getDatabaseReference().child("usuarios");
        usuarioAtual = UsuarioFirebase.getUsuarioAtual();

        //configuracao adapter

        adapterContatos = new AdapterContatos(listaMembros, getApplicationContext());

        // configuracao recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerMembros.setLayoutManager(layoutManager);
        recyclerMembros.setHasFixedSize(true);
        recyclerMembros.setAdapter(adapterContatos);

        recyclerMembros.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerMembros, new
                RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Usuario usuarioSelecionado = listaMembros.get(position);

                        //remover usuarioSelecionado da lista
                        listaMembros.remove(usuarioSelecionado);
                        adapterContatos.notifyDataSetChanged();

                        //adicionar usuario selecionado ao grupo
                        listaMembrosSelecionados.add(usuarioSelecionado);
                        grupoSelecionadoAdapter.notifyDataSetChanged();
                        atualizarMembrosToolbar();


                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }));


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });

        //configuracao adapter grupo selecionado

        grupoSelecionadoAdapter = new GrupoSelecionadoAdapter(listaMembrosSelecionados, getApplicationContext());

        // configuracao recycler view
        RecyclerView.LayoutManager layoutManagerHorizontal = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        membrosSelecionados.setLayoutManager(layoutManagerHorizontal);
        membrosSelecionados.setHasFixedSize(true);
        membrosSelecionados.setAdapter(grupoSelecionadoAdapter);

        membrosSelecionados.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), membrosSelecionados, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Usuario usuarioSelecionado = listaMembrosSelecionados.get(position);
                //remover listagem de membros selecionados
                listaMembrosSelecionados.remove(usuarioSelecionado);
                grupoSelecionadoAdapter.notifyDataSetChanged();

                // Adicionar a listagem de membros
                listaMembros.add(usuarioSelecionado);
                adapterContatos.notifyDataSetChanged();
                atualizarMembrosToolbar();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        }));

    }

    public void recuperarContatos(){

        valueEventListenerMembros = usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dados: snapshot.getChildren()){
                    Usuario usuario = dados.getValue(Usuario.class);

                    String emailUsuarioAtual = usuarioAtual.getEmail();

                    if (!emailUsuarioAtual.equals(usuario.getEmail())){
                        listaMembros.add(usuario);
                    }

                }

             adapterContatos.notifyDataSetChanged();
            atualizarMembrosToolbar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void atualizarMembrosToolbar(){
        int totalSelecionados = listaMembrosSelecionados.size();
        int total = listaMembros.size() + listaMembrosSelecionados.size();
        toolbar.setSubtitle(totalSelecionados + " de  " + total + " selecionados");

    }

    @Override
    public void onStart() {
        super.onStart();


        recuperarContatos();


    }

    @Override
    public void onStop() {
        super.onStop();
        //limpar contatos
        listaMembros.clear();
        usuariosRef.removeEventListener(valueEventListenerMembros);
    }
}