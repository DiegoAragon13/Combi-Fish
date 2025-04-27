package com.example.cf.CategoriasMesero.Cocina; //

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.example.cf.CategoriasAdmin.CevichesA.Ceviche;
import com.example.cf.CategoriasAdmin.CevichesA.ViewHolderCeviche;
import com.example.cf.CategoriasAdmin.CocinaA.Cocina;
import com.example.cf.CategoriasAdmin.CocinaA.ViewHolderCocina;
import com.example.cf.CategoriasMesero.Ceviche.CevicheMesero;
import com.example.cf.CategoriasMesero.Ticket;
import com.example.cf.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CocinaMesero extends AppCompatActivity {
    RecyclerView recyclerViewCocinaMes;
    FirebaseDatabase mfirebaseDatabase;
    DatabaseReference mRef;
    FirebaseRecyclerAdapter<Cocina, ViewHolderCocina> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Cocina> options;
    SharedPreferences sharedPreferences;
    Dialog dialog;
    Spinner mesaSpinner;
    String numeroDeMesaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cocina_mesero);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Cocina & Combie");
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recyclerViewCocinaMes = findViewById(R.id.recyclerViewCocinaMes);
        recyclerViewCocinaMes.setHasFixedSize(true);
        mesaSpinner = findViewById(R.id.mesaSpinner);

        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mfirebaseDatabase.getReference("PLATILLOS_COCINA/");

        dialog = new Dialog(CocinaMesero.this);

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

        ListarImagenesCocina();
    }

    private void ListarImagenesCocina() {
        options = new FirebaseRecyclerOptions.Builder<Cocina>().setQuery(mRef, Cocina.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Cocina, ViewHolderCocina>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderCocina viewHolderCocina, int i, @NonNull Cocina cocina) {
                viewHolderCocina.SeteoCocina(
                        getApplicationContext(),
                        cocina.getNombre(),
                        cocina.getPrecio(),
                        cocina.getImagen()
                );
            }

            @NonNull
            @Override
            public ViewHolderCocina onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cocina, parent, false);
                ViewHolderCocina viewHolderCocina = new ViewHolderCocina(itemView);

                viewHolderCocina.setOnClickListener(new ViewHolderCocina.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (numeroDeMesaSeleccionada != null) {
                            Cocina platilloSeleccionado = getItem(position);
                            if (platilloSeleccionado != null) {
                                DatabaseReference mesaSeleccionadaRef = FirebaseDatabase.getInstance().getReference()
                                        .child("mesas").child(numeroDeMesaSeleccionada).child("platillos");
                                mesaSeleccionadaRef.push().setValue(platilloSeleccionado);
                                Toast.makeText(CocinaMesero.this, "Platillo agregado a la mesa " + numeroDeMesaSeleccionada, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(CocinaMesero.this, "Por favor seleccione una mesa", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void OnItemLongClick(View view, int position) {
                        // Manejar el evento de clic largo si es necesario
                    }
                });
                return viewHolderCocina;
            }
        };

        sharedPreferences = CocinaMesero.this.getSharedPreferences("COCINA", MODE_PRIVATE);
        String ordenar_en = sharedPreferences.getString("Ordenar", "Dos");

        if (ordenar_en.equals("Dos")) {
            recyclerViewCocinaMes.setLayoutManager(new GridLayoutManager(CocinaMesero.this, 2));
        } else if (ordenar_en.equals("Tres")) {
            recyclerViewCocinaMes.setLayoutManager(new GridLayoutManager(CocinaMesero.this, 3));
        }

        firebaseRecyclerAdapter.startListening();
        recyclerViewCocinaMes.setAdapter(firebaseRecyclerAdapter);
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
            startActivity(new Intent(CocinaMesero.this, Ticket.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}