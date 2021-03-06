package com.example.whatsappclone.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.activity.ChatActivity;
import com.example.whatsappclone.activity.GrupoActivity;
import com.example.whatsappclone.adapter.AdapterContatos;
import com.example.whatsappclone.adapter.AdapterConversas;
import com.example.whatsappclone.config.ConfiguracaoFirebase;
import com.example.whatsappclone.helper.RecyclerItemClickListener;
import com.example.whatsappclone.model.Conversa;
import com.example.whatsappclone.model.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class ContatosFragment extends Fragment {

private RecyclerView recyclerViewListaContatos;
private AdapterContatos adapter;
private ArrayList<Usuario> listaContatos = new ArrayList<>();
private DatabaseReference usuariosRef;
private ValueEventListener valueEventListenerContatos;
private FirebaseUser usuarioAtual;

    public ContatosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        //configuracoes iniciais
        recyclerViewListaContatos = view.findViewById(R.id.recyclerViewListaContatos);
        usuariosRef = ConfiguracaoFirebase.getDatabaseReference().child("usuarios");
        usuarioAtual = ConfiguracaoFirebase.getAuth().getCurrentUser();


        // confitguracao adapter
        // instanciar adapter e passar a lista como parametro
        adapter = new AdapterContatos(listaContatos , getActivity());




        // configurar recycler view
        //instanciar um gerenciador de layout pegando o contexto da activity
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //define o layout
        recyclerViewListaContatos.setLayoutManager(layoutManager);
        //dertermina o numero de elementos visiveis
        recyclerViewListaContatos.setHasFixedSize(true);
        //defini o adapter
        recyclerViewListaContatos.setAdapter(adapter);

        //configurar evento de click
        recyclerViewListaContatos.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), recyclerViewListaContatos, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        List<Usuario> listaUsuariosAtualizada = adapter.getContatos();

                        Usuario usuarioSelecionado = listaUsuariosAtualizada.get(position);
                        boolean cabecalho = usuarioSelecionado.getEmail().isEmpty();

                        if (cabecalho){
                            Intent intent = new Intent(getActivity(), GrupoActivity.class);
                            startActivity(intent);


                        } else {

                            Intent intent = new Intent(getActivity(), ChatActivity.class);
                            intent.putExtra("contato", usuarioSelecionado );
                            startActivity(intent);
                        }


                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }));


        return view;
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
        listaContatos.clear();
        usuariosRef.removeEventListener(valueEventListenerContatos);
    }

    public void recuperarContatos(){

        Usuario itemGrupo = new Usuario();
        itemGrupo.setNome("Novo Grupo");
        itemGrupo.setEmail("");

        listaContatos.add(itemGrupo);



        valueEventListenerContatos = usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dados: snapshot.getChildren()){
                    Usuario usuario = dados.getValue(Usuario.class);

                    String emailUsuarioAtual = usuarioAtual.getEmail();

                    if (!emailUsuarioAtual.equals(usuario.getEmail())){
                        listaContatos.add(usuario);

                    }

                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void pesquisarContatos(String texto){

        List<Usuario> listaContatosBusca = new ArrayList<>();

        for ( Usuario usuario : listaContatos ) {
            String nome = usuario.getNome().toLowerCase();
            if (nome.contains(texto)){
                listaContatosBusca.add(usuario);
            }
        }

        adapter = new AdapterContatos(listaContatosBusca, getActivity());
        recyclerViewListaContatos.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    public void recarregarContatos() {
        adapter = new AdapterContatos(listaContatos, getActivity());
        recyclerViewListaContatos.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}