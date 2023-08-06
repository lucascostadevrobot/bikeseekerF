package com.app.bike.seeke.view.activitys;

import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.app.bike.seeke.R;
import com.app.bike.seeke.databinding.ActivityCorridaBinding;
import com.app.bike.seeke.databinding.FragmentHomeBinding;
import com.app.bike.seeke.domain.RequisicaoDomain;
import com.app.bike.seeke.domain.UsuarioDomain;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


public class CorridaActivity extends AppCompatActivity {

    private ActivityCorridaBinding binding;
    private GoogleMap mMap;
    SupportMapFragment supportMapFragment;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private LatLng localMotorista;

    private UsuarioDomain motorista;

    private  String idRequisicao;

    private RequisicaoDomain requisicaoDomain;

    private DatabaseReference databaseReference;

    private Button botaoAceitarCorridaStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCorridaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //Metodos para navegação no menu BottomNavigation
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_corrida);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }
}