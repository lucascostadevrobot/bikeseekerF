package com.app.bike.seeke.view.activitys;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.app.bike.seeke.databinding.ActivityPassageiroBinding;
import com.app.bike.seeke.domain.DestinoDomain;
import com.app.bike.seeke.domain.RequisicaoDomain;
import com.app.bike.seeke.domain.UsuarioDomain;
import com.app.bike.seeke.helper.UsuarioFirebase;
import com.app.bike.seeke.repository.ConfiguracaoFirebase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AlertDialogLayout;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;


import com.app.bike.seeke.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


public class PassageiroActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private ActivityPassageiroBinding binding;
    private FirebaseAuth autenticacao;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private EditText editMeuDestino;
    private ImageButton cancelaButton;
    private Button confirmaButton;
    private TextView atributoenderecoCidade;
    private LatLng localPassageiro;

    private LinearLayout linearLayoutDestino;
    private Button botaoChamarMotoBoyBikeSeeker;

    private  boolean motoboychamado = false;

    private DatabaseReference databaseReference;

    private RequisicaoDomain requisicaoDomainPassageiro;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inicializarComponentes(); //chamando metodo inicializar de componentes

        verificaStatusRequisicao();



    }

    /**
     * Metodo para verificar o status da requisicao e Relacionamento com a Recuperação da Requisicao ATUAL
     * 1- Aguardando
     * 2- Aceito
     * 3- Cancelado
     * */
    private void verificaStatusRequisicao(){
        UsuarioDomain usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        DatabaseReference requisicoes = databaseReference.child("requisicoes");
        Query requisicaoPesquisa = requisicoes.orderByChild("passageiro/id")
                .equalTo( usuarioLogado.getId());

        requisicaoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<RequisicaoDomain> lista = new ArrayList<>();
                /**
                 * 1- Entra no for comparando o Datasnapsho indo até o nó filho
                 * 2- cria uma lista de RequisicaoDomain adicionando a requiscao na lista
                 * 3- o For percorre a lista de requisicoes ate encontrar a requisicao mais atual
                 * */
                for (DataSnapshot ds: snapshot.getChildren()){
                    lista.add(ds.getValue(RequisicaoDomain.class));
                }

                Collections.reverse(lista);
                if(lista != null && lista.size()>0) {
                    requisicaoDomainPassageiro = lista.get(0);
                    switch (requisicaoDomainPassageiro.getStatus()) {
                        case RequisicaoDomain.STATUS_AGUARDANDO:
                            linearLayoutDestino.setVisibility(View.GONE);
                            botaoChamarMotoBoyBikeSeeker.setText("Cancelar Motoboy");
                            motoboychamado = true;
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //Metodo principal do nosso mapa
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        recuperarLocalizacaoUsuario();
        //Chamando o arquivo json com a estilização do mapa através desse metodo
        //Através dessa funcionalidade conseguimos chamar um mapa estilizado no arquivo JSON
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.

            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find stylwe. Error: ", e);

        }
    }
    //Metodo criado para validar se os campos digitar no search serão preenchidos todos
   /* public void validarEnderecoCompleto(View view){
        DestinoDomain destinoDomain = new DestinoDomain();
        if(destinoDomain.getCidade().isEmpty() || destinoDomain.getBairro().isEmpty()
                || destinoDomain.getRua().isEmpty() || destinoDomain.getNumero().isEmpty()){
            Toast.makeText(PassageiroActivity.this, "É necessário informar: Cidade, Bairro, Rua e Número!", Toast.LENGTH_SHORT).show();
        }
    }*/


    //Metodo que recebe a requisição do botão chamarBikeSeeker para chamar um motoboy
    @SuppressLint("SetTextI18n")
    public void chamarBikeSeeker(View view) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PassageiroActivity.this);
        String enderecoDestino = editMeuDestino.getText().toString();
        Address addressDestino = recuperarEnderecoDestino(enderecoDestino);

        if (!motoboychamado) {
            if (addressDestino != null) {
                // Crie a instância do DestinoDomain dentro do escopo do AlertDialog
                final DestinoDomain destinoDomain = new DestinoDomain();
                destinoDomain.setCidade(addressDestino.getLocality());
                destinoDomain.setCep(addressDestino.getPostalCode());
                destinoDomain.setBairro(addressDestino.getSubLocality());
                destinoDomain.setRua(addressDestino.getThoroughfare());
                destinoDomain.setNumero(addressDestino.getFeatureName());
                destinoDomain.setLatitude(String.valueOf(addressDestino.getLatitude()));
                destinoDomain.setLongitude(String.valueOf(addressDestino.getLongitude()));

                // Resto do seu código para o AlertDialog
                View alertDialogoPersonalizado = LayoutInflater.from(PassageiroActivity.this).inflate(R.layout.dialog_personalizado, null);
                atributoenderecoCidade = alertDialogoPersonalizado.findViewById(R.id.idCidade);
                cancelaButton = alertDialogoPersonalizado.findViewById(R.id.chamarUberId_cancela);
                confirmaButton = alertDialogoPersonalizado.findViewById(R.id.botaoConfirmaCorrida);

                // Atualize o texto no AlertDialog com o novo endereço
                atributoenderecoCidade.setText("Cidade: " + destinoDomain.getCidade());

                alertDialog.setView(alertDialogoPersonalizado);


                final AlertDialog dialogo = alertDialog.create();
                findViewById(R.id.botao_chamarmotoboy).setOnClickListener(v -> {
                    dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialogo.show();
                    cancelaButton.setOnClickListener(v12 -> {
                        // Limpar os dados da classe destinoDomain
                        destinoDomain.setCidade(null);
                        destinoDomain.setCep(null);
                        destinoDomain.setBairro(null);
                        destinoDomain.setRua(null);
                        destinoDomain.setNumero(null);
                        destinoDomain.setLatitude(null);
                        destinoDomain.setLongitude(null);

                        dialogo.dismiss();
                    });

                    confirmaButton.setOnClickListener(v1 -> {
                        salvarRequisicao(destinoDomain);
                        motoboychamado = true;
                        Toast.makeText(PassageiroActivity.this, "Obrigado por solicitar", Toast.LENGTH_SHORT).show();


                        dialogo.dismiss();
                    });
                });
            }
        } else {
            motoboychamado = false;
        }
    }
    //Fim do metodo chamar bikeSeeker


    //Metodo para salvar a requisicao quando o usuario clicar no botao confirmar do AlertDialog
    private void salvarRequisicao(DestinoDomain destinoDomain) {

        RequisicaoDomain requisicaoDomain = new RequisicaoDomain();
        requisicaoDomain.setDestinoDomain(destinoDomain);
        //Para configurar o passageiro precisamos recuperar o Usuario Logado
        UsuarioDomain usuarioPassageiroLogado = UsuarioFirebase.getDadosUsuarioLogado();
        usuarioPassageiroLogado.setLatitude(String.valueOf(localPassageiro.latitude));
        usuarioPassageiroLogado.setLongitude(String.valueOf(localPassageiro.longitude));
        requisicaoDomain.setPassageiro(usuarioPassageiroLogado);
        requisicaoDomain.setStatus(RequisicaoDomain.STATUS_AGUARDANDO);
        requisicaoDomain.salvar();

        linearLayoutDestino.setVisibility(View.GONE);
        botaoChamarMotoBoyBikeSeeker.setText("Cancelar motoboy");
    }

    /// Método para recuperar o Endereço de Destino para onde o usuário quer ir
    public Address recuperarEnderecoDestino(String endereco) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {

            List<Address> listaDeEnderecos = geocoder.getFromLocationName(endereco, 1); // Alterado para 1 para recuperar apenas um endereço
            if (listaDeEnderecos != null && listaDeEnderecos.size() > 0) {
                return listaDeEnderecos.get(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Metodo para recuperarmos a localização atual do usuario
    @SuppressLint("MissingPermission")
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
            @Override
            public void onLocationChanged(Location location) {

                //Recupera latitude e longitude
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                localPassageiro = new LatLng(latitude, longitude);

                //Atualizar Geofire
                UsuarioFirebase.atualizarDadosLocalizacao(latitude, longitude);


                mMap.clear();
                mMap.addMarker(
                        new MarkerOptions()
                                .position(localPassageiro)
                                .title("Meu Local")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.usuario))
                );
                mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(localPassageiro, 28)
                );
                mMap.getUiSettings().setZoomControlsEnabled(true);

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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PassageiroActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(PassageiroActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            ActivityCompat.requestPermissions(PassageiroActivity.this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 1);
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    10,
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

    //Meotod que inicializa todos os componentes da nossa Acivitiy Passegeiro
    //Sem  a ncessidade de inicializar todos os componenentes dentro de cada metodo.
    private void inicializarComponentes() {
        binding = ActivityPassageiroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Iniciar uma viagem");
        setSupportActionBar(binding.toolbar);

        //inicialização dos componentes
        editMeuDestino = findViewById(R.id.editTextMeuDestino);

        linearLayoutDestino = findViewById(R.id.linearLayoutDestino);
        botaoChamarMotoBoyBikeSeeker = findViewById(R.id.botao_chamarmotoboy);

        //Inicializa componente autenticação firebase e Database
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();


        //Trecho do código que inicializa o nosso Mapa.
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
}