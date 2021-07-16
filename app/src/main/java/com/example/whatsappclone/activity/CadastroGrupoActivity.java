package com.example.whatsappclone.activity;

import android.os.Bundle;

import com.example.whatsappclone.adapter.GrupoSelecionadoAdapter;
import com.example.whatsappclone.helper.RecyclerItemClickListener;
import com.example.whatsappclone.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.whatsappclone.R;

import java.util.ArrayList;
import java.util.List;

public class CadastroGrupoActivity extends AppCompatActivity {

    private ArrayList<Usuario> listaMembrosSelecionados = new ArrayList<>();
    private TextView textNomeGrupo;
    private TextView totalMembrosGrupo;
    private RecyclerView menbrosSelecionados;
    private GrupoSelecionadoAdapter grupoSelecionadoAdapter;



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

        // inicializar componentes
        textNomeGrupo = findViewById(R.id.editNomeGrupo);
        totalMembrosGrupo = findViewById(R.id.textTotalParticipantes);
        menbrosSelecionados = findViewById(R.id.recyclerMembrosGrupo);



        FloatingActionButton fab = findViewById(R.id.fabGrupoSalvar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
}