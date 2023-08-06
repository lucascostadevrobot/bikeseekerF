package com.app.bike.seeke.view.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.app.bike.seeke.R;
import com.app.bike.seeke.domain.UsuarioDomain;
import com.app.bike.seeke.helper.UsuarioFirebase;
import com.app.bike.seeke.repository.ConfiguracaoFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class LoginActivity extends AppCompatActivity {

    //Declarando os atributos
    private TextInputEditText campoEmail, campoSenha;
    private FirebaseAuth autenticacao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Iinicializar nossos componentes fazendo com que os atributos recebam os componentes
        campoEmail = findViewById(R.id.editLoginEmail);
        campoSenha = findViewById(R.id.editLoginSenha);
    }

    //Criando metodo através do onClick validarLoginUsuario btn tela de login
    public void validarLoginUsuario(View view){

        //Recuperar as informações e textos dos Campos
         String textoEmail = campoEmail.getText().toString();
         String textoSenha = campoSenha.getText().toString();

        if (!textoEmail.isEmpty()){//Verifica e-mail
            if (!textoSenha.isEmpty()){//Verifica senha

                UsuarioDomain usuarioDomain = new UsuarioDomain();
                usuarioDomain.setEmail(textoEmail);
                usuarioDomain.setSenha(textoSenha);

                logarUsuario(usuarioDomain);

            }else{

                Toast.makeText(LoginActivity.this, "Digte sua senha", Toast.LENGTH_SHORT).show();
            }

        }else{

            Toast.makeText(LoginActivity.this, "Preencha o E-mail", Toast.LENGTH_SHORT).show();
        }
    }
    //Metodo para autenticar usuario no app
    public void logarUsuario(UsuarioDomain usuarioDomain){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                usuarioDomain.getEmail(),
                usuarioDomain.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){//Caso consiga autenticar

                    //Verificar o tipo de usuário logado
                    //"Motorista" / "Passageiro"
                    //Para realizar a verificação de qual tipo o usuário será criamos um metodo na classe UsuarioFirebae
                    UsuarioFirebase.redirecionaUsuarioLogado(LoginActivity.this);


                }else { //Validação caso usuario digite dados errados
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

                    Toast.makeText(LoginActivity.this,
                            execao,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}