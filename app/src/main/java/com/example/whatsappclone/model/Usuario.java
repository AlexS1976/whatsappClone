package com.example.whatsappclone.model;

import com.example.whatsappclone.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

public class Usuario {
    private String id;
    private String nome;
    private String email;
    private String senha;
    private String UrlImagemPerfil;

    public Usuario() {
    }

    public void salvar(){
        DatabaseReference databaseReference = ConfiguracaoFirebase.getDatabaseReference();
        DatabaseReference usuario = databaseReference.child("usuarios").child(getId());
        usuario.setValue(this);


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

    public String getUrlImagemPerfil() {
        return UrlImagemPerfil;
    }

    public void setUrlImagemPerfil(String urlImagemPerfil) {
        UrlImagemPerfil = urlImagemPerfil;
    }
}

