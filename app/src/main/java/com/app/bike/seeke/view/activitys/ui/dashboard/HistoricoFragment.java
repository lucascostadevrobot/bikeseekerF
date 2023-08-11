package com.app.bike.seeke.view.activitys.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.bike.seeke.R;
import com.app.bike.seeke.adapter.HistoricoAdapterMotorista;
import com.app.bike.seeke.databinding.FragmentHistoricoBinding;
import com.app.bike.seeke.databinding.FragmentNotificationsBinding;
import com.app.bike.seeke.domain.RequisicaoDomain;
import com.app.bike.seeke.repository.ConfiguracaoFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HistoricoFragment extends Fragment {
    private FragmentHistoricoBinding binding;
   // private FragmentHistoricoBinding fragmentHistoricoBinding;

    List<RequisicaoDomain> listaRequisicoes;
    RecyclerView recyclerView;
    HistoricoAdapterMotorista historicoAdapterMotorista;
    DatabaseReference databaseReference;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HistoricoViewModel historicoViewModel =
                new ViewModelProvider(this).get(HistoricoViewModel.class);

        binding = FragmentHistoricoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        recyclerView =  binding.idRecycleViewHistorico;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        listaRequisicoes =  new ArrayList<>();
        databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
        databaseReference.child("requisicoes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dn:snapshot.getChildren()){
                    RequisicaoDomain requisicaoDomain = dn.getValue(RequisicaoDomain.class);
                    listaRequisicoes.add(requisicaoDomain);

                }
                historicoAdapterMotorista = new HistoricoAdapterMotorista(listaRequisicoes);
                recyclerView.setAdapter(historicoAdapterMotorista);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


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