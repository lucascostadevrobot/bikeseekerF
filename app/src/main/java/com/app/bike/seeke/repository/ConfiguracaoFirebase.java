package com.app.bike.seeke.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//Com essa classe podemos criar os metodos que irão retornar os objetos para database firebase
public class ConfiguracaoFirebase {


    //variaveis do tipo objeto referencia firebase
    private static DatabaseReference database;
    private static FirebaseAuth auth;

    //Retornando as instancias do firebasedatabase
    //Ou seja, metodo para instanciar e retornar as instancia para minha base de dados..
    public static DatabaseReference getFirebaseDatabase(){
        if(database == null){
            database = FirebaseDatabase.getInstance().getReference();
        }
        return  database;
    }
    //Retornando as instancias do firebaseauth
    //Ou seja, metodo para instanciar e retornar as instancia para autenticação do usuario
    public static FirebaseAuth getFirebaseAutenticacao(){

        if(auth == null){
            //Utilizando somente getInstance para recuperação da autenticacao
            auth = FirebaseAuth.getInstance();
        }

        return auth;
    }
}
