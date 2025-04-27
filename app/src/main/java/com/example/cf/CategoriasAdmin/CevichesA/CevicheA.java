package com.example.cf.CategoriasAdmin.CevichesA;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cf.CategoriasAdmin.AlcoholA.Alcohol;
import com.example.cf.CategoriasAdmin.AlcoholA.AlcoholA;
import com.example.cf.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

import java.util.HashMap;
import java.util.Map;

public class CevicheA extends AppCompatActivity {

    RecyclerView recyclerViewCeviche;//Nos servira para listar las imagenes
    FirebaseDatabase mfirebaseDatabase;
    DatabaseReference mRef;
    FirebaseRecyclerAdapter<Ceviche, ViewHolderCeviche> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Ceviche> options;
    SharedPreferences sharedPreferences;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ceviche);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Ceviche & Tostadas");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        recyclerViewCeviche = findViewById(R.id.recyclerViewCeviche);
        recyclerViewCeviche.setHasFixedSize(true);

        mfirebaseDatabase = mfirebaseDatabase.getInstance();
        mRef = mfirebaseDatabase.getReference("PLATILLOS_CEEVICHE/");

        dialog = new Dialog(CevicheA.this);


        ListarImagenesCeviche();



    }

    private void ListarImagenesCeviche() {
        options = new FirebaseRecyclerOptions.Builder<Ceviche>().setQuery(mRef,Ceviche.class).build();

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
                //INFLAMOS EL ITEM
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ceviche,parent,false);

               ViewHolderCeviche viewHolderCeviche = new ViewHolderCeviche(itemView);

               viewHolderCeviche.setOnClickListener(new ViewHolderCeviche.ClickListener() {
                   @Override
                   public void onItemClick(View view, int position) {
                       Toast.makeText(CevicheA.this, "Agregar 1", Toast.LENGTH_SHORT).show();
                   }

                   @Override
                   public void OnItemLongClick(View view, int position) {
                     final   String Nombre = getItem(position).getNombre();
                     final String Imagen = getItem(position).getImagen();



                       int precio = getItem(position).getPrecio();
                     final  String precioString = String.valueOf(precio);
                      // Toast.makeText(CevicheA.this, "Editar", Toast.LENGTH_SHORT).show();

                       AlertDialog.Builder builder = new AlertDialog.Builder(CevicheA.this);

                       String[] opciones ={"Actualizar","Eliminar"};
                       builder.setItems(opciones, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                               if (i == 0){
                                   //Toast.makeText(CevicheA.this, "Actualizar", Toast.LENGTH_SHORT).show();
                                   Intent intent = new Intent(CevicheA.this,AgregarCeviche.class);
                                   intent.putExtra("NombreEnviado",Nombre);
                                   intent.putExtra("ImgeneEnviada",Imagen);
                                   intent.putExtra("PrecioEnviado",precioString);
                                   startActivity(intent);
                               }
                               if (i == 1){
                                   EliminarDatos(Nombre,Imagen);
                               }
                           }
                       });
                       builder.create().show();
                   }
               });
                return viewHolderCeviche;
            }
        };
        sharedPreferences = CevicheA.this.getSharedPreferences("CECICHE",MODE_PRIVATE);
        String ordenar_en = sharedPreferences.getString("Ordenar","Dos");

        //ELEGIR EL TIPO DE VISTA
        if (ordenar_en.equals("Dos")){
            recyclerViewCeviche.setLayoutManager(new GridLayoutManager(CevicheA.this,2));
            firebaseRecyclerAdapter.startListening();
            recyclerViewCeviche.setAdapter(firebaseRecyclerAdapter);

        } else if (ordenar_en.equals("Tres")) {
            recyclerViewCeviche.setLayoutManager(new GridLayoutManager(CevicheA.this,3));
            firebaseRecyclerAdapter.startListening();
            recyclerViewCeviche.setAdapter(firebaseRecyclerAdapter);
        }
    }


    private void EliminarDatos(final String NombreActual, final String ImagenActual) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CevicheA.this);
        builder.setTitle("Eliminar");
        builder.setMessage("¿Desea eliminar la imagen a la papelera?");

        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Obtener el UID del usuario actual
                final String uidUsuarioActual = FirebaseAuth.getInstance().getCurrentUser().getUid();

                // Obtener la referencia a la base de datos
                Query query = mRef.orderByChild("nombre").equalTo(NombreActual);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Ceviche ceviche = ds.getValue(Ceviche.class);
                            String uidImagen = ceviche.getUid();
                            if (uidUsuarioActual.equals(uidImagen)) {
                                // Los UIDs coinciden, mover la imagen a la papelera
                                DatabaseReference refPapelera = FirebaseDatabase.getInstance().getReference("Papelera");
                                Map<String, Object> datosPapelera = new HashMap<>();
                                datosPapelera.put("nombre", ceviche.getNombre());
                                datosPapelera.put("imagen", ImagenActual);
                                datosPapelera.put("uid", uidImagen);
                                datosPapelera.put("fechaEliminacion", ServerValue.TIMESTAMP); // Opcional: Guardar fecha de eliminación

                                refPapelera.push().setValue(datosPapelera).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Opcionalmente eliminar la entrada original si es necesario o dejarla con un marcador
                                        ds.getRef().removeValue();
                                        Toast.makeText(CevicheA.this, "La imagen ha sido movida a la papelera", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(CevicheA.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(CevicheA.this, "No tienes permiso para mover esta imagen a la papelera", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CevicheA.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(CevicheA.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
            }
        });

        builder.create().show();
    }



    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseRecyclerAdapter!=null){
            firebaseRecyclerAdapter.startListening();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_agregar,menu);
        menuInflater.inflate(R.menu.menu_vista,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.Agregar) {
            startActivity(new Intent(CevicheA.this,AgregarCeviche.class));
            finish();
        } else if (item.getItemId() == R.id.Vista) {
            Ordenar_Imagenes();
        }
        return super.onOptionsItemSelected(item);
    }

    private void Ordenar_Imagenes(){
        TextView OrdenarTXT;
        Button Dos_Columnas,Tres_Columnas;


        dialog.setContentView(R.layout.dialog_ordenar);

        OrdenarTXT = dialog.findViewById(R.id.OrdenarTXT);
        Dos_Columnas = dialog.findViewById(R.id.Dos_Columnas);
        Tres_Columnas = dialog.findViewById(R.id.Tres_Columnas);


        //EVENTO DOS COLUMNAS
        Dos_Columnas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Ordenar","Dos");
                editor.apply();
                recreate();
                dialog.dismiss();
            }
        });


        //EVENTO TREA COLUMNAS
        Tres_Columnas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Ordenar","Tres");
                editor.apply();
                recreate();
                dialog.dismiss();

            }
        });

        dialog.show();

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}