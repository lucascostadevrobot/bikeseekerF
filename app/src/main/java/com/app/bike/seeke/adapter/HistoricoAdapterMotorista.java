package com.app.bike.seeke.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.bike.seeke.R;
import com.app.bike.seeke.domain.RequisicaoDomain;

import java.util.List;

public class HistoricoAdapterMotorista extends RecyclerView.Adapter {
    List<RequisicaoDomain> listHistoricoMotociclista;


    //Define um construtor recebendo a Lista de Historico do Motociclista
    public HistoricoAdapterMotorista(List<RequisicaoDomain> listHistoricoMotociclista) {
        this.listHistoricoMotociclista = listHistoricoMotociclista;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_historico_motociclista_personalizado, parent, false);
        ViewHolderClass vhClass = new ViewHolderClass(view);

        return vhClass;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ViewHolderClass viewHolderClass = (ViewHolderClass) holder;
        RequisicaoDomain requisicaoDomain = listHistoricoMotociclista.get(position);
        viewHolderClass.textNomeMoto.setText((CharSequence) requisicaoDomain.getMotorista());
        viewHolderClass.textCidade.setText(requisicaoDomain.getDestinoDomain().toString());
        //Falta passar a data na requisicao viewHolderClass.textData.setText(requisicaoDomain.);
    }

    @Override
    public int getItemCount() {
        return listHistoricoMotociclista.size();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder{
        TextView textNomeMoto;
        TextView textCidade;
        TextView textData;


        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);
            textNomeMoto = itemView.findViewById(R.id.texto_listrequisicoes_nome_passageiro);
            textCidade = itemView.findViewById(R.id.texto_localizacao_corrida);
            textData = itemView.findViewById(R.id.texto_data_corrida);
        }
    }
}
