package com.example.cf.CategoriasMesero.Ceviche;

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

import com.example.cf.CategoriasAdmin.CevichesA.Ceviche;
import com.example.cf.CategoriasAdmin.CevichesA.ViewHolderCeviche;
import com.example.cf.CategoriasMesero.Ticket;
import com.example.cf.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CevicheMesero extends AppCompatActivity {

    RecyclerView recyclerViewCevicheMes;
    FirebaseDatabase mfirebaseDatabase;
    DatabaseReference mRef;
    FirebaseRecyclerAdapter<Ceviche, ViewHolderCeviche> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Ceviche> options;
    SharedPreferences sharedPreferences;
    Dialog dialog;
    Spinner mesaSpinner;
    String numeroDeMesaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ceviche_mesero);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Ceviches & Tostadas");
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recyclerViewCevicheMes = findViewById(R.id.recyclerViewCevicheMes);
        recyclerViewCevicheMes.setHasFixedSize(true);
        mesaSpinner = findViewById(R.id.mesaSpinner);

        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mfirebaseDatabase.getReference("PLATILLOS_CEEVICHE/");

        dialog = new Dialog(CevicheMesero.this);

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

        ListarImagenesCeviche();
    }

    private void ListarImagenesCeviche() {
        options = new FirebaseRecyclerOptions.Builder<Ceviche>().setQuery(mRef, Ceviche.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Ceviche, ViewHolderCeviche>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderCeviche viewHolderCeviche, int i, @NonNull Ceviche ceviche) {
                viewHolderCeviche.SeteoCeviche(
                        getApplicationContext(),
                        ceviche.getNombre(),
                        ceviche.getPrecio(),
                        ceviche.getImagen()
                );
            }

            @NonNull
            @Override
            public ViewHolderCeviche onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ceviche, parent, false);
                ViewHolderCeviche viewHolderCeviche = new ViewHolderCeviche(itemView);

                viewHolderCeviche.setOnClickListener(new ViewHolderCeviche.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (numeroDeMesaSeleccionada != null) {
                            Ceviche platilloSeleccionado = getItem(position);
                            if (platilloSeleccionado != null) {
                                DatabaseReference mesaSeleccionadaRef = FirebaseDatabase.getInstance().getReference()
                                        .child("mesas").child(numeroDeMesaSeleccionada).child("platillos");
                                mesaSeleccionadaRef.push().setValue(platilloSeleccionado);
                                Toast.makeText(CevicheMesero.this, "Platillo agregado a la mesa " + numeroDeMesaSeleccionada, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(CevicheMesero.this, "Por favor seleccione una mesa", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void OnItemLongClick(View view, int position) {
                        // Manejar el evento de clic largo si es necesario
                    }
                });
                return viewHolderCeviche;
            }
        };

        sharedPreferences = CevicheMesero.this.getSharedPreferences("CEVICHE", MODE_PRIVATE);
        String ordenar_en = sharedPreferences.getString("Ordenar", "Dos");

        if (ordenar_en.equals("Dos")) {
            recyclerViewCevicheMes.setLayoutManager(new GridLayoutManager(CevicheMesero.this, 2));
        } else if (ordenar_en.equals("Tres")) {
            recyclerViewCevicheMes.setLayoutManager(new GridLayoutManager(CevicheMesero.this, 3));
        }

        firebaseRecyclerAdapter.startListening();
        recyclerViewCevicheMes.setAdapter(firebaseRecyclerAdapter);
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
            startActivity(new Intent(CevicheMesero.this, Ticket.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
