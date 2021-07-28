package com.example.whatsappclone.model;

import com.example.whatsappclone.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Conversa {

    private String idRemetente;
    private String idDestinatario;
    private String ultimaMensagem;
    private Usuario usuarioExibido;

    //variavel para identificar se é um grupo
    private String isGroup;
    private Grupo grupo;

    public Conversa() {
        this.setIsGroup("false");
    }

    public void salvar(){
        DatabaseReference database = ConfiguracaoFirebase.getDatabaseReference();
        DatabaseReference conversaRef = database.child("conversas");
        conversaRef.child(this.getIdRemetente())
                .child(this.getIdDestinatario())
                .setValue(this);

    }

    public String getIdRemetente() {
        return idRemetente;
    }

    public void setIdRemetente(String idRemetente) {
        this.idRemetente = idRemetente;
    }

    public String getIdDestinatario() {
        return idDestinatario;
    }

    public void setIdDestinatario(String idDestinatario) {
        this.idDestinatario = idDestinatario;
    }

    public String getUltimaMensagem() {
        return ultimaMensagem;
    }

    public void setUltimaMensagem(String ultimaMensagem) {
        this.ultimaMensagem = ultimaMensagem;
    }

    public Usuario getUsuarioExibido() {
        return usuarioExibido;
    }

    public void setUsuarioExibido(Usuario usuarioExibido) {
        this.usuarioExibido = usuarioExibido;
    }

    public String getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(String isGroup) {
        this.isGroup = isGroup;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }
}
