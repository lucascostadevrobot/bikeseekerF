package com.app.bike.seeke.view.activitys;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.app.bike.seeke.R;
import com.app.bike.seeke.helper.UsuarioFirebase;
import com.app.bike.seeke.repository.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;

//Classe principal que será responsável por gerenciar outras classes
public class ClassePrincipal extends AppCompatActivity {

    private FirebaseAuth autenticacao;

    //Cria um array de Strings para acesso as permissoes
    private String[] permissoes = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classe_principal);


        //Inicializando e declarando autenticacao para deslogar o usuario
       // autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        //autenticacao.signOut(); //Validar depois com o menu para sair

    }

    //Metodo para abrir a tela de login
    public void abrirTelaLogin(View view){
        startActivity(new Intent(this, LoginActivity.class));
    }
    //Metodo para abrir a tela de cadastro
    public void abrirTelaCadastro(View view){
       startActivity(new Intent(this, CadastroAcitvity.class));
    }

    //Metodo para redirecionar o usuario para a tela do seu tipo sem precisar realizar login toda inicialização do app
    @Override
    protected void onStart() {
        super.onStart();
        UsuarioFirebase.redirecionaUsuarioLogado(ClassePrincipal.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int permissaoResultado : grantResults){
            if( permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }

    }

    //Criando metodo para alertar o usuario caso ele recuse as permissoes em tempo de execução no app
    private void alertaValidacaoPermissao(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }
}