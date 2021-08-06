package com.example.whatsappclone.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.activity.ChatActivity;
import com.example.whatsappclone.adapter.AdapterConversas;
import com.example.whatsappclone.config.ConfiguracaoFirebase;
import com.example.whatsappclone.helper.RecyclerItemClickListener;
import com.example.whatsappclone.helper.UsuarioFirebase;
import com.example.whatsappclone.model.Conversa;
import com.example.whatsappclone.model.Usuario;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;


public class ConversaFragment extends Fragment {

    private RecyclerView recyclerView;

    //parametros para o adapter
    private List<Conversa> listaConversas = new ArrayList<>();
    private AdapterConversas adapter;

    //refenencia firebase
    private DatabaseReference databaseReference;
    private  DatabaseReference conversasRef;

    private  ChildEventListener childEventListenerConversas;


    public ConversaFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversa, container, false);

        recyclerView = view.findViewById(R.id.recyclerConversas);

        // configuracao adapter
        adapter = new AdapterConversas(listaConversas, getActivity());


        // confoguracao recyclerview

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        //configurar evento de click
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Conversa conversaSelecionada = listaConversas.get(position);

                if(conversaSelecionada.getIsGroup().equals("true")){

                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra("chatGrupo", conversaSelecionada.getGrupo() );
                    startActivity(intent);

                }else{

                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra("contato", conversaSelecionada.getUsuarioExibido() );
                    startActivity(intent);

                }


            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        }
        ));


        //configura conversasref
        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        databaseReference = ConfiguracaoFirebase.getDatabaseReference();
        conversasRef = databaseReference.child("conversas")
                .child(identificadorUsuario);
    return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        recuperarConversas();
    }

    @Override
    public void onStop() {
        super.onStop();
        conversasRef.removeEventListener(childEventListenerConversas);
    }

    public void pesquisarConversas(String texto){

    List<Conversa> listaConversaBusca = new ArrayList<>();

        for ( Conversa conversa : listaConversas ){

            if( conversa.getUsuarioExibido() != null ){
                String nome = conversa.getUsuarioExibido().getNome().toLowerCase();
                String ultimaMsg = conversa.getUltimaMensagem().toLowerCase();

                if( nome.contains( texto ) || ultimaMsg.contains( texto ) ){
                    listaConversaBusca.add( conversa );
                }
            }else {
                String nome = conversa.getGrupo().getNome().toLowerCase();
                String ultimaMsg = conversa.getUltimaMensagem().toLowerCase();

                if( nome.contains( texto ) || ultimaMsg.contains( texto ) ){
                    listaConversaBusca.add( conversa );
                }
            }


        }
    adapter = new AdapterConversas(listaConversaBusca, getActivity());
    recyclerView.setAdapter(adapter);
    adapter.notifyDataSetChanged();


    }

    public void recarregarConversas(){
        adapter = new AdapterConversas(listaConversas, getActivity());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void recuperarConversas(){



        childEventListenerConversas = conversasRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                // recuperar conversas
                Conversa conversa = snapshot.getValue(Conversa.class);
                listaConversas.add(conversa);
                adapter.notifyDataSetChanged();


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