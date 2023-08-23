package com.app.bike.seeke.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.bike.seeke.R;
import com.app.bike.seeke.domain.UsuarioDomain;
import com.app.bike.seeke.repository.ConfiguracaoFirebase;
import com.app.bike.seeke.view.activitys.PassageiroActivity;
import com.app.bike.seeke.view.activitys.RequisicoesActivity;
import com.app.bike.seeke.view.activitys.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class UsuarioFirebase {

    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();
    }


    //Metodo para recuperar os dados de quando o usuario estiver logado
    public static UsuarioDomain getDadosUsuarioLogado(){
        FirebaseUser firebaseUser = getUsuarioAtual();
        UsuarioDomain usuarioDomain = new UsuarioDomain();
        usuarioDomain.setId(firebaseUser.getUid());
        usuarioDomain.setEmail(firebaseUser.getEmail());
        usuarioDomain.setNome(firebaseUser.getDisplayName());
        return usuarioDomain;
    }


    public static boolean atualizaNomeUsuario(String nome){

        try {

            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nome)
                    .build();

            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()){
                        Log.d("Perfil", "Erro ao atualizar o seu nome de perfil");
                    }

                }
            });

            return true;

        }catch ( Exception e){
            e.printStackTrace();
            return false;
        }

    }

    //Toda vez que o metodo for chamado ele irá redirecionar o usuário para tela do seu tipo de usuário
    //Para isso precisamos realizar consulta em nosso banco de dados para saber se o usuário existe ou não ou se é Motorista ou Passageiro
    public static void redirecionaUsuarioLogado(Activity activity){
         FirebaseUser user = getUsuarioAtual();

        if (user != null){
            DatabaseReference usuarioRef = ConfiguracaoFirebase.getFirebaseDatabase()
                    .child("usuarios")
                    .child(getIdentificadorUsuarioId()) ;
            usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UsuarioDomain usuarioDomain = snapshot.getValue(UsuarioDomain.class);
                    String tipoUsuario = usuarioDomain != null ? usuarioDomain.getTipo() : null; //Recuperando o tipo do usuario
                    if (tipoUsuario != null && tipoUsuario.equals("M")){
                        // Se for um motorista, inicie o fragmento HomeFragment
                        Intent intent = new Intent(activity, RequisicoesActivity.class);
                        activity.startActivity(intent);
                    }else {
                        Intent intent = new Intent(activity, PassageiroActivity.class);
                        activity.startActivity(intent);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    //Metodo que retorna o ID direto do Usuario no BD
    public static String  getIdentificadorUsuarioId(){
        return  getUsuarioAtual().getUid();
    }
}
