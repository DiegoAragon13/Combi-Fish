package com.example.cf.CategoriasMesero.Kids;

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

import com.example.cf.CategoriasAdmin.KidsA.Kids;
import com.example.cf.CategoriasAdmin.KidsA.ViewHolderKids;
import com.example.cf.CategoriasMesero.Ticket;
import com.example.cf.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class KidsMesero extends AppCompatActivity {

    RecyclerView recyclerViewKidsMes;
    FirebaseDatabase mfirebaseDatabase;
    DatabaseReference mRef;
    FirebaseRecyclerAdapter<Kids, ViewHolderKids> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Kids> options;
    SharedPreferences sharedPreferences;
    Dialog dialog;
    Spinner mesaSpinner;
    String numeroDeMesaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kids_mesero);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Kids & Extras");
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recyclerViewKidsMes = findViewById(R.id.recyclerViewKidsMes);
        recyclerViewKidsMes.setHasFixedSize(true);
        mesaSpinner = findViewById(R.id.mesaSpinner);

        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mfirebaseDatabase.getReference("PLATILLOS_KIDS/");

        dialog = new Dialog(KidsMesero.this);

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

        ListarImagenesKids();
    }

    private void ListarImagenesKids() {
        options = new FirebaseRecyclerOptions.Builder<Kids>().setQuery(mRef, Kids.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Kids, ViewHolderKids>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderKids viewHolderKids, int i, @NonNull Kids kids) {
                viewHolderKids.SeteoKids(
                        getApplicationContext(),
                        kids.getNombre(),
                        kids.getPrecio(),
                        kids.getImagen()
                );
            }

            @NonNull
            @Override
            public ViewHolderKids onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kids, parent, false);
                ViewHolderKids viewHolderKids = new ViewHolderKids(itemView);

                viewHolderKids.setOnClickListener(new ViewHolderKids.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (numeroDeMesaSeleccionada != null) {
                            Kids platilloSeleccionado = getItem(position);
                            if (platilloSeleccionado != null) {
                                DatabaseReference mesaSeleccionadaRef = FirebaseDatabase.getInstance().getReference()
                                        .child("mesas").child(numeroDeMesaSeleccionada).child("platillos");
                                mesaSeleccionadaRef.push().setValue(platilloSeleccionado);
                                Toast.makeText(KidsMesero.this, "Platillo agregado a la mesa " + numeroDeMesaSeleccionada, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(KidsMesero.this, "Por favor seleccione una mesa", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void OnItemLongClick(View view, int position) {
                        // Manejar el evento de clic largo si es necesario
                    }
                });
                return viewHolderKids;
            }
        };

        sharedPreferences = KidsMesero.this.getSharedPreferences("KIDS", MODE_PRIVATE);
        String ordenar_en = sharedPreferences.getString("Ordenar", "Dos");

        if (ordenar_en.equals("Dos")) {
            recyclerViewKidsMes.setLayoutManager(new GridLayoutManager(KidsMesero.this, 2));
        } else if (ordenar_en.equals("Tres")) {
            recyclerViewKidsMes.setLayoutManager(new GridLayoutManager(KidsMesero.this, 3));
        }

        firebaseRecyclerAdapter.startListening();
        recyclerViewKidsMes.setAdapter(firebaseRecyclerAdapter);
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
            startActivity(new Intent(KidsMesero.this, Ticket.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
