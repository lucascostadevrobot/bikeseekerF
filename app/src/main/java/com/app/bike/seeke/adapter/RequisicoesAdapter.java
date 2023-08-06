package com.app.bike.seeke.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.bike.seeke.R;
import com.app.bike.seeke.domain.RequisicaoDomain;
import com.app.bike.seeke.domain.UsuarioDomain;
import com.app.bike.seeke.helper.UsuarioFirebase;

import java.util.List;

public class RequisicoesAdapter extends RecyclerView.Adapter<RequisicoesAdapter.ViewHolderRequisicoes> {

    private List<RequisicaoDomain> requisicaoDomains;
    private Context context;
    private UsuarioDomain motorista;

    //Inicializando o construtor
    public RequisicoesAdapter(List<RequisicaoDomain> requisicaoDomains, Context context, UsuarioDomain motorista) {
        this.requisicaoDomains = requisicaoDomains;
        this.context = context;
        this.motorista = motorista;
    }

    @NonNull
    @Override
    public ViewHolderRequisicoes onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Instanciando o Recycler e passando o Layout porsonalizado
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_adapter_personalizado, parent, false);
        return new ViewHolderRequisicoes(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderRequisicoes holder, int position) {

        RequisicaoDomain requisicaoDomain = requisicaoDomains.get(position);
        UsuarioDomain passageiro = requisicaoDomain.getPassageiro();

        holder.nome.setText(passageiro.getNome());
        holder.distancia.setText("1 km- aproximadamente");
    }

    @Override
    public int getItemCount() {
        return requisicaoDomains.size();
    }

    //Passando objetos para o estilizar o RecyclerView e exibir na lista
    public  class ViewHolderRequisicoes extends RecyclerView.ViewHolder{
        TextView nome, distancia, foto, iconPartida, iconChegada;

        public ViewHolderRequisicoes(View itemView){
            super(itemView);

            nome = itemView.findViewById(R.id.texto_listrequisicoes_nome_passageiro);
            distancia = itemView.findViewById(R.id.texto_listrequisicoes_distancia);






        }
    }
}
