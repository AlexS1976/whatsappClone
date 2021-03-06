package com.example.whatsappclone.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.R;
import com.example.whatsappclone.model.Conversa;
import com.example.whatsappclone.model.Grupo;
import com.example.whatsappclone.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterConversas  extends RecyclerView.Adapter<AdapterConversas.MyViewHolder> {

    private  List<Conversa> conversas;
    private Context context;

    public AdapterConversas(List<Conversa> lista, Context c) {
        this.conversas = lista;
        this.context = c;
    }

    public List<Conversa> getConversas(){
        return this.conversas;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.contatos_layout, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Conversa conversa = conversas.get(position);
        holder.ultimaMensagem.setText(conversa.getUltimaMensagem());

        if (conversa.getIsGroup().equals("true")){

            Grupo grupo = conversa.getGrupo();
            holder.nome.setText(grupo.getNome());

            if (grupo.getFoto() != null){

                Uri url = Uri.parse(grupo.getFoto());
                Glide.with(context).load(url).into(holder.foto);
            }else {
                holder.foto.setImageResource(R.drawable.padrao);
            }

        } else{
                Usuario usuario = conversa.getUsuarioExibido();

            if (usuario != null){

                holder.nome.setText(usuario.getNome());



                if (usuario.getFoto() != null){

                    Uri url = Uri.parse(usuario.getFoto());
                    Glide.with(context).load(url).into(holder.foto);


                }else {
                    holder.foto.setImageResource(R.drawable.padrao);
                }

            }

            }





    }

    @Override
    public int getItemCount() {
        return conversas.size();
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView foto;
        TextView nome;
        TextView ultimaMensagem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.fotoContato);
            nome = itemView.findViewById(R.id.textNomeContato);
            ultimaMensagem = itemView.findViewById(R.id.textEmailContato);

        }
    }
}
