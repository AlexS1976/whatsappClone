package com.example.whatsappclone.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfiguracaoFirebase {
    private static DatabaseReference databaseReference;
    private static FirebaseAuth auth;

    //retornar a intancia do firebase Database
    public static  DatabaseReference getDatabaseReference(){
        if (databaseReference == null){
            databaseReference = FirebaseDatabase.getInstance().getReference();
        }
        return databaseReference;
    }
    //retornar a intancia do firebase auth
    public static FirebaseAuth getAuth(){
        if (auth == null){
            auth = FirebaseAuth.getInstance();

        }
        return auth;
    }


}
