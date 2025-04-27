package com.example.cf.CategoriasMesero.SinAlcohol;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cf.CategoriasAdmin.SinAlcocholA.SinAlcohol;
import com.example.cf.CategoriasAdmin.SinAlcocholA.ViewHolderSinAlcohol;
import com.example.cf.CategoriasMesero.Ticket;
import com.example.cf.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SinAlcoholMesero extends AppCompatActivity {

    RecyclerView recyclerViewSinAlcoholMes;
    FirebaseDatabase mfirebaseDatabase;
    DatabaseReference mRef;
    FirebaseRecyclerAdapter<SinAlcohol, ViewHolderSinAlcohol> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<SinAlcohol> options;
    SharedPreferences sharedPreferences;
    Dialog dialog;
    Spinner mesaSpinner;
    String numeroDeMesaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sin_alcohol_mesero);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Bebidas Sin Alcohol ");
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recyclerViewSinAlcoholMes = findViewById(R.id.recyclerViewSinAlcoholMes);
        recyclerViewSinAlcoholMes.setHasFixedSize(true);
        mesaSpinner = findViewById(R.id.mesaSpinner);

        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mfirebaseDatabase.getReference("BEBIDA_SIN_ALCOHOL/");

        dialog = new Dialog(SinAlcoholMesero.this);

        // Configurar Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.mesas_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mesaSpinner.setAdapter(adapter);
        mesaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                numeroDeMesaSeleccionada = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                numeroDeMesaSeleccionada = null;
            }
        });

        ListarBebidasSinAlcohol();
    }

    private void ListarBebidasSinAlcohol() {
        options = new FirebaseRecyclerOptions.Builder<SinAlcohol>().setQuery(mRef, SinAlcohol.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<SinAlcohol, ViewHolderSinAlcohol>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderSinAlcohol viewHolderSinAlcohol, int i, @NonNull SinAlcohol sinAlcohol) {
                viewHolderSinAlcohol.SeteoSinAlcohol(
                        getApplicationContext(),
                        sinAlcohol.getNombre(),
                        sinAlcohol.getPrecio(),
                        sinAlcohol.getImagen()
                );
            }

            @NonNull
            @Override
            public ViewHolderSinAlcohol onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sinalcohol, parent, false);
                ViewHolderSinAlcohol viewHolderSinAlcohol = new ViewHolderSinAlcohol(itemView);

                viewHolderSinAlcohol.setOnClickListener(new ViewHolderSinAlcohol.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (numeroDeMesaSeleccionada != null) {
                            SinAlcohol bebidaSeleccionada = getItem(position);
                            if (bebidaSeleccionada != null) {
                                DatabaseReference mesaSeleccionadaRef = FirebaseDatabase.getInstance().getReference()
                                        .child("mesas").child(numeroDeMesaSeleccionada).child("platillos");
                                mesaSeleccionadaRef.push().setValue(bebidaSeleccionada);
                                Toast.makeText(SinAlcoholMesero.this, "Bebida agregada a la mesa " + numeroDeMesaSeleccionada, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SinAlcoholMesero.this, "Por favor seleccione una mesa", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void OnItemLongClick(View view, int position) {
                        // Manejar el evento de clic largo si es necesario
                    }
                });
                return viewHolderSinAlcohol;
            }
        };

        sharedPreferences = SinAlcoholMesero.this.getSharedPreferences("SIN_ALCOHOL", MODE_PRIVATE);
        String ordenar_en = sharedPreferences.getString("Ordenar", "Dos");

        if (ordenar_en.equals("Dos")) {
            recyclerViewSinAlcoholMes.setLayoutManager(new GridLayoutManager(SinAlcoholMesero.this, 2));
        } else if (ordenar_en.equals("Tres")) {
            recyclerViewSinAlcoholMes.setLayoutManager(new GridLayoutManager(SinAlcoholMesero.this, 3));
        }

        firebaseRecyclerAdapter.startListening();
        recyclerViewSinAlcoholMes.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.startListening();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_mesero, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.cuenta) {
            startActivity(new Intent(SinAlcoholMesero.this, Ticket.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
