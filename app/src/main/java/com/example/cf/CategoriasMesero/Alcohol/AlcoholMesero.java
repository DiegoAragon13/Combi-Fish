package com.example.cf.CategoriasMesero.Alcohol;

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

import com.example.cf.CategoriasAdmin.AlcoholA.Alcohol;
import com.example.cf.CategoriasAdmin.AlcoholA.ViewHolderAlcohol;
import com.example.cf.CategoriasMesero.Ticket;
import com.example.cf.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AlcoholMesero extends AppCompatActivity {

    RecyclerView recyclerViewAlcoholMes;
    FirebaseDatabase mfirebaseDatabase;
    DatabaseReference mRef;
    FirebaseRecyclerAdapter<Alcohol, ViewHolderAlcohol> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Alcohol> options;
    SharedPreferences sharedPreferences;
    Dialog dialog;
    Spinner mesaSpinner;
    String numeroDeMesaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alcohol_mesero);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Alcohol");
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recyclerViewAlcoholMes = findViewById(R.id.recyclerViewAlcoholMes);
        recyclerViewAlcoholMes.setHasFixedSize(true);
        mesaSpinner = findViewById(R.id.mesaSpinner);

        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mfirebaseDatabase.getReference("BEBIDA_ALCOHOL/");

        dialog = new Dialog(AlcoholMesero.this);

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

        ListarImagenesAlcohol();

    }

    private void ListarImagenesAlcohol() {
        options = new FirebaseRecyclerOptions.Builder<Alcohol>().setQuery(mRef, Alcohol.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Alcohol, ViewHolderAlcohol>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderAlcohol viewHolderAlcohol, int i, @NonNull Alcohol alcohol) {
                viewHolderAlcohol.SeteoAlcohol(
                        getApplicationContext(),
                        alcohol.getNombre(),
                        alcohol.getPrecio(),
                        alcohol.getImagen()
                );
            }

            @NonNull
            @Override
            public ViewHolderAlcohol onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alcohol, parent, false);
                ViewHolderAlcohol viewHolderAlcohol = new ViewHolderAlcohol(itemView);

                viewHolderAlcohol.setOnClickListener(new ViewHolderAlcohol.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (numeroDeMesaSeleccionada != null) {
                            Alcohol alcoholSeleccionado = getItem(position);
                            if (alcoholSeleccionado != null) {
                                DatabaseReference mesaSeleccionadaRef = FirebaseDatabase.getInstance().getReference()
                                        .child("mesas").child(numeroDeMesaSeleccionada).child("platillos");
                                mesaSeleccionadaRef.push().setValue(alcoholSeleccionado);
                                Toast.makeText(AlcoholMesero.this, "Bebida agregada a la mesa " + numeroDeMesaSeleccionada, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AlcoholMesero.this, "Por favor seleccione una mesa", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void OnItemLongClick(View view, int position) {
                        // Manejar el evento de clic largo si es necesario
                    }
                });
                return viewHolderAlcohol;
            }
        };

        sharedPreferences = AlcoholMesero.this.getSharedPreferences("COCINA", MODE_PRIVATE);
        String ordenar_en = sharedPreferences.getString("Ordenar", "Dos");

        if (ordenar_en.equals("Dos")) {
            recyclerViewAlcoholMes.setLayoutManager(new GridLayoutManager(AlcoholMesero.this, 2));
        } else if (ordenar_en.equals("Tres")) {
            recyclerViewAlcoholMes.setLayoutManager(new GridLayoutManager(AlcoholMesero.this, 3));
        }

        firebaseRecyclerAdapter.startListening();
        recyclerViewAlcoholMes.setAdapter(firebaseRecyclerAdapter);
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
            startActivity(new Intent(AlcoholMesero.this, Ticket.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}