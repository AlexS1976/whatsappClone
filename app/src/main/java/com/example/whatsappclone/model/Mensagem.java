package com.example.whatsappclone.model;

public class Mensagem {

    private String texto;
    private  String idUsuario;
    private  String imagem;
    private String nome;

    public Mensagem() {

        this.setNome("");
    }

    public String getTexto() {

        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
