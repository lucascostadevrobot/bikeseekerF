package com.app.bike.seeke.view.activitys.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.bike.seeke.R;
import com.app.bike.seeke.adapter.HistoricoAdapterMotorista;
import com.app.bike.seeke.adapter.PerfilMotoristaAdapter;
import com.app.bike.seeke.databinding.FragmentNotificationsBinding;
import com.app.bike.seeke.domain.RequisicaoDomain;
import com.app.bike.seeke.domain.UsuarioDomain;
import com.app.bike.seeke.repository.ConfiguracaoFirebase;
import com.app.bike.seeke.view.activitys.RequisicoesActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class PerfilFragment extends Fragment {


    UsuarioDomain motorista;

    RequisicaoDomain requisicaoDomain;
    RecyclerView recyclerView;
    PerfilMotoristaAdapter perfilAdapterMotorista;
    DatabaseReference databaseReference;


    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.idRecycleViewPerfilMotorista;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        motorista = new UsuarioDomain();
        requisicaoDomain = new RequisicaoDomain();
        perfilAdapterMotorista = new PerfilMotoristaAdapter(requisicaoDomain);
        recyclerView.setAdapter(perfilAdapterMotorista);

        /*recyclerView = binding.idRecycleViewPerfilMotorista;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listaRequisicoes = new RequisicaoDomain();
        databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
        databaseReference.child("requisicoes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dn : snapshot.getChildren()) {
                    RequisicaoDomain requisicaoDomain = dn.getValue(RequisicaoDomain.class);
                    listaRequisicoes.add(requisicaoDomain);
                    requisicaoDomain.atualizarRequisicao();

                }
                perfilAdapterMotorista = new PerfilMotoristaAdapter(listaRequisicoes);
                recyclerView.setAdapter(perfilAdapterMotorista);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}