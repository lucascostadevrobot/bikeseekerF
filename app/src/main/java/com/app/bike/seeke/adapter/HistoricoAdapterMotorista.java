package com.app.bike.seeke.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.bike.seeke.R;
import com.app.bike.seeke.domain.DestinoDomain;
import com.app.bike.seeke.domain.RequisicaoDomain;
import com.app.bike.seeke.domain.UsuarioDomain;

import org.w3c.dom.Text;

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
        UsuarioDomain passageiro = requisicaoDomain.getPassageiro();
        DestinoDomain destinoDomain = requisicaoDomain.getDestinoDomain();



        viewHolderClass.textNomeMoto.setText(passageiro.getNome());
        viewHolderClass.textCidade.setText(destinoDomain.getCidade());
        viewHolderClass.txtStatusDaCorrida.setText(requisicaoDomain.getStatus());
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
        TextView txtStatusDaCorrida;


        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);
            textNomeMoto = itemView.findViewById(R.id.texto_listrequisicoes_nome_passageiro);
            textCidade = itemView.findViewById(R.id.texto_localizacao_corrida);
            textData = itemView.findViewById(R.id.texto_data_corrida);
            txtStatusDaCorrida = itemView.findViewById(R.id.texto_stutus_corrida);
        }
    }

    /*public void filtroCardHistoricoMotorista() {


        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(requireContext());
        alertBuilder.setTitle("Escolha o que fazer");
        alertBuilder.setMessage("Essa requisição do histórico ainda não foi aceita. O seu passageiro está te esperando. Deseja aceitar?");
        alertBuilder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                aceitarCorrida();

            }
        });

        alertBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Ação a ser executada quando o botão "Cancelar" for pressionado
                // Geralmente, não é necessário fazer nada aqui, já que é apenas um cancelamento
            }
        });
        alertBuilder.show();
    }

    //Metodo responsável quando o Motorista clicar no Botão de aceitar corrida
    public void aceitarCorrida() {
        requisicaoDomain = new RequisicaoDomain();
        requisicaoDomain.setId(idRequisicao);
        requisicaoDomain.setMotorista(motorista);
        requisicaoDomain.setStatus(RequisicaoDomain.STATUS_A_CAMINHO);

        requisicaoDomain.atualizarRequisicao();


    }*/
}
