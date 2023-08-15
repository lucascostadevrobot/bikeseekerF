package com.app.bike.seeke.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.bike.seeke.R;
import com.app.bike.seeke.domain.DestinoDomain;
import com.app.bike.seeke.domain.RequisicaoDomain;
import com.app.bike.seeke.domain.UsuarioDomain;
import com.app.bike.seeke.helper.UsuarioFirebase;

import java.util.List;

public class PerfilMotoristaAdapter extends RecyclerView.Adapter {

    //List<UsuarioDomain> listUsuariosPerfil;
    RequisicaoDomain requisicaoDomain;

    public PerfilMotoristaAdapter(RequisicaoDomain requisicaoDomain) {
        this.requisicaoDomain = requisicaoDomain;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.informacao_motorista_perfil2, parent, false);
        ViewHolderClass viewHolderClass = new ViewHolderClass(view);

        return viewHolderClass;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        UsuarioDomain usuarioPassageiroLogado = UsuarioFirebase.getDadosUsuarioLogado();

        ViewHolderClass viewHolderClass = (ViewHolderClass) holder;
         requisicaoDomain.getMotorista();

        requisicaoDomain.setMotorista(usuarioPassageiroLogado);
        UsuarioDomain motorista = requisicaoDomain.getMotorista();




        viewHolderClass.emailMotorista.setText(motorista.getEmail());
        viewHolderClass.tipoUsuarioMotorista.setText(motorista.getTipo());
        viewHolderClass.mensagemSuporte.setText(R.string.texto_mesangem_suporte_motorista);
        //Adicionar a opção de clicar no text view para sair

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder{

        TextView emailMotorista;
        TextView tipoUsuarioMotorista;
        TextView sairDoAplicativo;
        ImageView iconeSuporte;

        TextView mensagemSuporte;


        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);
            emailMotorista = itemView.findViewById(R.id.emailMotoristaPerfil);
            tipoUsuarioMotorista = itemView.findViewById(R.id.idTextoTipoUsuario);
            sairDoAplicativo = itemView.findViewById(R.id.idOpcaoSairDoAppMotorista);
            //iconeSuporte = itemView.findViewById(R.id.idOpcaoSairDoAppMotorista);
            mensagemSuporte = itemView.findViewById(R.id.idTextoInformacaoSuporte);
        }
    }
}
