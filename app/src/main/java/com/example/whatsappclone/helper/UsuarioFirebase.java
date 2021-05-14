package com.example.whatsappclone.helper;

import com.example.whatsappclone.config.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;

public class UsuarioFirebase {

    public static String getIdentificadorUsuario(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getAuth();
        String email = usuario.getCurrentUser().getEmail();
        String identificadorUsuario = Base64Custom.codificarBase64(email);

        return identificadorUsuario;
    }



}
