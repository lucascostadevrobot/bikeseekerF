package com.app.bike.seeke.view.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.app.bike.seeke.R;
import com.app.bike.seeke.domain.UsuarioDomain;
import com.app.bike.seeke.helper.UsuarioFirebase;
import com.app.bike.seeke.repository.ConfiguracaoFirebase;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroAcitvity extends AppCompatActivity {

    private TextInputEditText campoNome, campoEmail, campoSenha;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchTipoUsuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_acitvity);

        //incializando os componentes
        campoNome = findViewById(R.id.editCadastroNome);
        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha);
        switchTipoUsuario = findViewById(R.id.editSwitchTipoUsuario);
    }

    public void validarCadastroUsuario(View view){
        //Recuperando os  textos dos Campos quando o usuário digita
        String textoNome = (campoNome.getText()).toString();
        String textoEmail = (campoEmail.getText()).toString();
        String textoSenha = (campoSenha.getText()).toString();


        //Validando se os campos foram preenchidos,
        if ((!textoEmail.isEmpty())){//verifica o E-mail
            if (!textoSenha.isEmpty()) {//verifica senha

                UsuarioDomain usuarioDomain =  new UsuarioDomain();
                usuarioDomain.setNome(textoNome);
                usuarioDomain.setEmail(textoEmail);
                usuarioDomain.setSenha(textoSenha);
                usuarioDomain.setTipo(verificaTipoUsuario());

                //Chamando o Metodo para salvar o usuario
                cadastrarUsuario(usuarioDomain);
            }else {
                Toast.makeText(CadastroAcitvity.this,
                        "Preencha o seu E-mail para prosseguir",
                        Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(CadastroAcitvity.this,
                    "Preencha o seu nome para prosseguir",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //Metodo para cadastrar usuario no firebase
    public void cadastrarUsuario(UsuarioDomain usuarioDomain){
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuarioDomain.getEmail(),
                usuarioDomain.getSenha()
        ).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {

                try {
                    //salvaremos  nosso usuario no banco de dados
                    String idUsuario = task.getResult().getUser().getUid();
                    usuarioDomain.setId(idUsuario);
                    usuarioDomain.salvar();

                    //Atualiza nome no UserPrifile do Firebase
                    UsuarioFirebase.atualizaNomeUsuario(usuarioDomain.getNome() );


                    //Redireciona o usuario com base no seu tipo
                    //Se o usuário for passageiro chama a Activity maps
                    //senão chama a activity requisições
                    if (verificaTipoUsuario() == "P") {
                        startActivity(new Intent(CadastroAcitvity.this, PassageiroActivity.class));
                        finish();

                        Toast.makeText(CadastroAcitvity.this,
                                "Sucesso ao se cadastrar Passageiro",
                                Toast.LENGTH_SHORT).show();

                    } else {

                        startActivity(new Intent(CadastroAcitvity.this, RequisicoesActivity.class));
                        finish();
                        Toast.makeText(CadastroAcitvity.this,
                                "Sucesso ao se cadastrar Motorista",
                                Toast.LENGTH_SHORT).show();

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


            }else{
                String execao = "";
                try {
                    throw task.getException();
                }catch (FirebaseAuthWeakPasswordException e){
                    execao = "Digite uma senha mais forte!";
                }catch (FirebaseAuthInvalidCredentialsException e){
                    execao = "Por favor, digite um -mail válido";
                }catch (FirebaseAuthUserCollisionException e){
                    execao = "Já existe uma conta com esses dados!";
                }catch (Exception e){
                    execao = "Aconteceu um erro ao se cadastrar! Tente novamente." + e.getMessage();
                    e.printStackTrace();
                }

                Toast.makeText(CadastroAcitvity.this,
                        execao,
                        Toast.LENGTH_SHORT).show();
            }
        });

    }
    //Metodo para verificar o tipo do usuario com operadores ternarios
    public String verificaTipoUsuario(){
        return switchTipoUsuario.isChecked() ? "M" : "P" ;

    }
}