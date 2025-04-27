package com.example.cf.CategoriasMesero.Quesadillas;

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

import com.example.cf.CategoriasAdmin.QuesadillasA.Quesadilla;
import com.example.cf.CategoriasAdmin.QuesadillasA.ViewHolderQuesadillas;
import com.example.cf.CategoriasMesero.Ticket;
import com.example.cf.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class QuesadillaMesero extends AppCompatActivity {

    RecyclerView recyclerViewQuesadillaMes;
    FirebaseDatabase mfirebaseDatabase;
    DatabaseReference mRef;
    FirebaseRecyclerAdapter<Quesadilla, ViewHolderQuesadillas> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Quesadilla> options;
    SharedPreferences sharedPreferences;
    Dialog dialog;
    Spinner mesaSpinner;
    String numeroDeMesaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quesadillas_mesero);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Quesadilla & Tacos");
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recyclerViewQuesadillaMes = findViewById(R.id.recyclerViewCevicheMes);
        recyclerViewQuesadillaMes.setHasFixedSize(true);

        mesaSpinner = findViewById(R.id.mesaSpinner);

        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mfirebaseDatabase.getReference("PLATILLOS_QUESADILLA/");

        dialog = new Dialog(QuesadillaMesero.this);

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

        ListarImagenesQuesadilla();
    }

    private void ListarImagenesQuesadilla() {
        options = new FirebaseRecyclerOptions.Builder<Quesadilla>().setQuery(mRef, Quesadilla.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Quesadilla, ViewHolderQuesadillas>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderQuesadillas viewHolderQuesadillas, int i, @NonNull Quesadilla quesadillas) {
                viewHolderQuesadillas.SeteoQuesadillas(
                        getApplicationContext(),
                        quesadillas.getNombre(),
                        quesadillas.getPrecio(),
                        quesadillas.getImagen()
                );
            }

            @NonNull
            @Override
            public ViewHolderQuesadillas onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quesadilla, parent, false);
                ViewHolderQuesadillas viewHolderQuesadillas = new ViewHolderQuesadillas(itemView);

                viewHolderQuesadillas.setOnClickListener(new ViewHolderQuesadillas.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (numeroDeMesaSeleccionada != null) {
                            Quesadilla platilloSeleccionado = getItem(position);
                            if (platilloSeleccionado != null) {
                                DatabaseReference mesaSeleccionadaRef = FirebaseDatabase.getInstance().getReference()
                                        .child("mesas").child(numeroDeMesaSeleccionada).child("platillos");
                                mesaSeleccionadaRef.push().setValue(platilloSeleccionado);
                                Toast.makeText(QuesadillaMesero.this, "Platillo agregado a la mesa " + numeroDeMesaSeleccionada, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(QuesadillaMesero.this, "Por favor seleccione una mesa", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void OnItemLongClick(View view, int position) {
                        // Manejar el evento de clic largo si es necesario
                    }
                });
                return viewHolderQuesadillas;
            }
        };

        sharedPreferences = QuesadillaMesero.this.getSharedPreferences("COCINA", MODE_PRIVATE);
        String ordenar_en = sharedPreferences.getString("Ordenar", "Dos");

        // ELEGIR EL TIPO DE VISTA
        if (ordenar_en.equals("Dos")) {
            recyclerViewQuesadillaMes.setLayoutManager(new GridLayoutManager(QuesadillaMesero.this, 2));
        } else if (ordenar_en.equals("Tres")) {
            recyclerViewQuesadillaMes.setLayoutManager(new GridLayoutManager(QuesadillaMesero.this, 3));
        }

        firebaseRecyclerAdapter.startListening();
        recyclerViewQuesadillaMes.setAdapter(firebaseRecyclerAdapter);
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
            startActivity(new Intent(QuesadillaMesero.this, Ticket.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
