package com.example.whatsappclone.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.R;
import com.example.whatsappclone.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterContatos  extends RecyclerView.Adapter<AdapterContatos.MyViewHolder> {

    private List<Usuario> contatos;
    private Context context;

    public AdapterContatos(List<Usuario> listaContatos, Context c) {
        this.contatos = listaContatos;
        this.context = c;
    }

    public List<Usuario> getContatos() {
        return this.contatos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contatos_layout, parent, false);

        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Usuario usuario = contatos.get(position);
        boolean cabecalho = usuario.getEmail().isEmpty();

        if (cabecalho){
            holder.nome.setText(usuario.getNome());
            holder.email.setVisibility(View.GONE);


        } else{
            holder.nome.setText(usuario.getNome());
            holder.email.setText(usuario.getEmail());
        }
        if (cabecalho){
            holder.foto.setImageResource(R.drawable.icone_grupo);

        }else{
            if (usuario.getFoto() != null){
                Uri uri = Uri.parse(usuario.getFoto());
                Glide.with(context).load(uri).into(holder.foto);

            } else { holder.foto.setImageResource(R.drawable.padrao);

            }
        }




    }

    @Override
    public int getItemCount() {
        return contatos.size() ;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView foto;
        TextView email;
        TextView nome;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.fotoContato);
            email = itemView.findViewById(R.id.textEmailContato);
            nome = itemView.findViewById(R.id.textNomeContato);
        }
    }


}
