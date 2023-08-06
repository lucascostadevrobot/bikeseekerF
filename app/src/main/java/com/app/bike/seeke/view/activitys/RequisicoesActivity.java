package com.app.bike.seeke.view.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.bike.seeke.R;
import com.app.bike.seeke.adapter.RequisicoesAdapter;
import com.app.bike.seeke.domain.RequisicaoDomain;
import com.app.bike.seeke.domain.UsuarioDomain;
import com.app.bike.seeke.helper.RecyclerItemClickListener;
import com.app.bike.seeke.helper.UsuarioFirebase;
import com.app.bike.seeke.repository.ConfiguracaoFirebase;
import com.app.bike.seeke.view.activitys.ui.home.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RequisicoesActivity extends AppCompatActivity {
    private FirebaseAuth autenticacao;
    private DatabaseReference databaseReference;

    private RecyclerView recyclerViewRequisicoes;

    //Criando a lista para o RecyclerView
    private List<RequisicaoDomain> list = new ArrayList<>();

    //Criando um Adapter para Referenciar adapters ao recyclerview
    private RequisicoesAdapter requisicoesAdapter;

    private TextView textResultadoRequisicoes;

    private ProgressBar progressBarRequisicoes;

    private UsuarioDomain motorista;

    private LocationManager locationManager;

    private LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requisicoes);

        inicializarComponentesMotorista();

        //Metodo recuperando localização do usuario
        recuperarLocalizacaoUsuario();
    }

    //Realiza a consulta no metodo verificaStatus da requisicao e Starta o mesmo
    @Override
    protected void onStart() {
        super.onStart();
        verificaStatusRequisicao();
    }

    private void verificaStatusRequisicao() {
        UsuarioDomain usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        DatabaseReference firabaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference requisicoe = firabaseRef.child("requisicoes");

        Query
                requisicoesPesquisas = requisicoe.orderByChild("motorista/id")
                .equalTo(usuarioLogado.getId());

        requisicoesPesquisas.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    RequisicaoDomain requisicaoDomain = ds.getValue(RequisicaoDomain.class);
                    if (requisicaoDomain.getStatus().equals(RequisicaoDomain.STATUS_A_CAMINHO)
                            || requisicaoDomain.getStatus().equals(RequisicaoDomain.STATUS_VIAGEM)) {
                        abrirTelaCorrida(requisicaoDomain.getId(), motorista, true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void recuperarLocalizacaoUsuario() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //Recupera latitude e longitude
        /* Segunda vez que executamos o metodo de gerenciamento de permissões.
         * 1)Provedor da localização
         * 2)Tempo mínimo entre atualizações de localizações (milesegundos)
         * 3)Distancia mínima entre atualizações de localizações
         * 4)Location listener (para recebermos as atualizações)
         */
        locationListener = new LocationListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onLocationChanged(Location location) {
                /**
                 * 0- Recupera a latitude e longitue
                 * 1- Verifica se a variavel lat e log estão vazios
                 * 2- Se não estiver setamos a lat e log no motorista logado
                 * 3- Realiza o calculo para medir a distancia do motorista com o passageiro
                 * 4- Determina que no removeUpdates não queremos  a partir da aceitação receber atualizações do passageoiro
                 * 5- Atualiza e notifica as atualizações pora o motorista
                 * */
                String latitude = String.valueOf(location.getLatitude());
                String longitude = String.valueOf(location.getLongitude());

                if (!latitude.isEmpty() && !longitude.isEmpty()) {
                    motorista.setLatitude(latitude);
                    motorista.setLongitude(longitude);
                    locationManager.removeUpdates(locationListener);
                    requisicoesAdapter.notifyDataSetChanged();

                }


            }

            /* Segunda vez que executamos o metodo de gerenciamento de permissões.
             * 1)Provedor da localização
             * 2)Tempo mínimo entre atualizações de localizações (milesegundos)
             * 3)Distancia mínima entre atualizações de localizações
             * 4)Location listener (para recebermos as atualizações)
             */
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        //Solicitar atualizações de localização
        //Solicitar atualizações de localizaçã
        //minTimeMs: Determina de quanto em quanto tempo nosso locationListener vai atualizar a localização do motorista
        //mimDisntance: Determina da distancia atualizada
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RequisicoesActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(RequisicoesActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            ActivityCompat.requestPermissions(RequisicoesActivity.this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 1);
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0,
                    locationListener
            );


            return;
        }
    }


    //Metodo para criação do nosso menu global com a opção de Sair
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //Metodo para opções do nosso Menu Sair
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSair:
                autenticacao.signOut();
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void abrirTelaCorrida(String idRequisicao, UsuarioDomain motorista, boolean requisicaoAtiva) {
        Intent intent = new Intent(RequisicoesActivity.this, CorridaActivity.class);
        intent.putExtra("idRequisicao", idRequisicao);
        intent.putExtra("motorista", motorista);
        intent.putExtra("requisicaoAtiva", requisicaoAtiva);
        startActivity(intent);
    }

    public void inicializarComponentesMotorista() {

        //Inicializa componentes do xml da tela de requisicoes;
        recyclerViewRequisicoes = findViewById(R.id.recyclerRequisicoes);
        textResultadoRequisicoes = findViewById(R.id.carregando_texto_recycler_requisicoes);
        progressBarRequisicoes = findViewById(R.id.progressBarRecylerRequisicoes);

        //Inicializa componente autenticação firebase e Database
        motorista = UsuarioFirebase.getDadosUsuarioLogado();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();

        //Configurando o RecylerView
        requisicoesAdapter = new RequisicoesAdapter(list, getApplicationContext(), motorista);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewRequisicoes.setLayoutManager(layoutManager);
        recyclerViewRequisicoes.setHasFixedSize(true);
        recyclerViewRequisicoes.setAdapter(requisicoesAdapter);

        recyclerViewRequisicoes.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerViewRequisicoes,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            }

                            @Override
                            public void onItemClick(View view, int position) {
                                RequisicaoDomain requisicaoDomain = list.get(position);
                                abrirTelaCorrida(requisicaoDomain.getId(), motorista, false);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }
                        }
                )
        );


        //Metodo recupera requisicoes
        recuperarRequisicoes();
    }

    private void recuperarRequisicoes() {
        DatabaseReference requisicoesRecycler = databaseReference.child("requisicoes");

        //Ordena as requisicoes pelo OrderByChild do banco de dados apenas Requisicoes com Status AGUARDANDO

        Query requisicaoPesquisas = requisicoesRecycler.orderByChild("status")
                .equalTo(RequisicaoDomain.STATUS_AGUARDANDO);

        //Verifica se existe requisicoes e recuperar pelo EventListener
        requisicaoPesquisas.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.getChildrenCount() > 0) {

                    textResultadoRequisicoes.setVisibility(View.GONE);
                    progressBarRequisicoes.setVisibility(View.GONE);

                    //Mostrando o RecyclerView
                    recyclerViewRequisicoes.setVisibility(View.VISIBLE);
                } else {
                    textResultadoRequisicoes.setVisibility(View.VISIBLE);
                    progressBarRequisicoes.setVisibility(View.VISIBLE);

                    //Escondendo o RecyclerView
                    recyclerViewRequisicoes.setVisibility(View.GONE);
                }

                //Usando for para recuperar a listagem de requisicoes
                //Exibe um List no Recycler View
                list.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    RequisicaoDomain requisicaoDomain = ds.getValue(RequisicaoDomain.class);
                    list.add(requisicaoDomain);

                }

                requisicoesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}