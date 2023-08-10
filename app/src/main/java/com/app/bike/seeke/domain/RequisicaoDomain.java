package com.app.bike.seeke.domain;

import com.app.bike.seeke.repository.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class RequisicaoDomain {
    private String id;
    private String status;
    private UsuarioDomain passageiro;
    private UsuarioDomain motorista;
    private  DestinoDomain destinoDomain;

    /*Enquanto o passageiro realizar a requisicao de confirmar a viagem, o status para ele será de
     aguardando. Até que o Piloto aceite a corrida

     Pode ser criada uma classe apenas para os Status Requisicoes
     */
    public static final String STATUS_AGUARDANDO = "Aguardando";

    /*Status que aparecerá assim que o Motorista aceitar a requisicao do usuario*/
    public static final String STATUS_A_CAMINHO = "Indo até você!";

    /*Status que aparecerá assim que o Passageiro estiver em viagem com o piloto da moto*/
    public static final String STATUS_VIAGEM= "Em viagem com passageiro!";

    /*Status que aparecerá quando o Piloto finalizar a viagem com o passageiro.*/
    public static final String STATUS_FINALIZADA= "Corrida finalizada!";


    //Construtor vazio
    public RequisicaoDomain() {
    }

    /*//Construtor com parametros
    public RequisicaoDomain(String id, String status, UsuarioDomain passageiro) {
        this.id = id;
        this.status = status;
        this.passageiro = passageiro;
    }*/


    //Metodo salvar requisição no banco de dados firebase
    public void salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference requisicoes = firebaseRef.child("requisicoes"); //salva a requisicao em no no BD

        String idRequisicao = requisicoes.push().getKey(); //Recuperar a chave da requisicao
        setId(idRequisicao);

        requisicoes.child(getId()).setValue(this); //Salvando todos os dados da requisicao do firebase
    }

    public void atualizarRequisicao(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference requisicoes = firebaseRef.child("requisicoes"); //salva a requisicao em no no BD
        DatabaseReference requisicao = requisicoes.child(getId());

        //Parte que realiza a atualização na requisicao direto no banco de dados, através do requisicoes e requisicao
        Map<String, Object> objeto = new HashMap<String, Object>();
        objeto.put("motorista", getMotorista());
        objeto.put("status", getStatus());

        requisicao.updateChildren(objeto);

    }




    //Getters e Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UsuarioDomain getPassageiro() {
        return passageiro;
    }

    public void setPassageiro(UsuarioDomain passageiro) {
        this.passageiro = passageiro;
    }

    public UsuarioDomain getMotorista() {
        return motorista;
    }

    public void setMotorista(UsuarioDomain motorista) {
        this.motorista = motorista;
    }

    public DestinoDomain getDestinoDomain() {
        return destinoDomain;
    }

    public void setDestinoDomain(DestinoDomain destinoDomainn) {
        this.destinoDomain = destinoDomainn;
    }
}
