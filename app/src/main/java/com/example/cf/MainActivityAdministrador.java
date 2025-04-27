package com.example.cf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.cf.FragmentosAdministrador.InicioAdmin;
import com.example.cf.FragmentosAdministrador.ListaAdmin;
import com.example.cf.FragmentosAdministrador.PerfilAdmin;
import com.example.cf.FragmentosAdministrador.RegistrarAdmin;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivityAdministrador extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_administrador);


        Toolbar toolbar = findViewById(R.id.toobarA);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout_A);


        NavigationView navigationView = findViewById(R.id.nav_viewA);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);


        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containerA,
                    new InicioAdmin()).commit();
            navigationView.setCheckedItem(R.id.Inicioadmin);
        }


    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.Inicioadmin){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containerA,
                    new InicioAdmin()).commit();

        }
        if(item.getItemId() == R.id.PerfilAdmin){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containerA,
                    new PerfilAdmin()).commit();

        }
        if(item.getItemId() == R.id.RegistrarAdmin){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containerA,
                    new RegistrarAdmin()).commit();

        }
        if(item.getItemId() == R.id.ListarAdmin){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containerA,
                    new ListaAdmin()).commit();

        }
        if(item.getItemId() == R.id.Salir){

           CerrarSesion();

        }
            drawerLayout.closeDrawer(GravityCompat.START);
        //return true;
        return super.onOptionsItemSelected(item);
    }

    private void ComprobandoInicioSesion(){
        if (user!= null){
            Toast.makeText(this, "Se ha iniciado sesión", Toast.LENGTH_SHORT).show();
        }else{
            //Si no se ha iniciado sesion es por que el usuario no es admin
            startActivity(new Intent(MainActivityAdministrador.this,MainActivity.class));
            finish();
        }
    }

    private void CerrarSesion(){
        firebaseAuth.signOut();
        startActivity(new Intent(MainActivityAdministrador.this,MainActivity.class));
        Toast.makeText(this, "Cerraste sesión exitosamente", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart(){
        ComprobandoInicioSesion();
        super.onStart();
    }


}