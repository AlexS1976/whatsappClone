package com.example.whatsappclone.model;

import com.example.whatsappclone.config.ConfiguracaoFirebase;
import com.example.whatsappclone.helper.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Usuario implements Serializable {
    private String id;
    private String nome;
    private String email;
    private String senha;
    private  String foto;

    public Usuario() {

    }

    public void salvar(){
        DatabaseReference databaseReference = ConfiguracaoFirebase.getDatabaseReference();
        DatabaseReference usuario = databaseReference.child("usuarios").child(getId());
        usuario.setValue(this);

    }

    public void atualizar(){
        String idUsuario = UsuarioFirebase.getIdentificadorUsuario();
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getDatabaseReference();

        DatabaseReference usuarioRef = firebaseRef.child("usuarios")
                .child(idUsuario);

        Map<String, Object> valoresUsuario = converterParaMap();

        usuarioRef.updateChildren(valoresUsuario);


    }
@Exclude
    public Map<String, Object> converterParaMap(){
        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("email", getEmail());
        usuarioMap.put("nome", getNome());
        usuarioMap.put("foto", getFoto());

        return usuarioMap;
    }

@Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
@Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}

