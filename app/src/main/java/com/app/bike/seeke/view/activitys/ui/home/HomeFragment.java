package com.app.bike.seeke.view.activitys.ui.home;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.viewmodel.CreationExtras;

import com.app.bike.seeke.R;
import com.app.bike.seeke.databinding.FragmentHomeBinding;
import com.app.bike.seeke.domain.RequisicaoDomain;
import com.app.bike.seeke.domain.UsuarioDomain;
import com.app.bike.seeke.helper.UsuarioFirebase;
import com.app.bike.seeke.repository.ConfiguracaoFirebase;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


public class HomeFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    private FragmentHomeBinding binding;
    private GoogleMap mMap;
    SupportMapFragment supportMapFragment;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private LatLng localMotorista;
    private LatLng localPassageiro;
    private UsuarioDomain motorista;
    private UsuarioDomain passageiro;

    private String idRequisicao;

    private RequisicaoDomain requisicaoDomain;

    private DatabaseReference databaseReference;

    private Button botaoAceitarCorridaStatus;

    private Marker marcadorMotorista;
    private Marker marcadorPassageiro;

    private FirebaseAuth autenticacao;
    private String satusRequisicao;
    private boolean requisicaoAtiva;
    private FloatingActionButton floatingActionButton;


    //Construtor vazio
    public HomeFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        botaoAceitarCorridaStatus = (Button) view.findViewById(R.id.botao_aceitarCorrida);
        botaoAceitarCorridaStatus.setOnClickListener(this);
        //botaoAceitarCorridaStatus = requireActivity().findViewById(R.id.botao_aceitarCorrida);




        //Verifica se o MapaSuporteManager esta vazio
        if (supportMapFragment == null) {
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            supportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, supportMapFragment).commit();


                /*MapsInitializer.initialize(getContext(), MapsInitializer.Renderer.LATEST, new OnMapsSdkInitializedCallback() {
                    @Override
                    public void onMapsSdkInitialized(@NonNull MapsInitializer.Renderer renderer) {
                        Log.e("TAG", "onMapsSdkInitialized:  ");
                    }
                });*/

            //Instanciando Objetos para Back Botton Fragment
            Toolbar toolbar = view.findViewById(R.id.idIniciarRotaMotorista);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                toolbar.setNavigationIcon(R.drawable.baseline_keyboard_backspace_24);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().onBackPressed();
                    }
                });
            }
        }
        //Recuperando os dados dos usuários passageiros na lista de requisicoes
        //Esse if é necessário para alterarmos e trabalharmos com alteração do status da requisicao
        if (requireActivity().getIntent().getExtras().containsKey("idRequisicao")
                && requireActivity().getIntent().getExtras().containsKey("motorista")) {
            Bundle extras = requireActivity().getIntent().getExtras();
            motorista = (UsuarioDomain) extras.getSerializable("motorista");
            if (motorista != null && motorista.getLatitude() != null && motorista.getLongitude() != null) {

                localMotorista = new LatLng( //ALTEREI AQUI POIS O POSITION DO MARCADOR MOTORISTA ESTA DANDO ERRO LATLONG NAO PODE SER NULO
                        Double.parseDouble(motorista.getLatitude()),
                        Double.parseDouble(motorista.getLongitude())
                );
                idRequisicao = extras.getString("idRequisicao");
                requisicaoAtiva = extras.getBoolean("requisicaoAtiva");
                verificaStatusRequisicaoUi();
            }
        }

        supportMapFragment.getMapAsync(this);
        return view;
    }

    //Metodo Principal do Mapa
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Chamando o arquivo json com a estilização do mapa através desse metodo
        //Através dessa funcionalidade conseguimos chamar um mapa estilizado no arquivo JSON
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.

            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            requireActivity().getApplicationContext(), R.raw.style_json));
            recuperarLocalizacaoUsuario();

            if (!success) {
                Log.e(TAG, "Estilo de Mapa não carregado.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find stylwe. Error: ", e);

        }
    }


    private void recuperarLocalizacaoUsuario() {
        locationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
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
                localMotorista = new LatLng(latitude, longitude);
                //mMap.clear();
                /*
                mMap.getUiSettings().setZoomControlsEnabled(true);
                 Essa função  não será utilizada, ButtonNavigation está sobrepondo
                 */
                //Atualizar Geofire
                UsuarioFirebase.atualizarDadosLocalizacao(latitude, longitude);
                alteraInterfaceSatusRequisicao(satusRequisicao);


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
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 1);
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0,
                    locationListener
            );
        }
    }


    /**
     * Esse metodo é responsável por sempre verificar os status da corrida na tela UI motorista
     * Assim é possível que a UI seja atualizada de acordo com as distancias percorridas
     */
    private void verificaStatusRequisicaoUi() {
        //Inicializa componente autenticação firebase e Database
        databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference requisicoesDataBaseRef = databaseReference.child("requisicoes")
                .child(idRequisicao);
        requisicoesDataBaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requisicaoDomain = snapshot.getValue(RequisicaoDomain.class);
                if (requisicaoDomain != null) { //Alteração aqui erro ao voltar tela de requisicao
                    passageiro = requisicaoDomain.getPassageiro();
                    localPassageiro = new LatLng(
                            Double.parseDouble(passageiro.getLatitude()),
                            Double.parseDouble(passageiro.getLongitude())

                    );
                    satusRequisicao = requisicaoDomain.getStatus();
                    alteraInterfaceSatusRequisicao(satusRequisicao);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void alteraInterfaceSatusRequisicao(String status){
        switch (status) {
            case RequisicaoDomain.STATUS_AGUARDANDO:
                requisicaoAguardando();
                break;

            case RequisicaoDomain.STATUS_A_CAMINHO:
                requisicaoACaminho();
                break;
        }
    }

    //Vai alterar o botão aceitar corrida
    private void requisicaoAguardando() {
        if (mMap != null) {
            botaoAceitarCorridaStatus.setText("Aceitar corrida");
            //Exibe o marcador do motorista
            adicionarMarcadorMotorista(localMotorista, motorista.getNome());
            mMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(localMotorista, 28)
            );
        }
    }

    private void requisicaoACaminho() {
        if (mMap != null) {
            botaoAceitarCorridaStatus.setText("A caminho do passageiro");


            //Exibe o marcador do motorista
            adicionarMarcadorMotorista(localMotorista, motorista.getNome());
            //Exibe o marcador do passageiro
            adicionarMarcadorPassageiro(localPassageiro, passageiro.getNome());
            //Centraliza os marcados do passageiro e motorista
            centralizaDoisMarcadores(marcadorMotorista, marcadorPassageiro);

            //Inicia o monitoramento do motorista / passageiro
            iniciarMonitoramentoCorrida(passageiro, motorista);


        }
    }

    private void iniciarMonitoramentoCorrida(UsuarioDomain p, UsuarioDomain m){
        //Incializando geofire
        //Define no local_usuario
        DatabaseReference localUsuario = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("local_usuario");
        GeoFire geoFire = new GeoFire(localUsuario);
        //Adiciona circulo no passageiro
        Circle circulo = mMap.addCircle(
                new CircleOptions()
                        .center(localPassageiro)
                        .radius(50) //em metros
                        .fillColor(Color.argb(72,0,40, 100))
                        .strokeColor(Color.argb(5, 61,139, 0))
        );

        GeoQuery geoQuery = geoFire.queryAtLocation(
                new GeoLocation(localPassageiro.latitude, localPassageiro.longitude),
                0.08 //em km (0.05 80 metros, 0.8 equivale a 800 metros)
        );
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //Verificamos se o motorista entra dentro da area de 80 metros,para isso usamos o metodo onKeyEntered
                //Para isso precisamos verificar se o Id do motorista está dentro da area. Sabemos que do Motorista já esta
                if (key.equals(motorista.getId())){
                    requisicaoDomain.setStatus(RequisicaoDomain.STATUS_VIAGEM);
                    requisicaoDomain.atualizaStatusRequisicao();

                    geoQuery.removeAllListeners();
                    circulo.remove();

                }

            }
            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });


    }

    /**
     * Bounds: Utiliza conceito de definição de limites
     */
    private void centralizaDoisMarcadores(Marker marcador1, Marker marcador2) {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(marcador1.getPosition());
        builder.include(marcador2.getPosition());

        LatLngBounds bounds = builder.build();

        int largura = getResources().getDisplayMetrics().widthPixels;
        int altura = getResources().getDisplayMetrics().heightPixels;
        int espacoInterno = (int) (largura * 0.20);

        mMap.moveCamera(
                CameraUpdateFactory.newLatLngBounds(bounds, largura, altura, espacoInterno)
        );

    }

    private void adicionarMarcadorMotorista(LatLng localizacao, String titulo) {
        if (marcadorMotorista != null)
            marcadorMotorista.remove();
        marcadorMotorista = mMap.addMarker(
                new MarkerOptions()
                        .position(localizacao)
                        .title(titulo)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.motorbike))
        );
    }

    private void adicionarMarcadorPassageiro(LatLng localizacao, String titulo) {
        if (marcadorPassageiro != null)
            marcadorPassageiro.remove();
        marcadorPassageiro = mMap.addMarker(
                new MarkerOptions()
                        .position(localizacao)
                        .title(titulo)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.usuario))
        );

    }

    //Metodo responsável quando o Motorista clicar no Botão de aceitar corrida
    public void aceitarCorrida(View view) {
        requisicaoDomain = new RequisicaoDomain();
        requisicaoDomain.setId(idRequisicao);
        requisicaoDomain.setMotorista(motorista);
        requisicaoDomain.setStatus(RequisicaoDomain.STATUS_A_CAMINHO);

        requisicaoDomain.atualizarRequisicao();

    }

    @Override
    public void onClick(View view) {
        aceitarCorrida(view);
    }

    //Metodos Ciclo de vida do Framgmento
    @Override
    public void onStart() {
        super.onStart();
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


    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
    }

}