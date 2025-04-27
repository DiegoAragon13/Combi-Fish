package com.example.cf.CategoriasAdmin.KidsA;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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


import com.example.cf.CategoriasAdmin.AlcoholA.Alcohol;
import com.example.cf.CategoriasAdmin.AlcoholA.AlcoholA;
import com.example.cf.CategoriasAdmin.CevichesA.AgregarCeviche;
import com.example.cf.CategoriasAdmin.CevichesA.CevicheA;
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

import java.util.HashMap;
import java.util.Map;

public class KidsA extends AppCompatActivity {

    RecyclerView recyclerViewKids;//Nos servira para listar las imagenes
    FirebaseDatabase mfirebaseDatabase;
    DatabaseReference mRef;
    FirebaseRecyclerAdapter<Kids, ViewHolderKids> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Kids> options;
    SharedPreferences sharedPreferences;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kids);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Platillos kids y extras");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        recyclerViewKids = findViewById(R.id.recyclerViewKids);
        recyclerViewKids.setHasFixedSize(true);

        mfirebaseDatabase = mfirebaseDatabase.getInstance();
        mRef = mfirebaseDatabase.getReference("PLATILLOS_KIDS/");

        dialog = new Dialog(KidsA.this);


        ListarImagenesKids();

    }
    private void ListarImagenesKids() {
        options = new FirebaseRecyclerOptions.Builder<Kids>().setQuery(mRef,Kids.class).build();

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
                //INFLAMOS EL ITEM
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kids,parent,false);

                ViewHolderKids viewHolderKids = new ViewHolderKids(itemView);

                viewHolderKids.setOnClickListener(new ViewHolderKids.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(KidsA.this, "Agregar 1", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void OnItemLongClick(View view, int position) {
                        final   String Nombre = getItem(position).getNombre();
                        final String Imagen = getItem(position).getImagen();



                        int precio = getItem(position).getPrecio();
                        final  String precioString = String.valueOf(precio);
                        // Toast.makeText(CevicheA.this, "Editar", Toast.LENGTH_SHORT).show();

                        AlertDialog.Builder builder = new AlertDialog.Builder(KidsA.this);

                        String[] opciones ={"Actualizar","Eliminar"};
                        builder.setItems(opciones, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0){
                                    //Toast.makeText(CevicheA.this, "Actualizar", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(KidsA.this, AgregarKids.class);
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
                return viewHolderKids;
            }
        };
        sharedPreferences = KidsA.this.getSharedPreferences("KIDS",MODE_PRIVATE);
        String ordenar_en = sharedPreferences.getString("Ordenar","Dos");

        //ELEGIR EL TIPO DE VISTA
        if (ordenar_en.equals("Dos")){
            recyclerViewKids.setLayoutManager(new GridLayoutManager(KidsA.this,2));
            firebaseRecyclerAdapter.startListening();
            recyclerViewKids.setAdapter(firebaseRecyclerAdapter);

        } else if (ordenar_en.equals("Tres")) {
            recyclerViewKids.setLayoutManager(new GridLayoutManager(KidsA.this,3));
            firebaseRecyclerAdapter.startListening();
            recyclerViewKids.setAdapter(firebaseRecyclerAdapter);
        }
    }


    private void EliminarDatos(final String NombreActual, final String ImagenActual) {
        AlertDialog.Builder builder = new AlertDialog.Builder(KidsA.this);
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
                            Kids kids = ds.getValue(Kids.class);
                            String uidImagen = kids.getUid();
                            if (uidUsuarioActual.equals(uidImagen)) {
                                // Los UIDs coinciden, mover la imagen a la papelera
                                DatabaseReference refPapelera = FirebaseDatabase.getInstance().getReference("Papelera");
                                Map<String, Object> datosPapelera = new HashMap<>();
                                datosPapelera.put("nombre", kids.getNombre());
                                datosPapelera.put("imagen", ImagenActual);
                                datosPapelera.put("uid", uidImagen);
                                datosPapelera.put("fechaEliminacion", ServerValue.TIMESTAMP); // Opcional: Guardar fecha de eliminación

                                refPapelera.push().setValue(datosPapelera).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Opcionalmente eliminar la entrada original si es necesario o dejarla con un marcador
                                        ds.getRef().removeValue();
                                        Toast.makeText(KidsA.this, "La imagen ha sido movida a la papelera", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(KidsA.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(KidsA.this, "No tienes permiso para mover esta imagen a la papelera", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(KidsA.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(KidsA.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
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
           // Toast.makeText(this, "Agregar imagen", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(KidsA.this, AgregarKids.class));
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