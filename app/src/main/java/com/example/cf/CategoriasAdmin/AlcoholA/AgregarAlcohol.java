package com.example.cf.CategoriasAdmin.AlcoholA;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cf.CategoriasAdmin.CevichesA.AgregarCeviche;
import com.example.cf.CategoriasAdmin.CevichesA.Ceviche;
import com.example.cf.CategoriasAdmin.CevichesA.CevicheA;
import com.example.cf.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class AgregarAlcohol extends AppCompatActivity {

    TextView PrecioPlatillosAlcohol;
    EditText NombrePlatillosAlcohol;
    ImageView ImagenPlatillo;
    Button PublicarPlatilloAlcohol;

    String RutaDeAlmacenamiento = "Bebida_Alcohol_Subida/";
    String RutaDeBaseDeDatos = "BEBIDA_ALCOHOL";
    Uri RutaArchivoUri;

    StorageReference mStorageReference;
    com.google.firebase.database.DatabaseReference DatabaseReference;

    ProgressDialog progressDialog;

    String rNombre, rImagen, rPrecio;//dbfhrsrhdgbc etnf ghnebrdxfcvf

    int CODIGO_DE_SOLICITUD_IMAGEN = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_alcohol);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Publicar");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        PrecioPlatillosAlcohol = findViewById(R.id.PrecioPlatillosAlcohol);
        NombrePlatillosAlcohol = findViewById(R.id.NombrePlatillosAlcohol);
        ImagenPlatillo = findViewById(R.id.ImagenPlatillo);
        PublicarPlatilloAlcohol = findViewById(R.id.PublicarPlatilloAlcohol);

        mStorageReference = FirebaseStorage.getInstance().getReference();
        DatabaseReference = FirebaseDatabase.getInstance().getReference(RutaDeBaseDeDatos);
        progressDialog = new ProgressDialog(AgregarAlcohol.this);


        Bundle intent = getIntent().getExtras();
        if (intent != null) {


            //RECUPERAR LOS DATOS  DE LA ACTIVIDAD Alcohol
            rNombre = intent.getString("NombreEnviado");
            rImagen = intent.getString("ImgeneEnviada");
            rPrecio = intent.getString("PrecioEnviado");

            //setear
            NombrePlatillosAlcohol.setText(rNombre);
            PrecioPlatillosAlcohol.setText(rPrecio);
            Picasso.get().load(rImagen).into(ImagenPlatillo);

            //cambiar el nombre del action bar
            actionBar.setTitle("Actualizar");
            String actualizar = "Actualizar";
            //cambiar el nombre del boton
            PublicarPlatilloAlcohol.setText(actualizar);


        }


        ImagenPlatillo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Seleccionar imagen"), CODIGO_DE_SOLICITUD_IMAGEN);
            }
        });

        PublicarPlatilloAlcohol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PublicarPlatilloAlcohol.getText().equals("Publicar")) {
                    SubirImagen();
                } else {
                    EmpezarActualizacion();
                }

            }
        });

    }

    private void EmpezarActualizacion() {
        progressDialog.setTitle("Actualizado");
        progressDialog.setMessage("Espere por favor...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        EliminarImagenAnterior();
    }

    private void EliminarImagenAnterior() {
        StorageReference imagen = getInstance().getReferenceFromUrl(rImagen);
        imagen.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //si la imagen se elimino
                Toast.makeText(AgregarAlcohol.this, "La imagen anterior a sido eliminada ", Toast.LENGTH_SHORT).show();
                SubirNuevaImgen();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AgregarAlcohol.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }


    private void SubirNuevaImgen() {
        String nuevaImagen = System.currentTimeMillis() + ".png";
        StorageReference mStorageReference2 = mStorageReference.child(RutaDeAlmacenamiento + nuevaImagen);
        Bitmap bitmap = ((BitmapDrawable)ImagenPlatillo.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte [] data = byteArrayOutputStream.toByteArray();
        UploadTask uploadTask = mStorageReference2.putBytes(data);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String downloadUri = uri.toString();
                        ActualizarImagenDB(downloadUri); // Llamada a ActualizarImagenDB con la nueva imagen
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(AgregarAlcohol.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ActualizarImagenDB(final String nuevaImagen) {
        final String nombreActualizar = NombrePlatillosAlcohol.getText().toString();
        final String precioActualizar = PrecioPlatillosAlcohol.getText().toString();
        final int precioInt = Integer.parseInt(precioActualizar);
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("BEBIDA_ALCOHOL");

        Query query = databaseReference.orderByChild("nombre").equalTo(rNombre);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    // Guardar el registro actual en "Papelera/Actualizaciones"
                    Alcohol alcoholActual = ds.getValue(Alcohol.class);
                    DatabaseReference papeleraRef = firebaseDatabase.getReference("Papelera/Actualizaciones");
                    String idPapelera = papeleraRef.push().getKey();
                    papeleraRef.child(idPapelera).setValue(alcoholActual);

                    // Actualizar el registro en "BEBIDA_ALCOHOL"
                    ds.getRef().child("nombre").setValue(nombreActualizar);
                    ds.getRef().child("imagen").setValue(nuevaImagen);
                    ds.getRef().child("precio").setValue(precioInt);

                    // Actualizar la lista de admins que han editado el registro
                    DatabaseReference adminRef = ds.getRef().child("admins");
                    adminRef.child(uid).setValue(true);

                    progressDialog.dismiss();
                    Toast.makeText(AgregarAlcohol.this, "Actualizado correctamente", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AgregarAlcohol.this, AlcoholA.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(AgregarAlcohol.this, "Error al actualizar la base de datos", Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void SubirImagen() {
        if (RutaArchivoUri != null) {
            progressDialog.setTitle("Espere por favor");
            progressDialog.setMessage("Subiendo imagen del platillo ...");
            progressDialog.setCancelable(false);
            StorageReference storageReference2 = mStorageReference.child(RutaDeAlmacenamiento + System.currentTimeMillis() + "." + ObtenerExtensionDelArchivo(RutaArchivoUri));
            storageReference2.putFile(RutaArchivoUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUri = uri.toString();

                                    String mNombre = NombrePlatillosAlcohol.getText().toString();
                                    String mPrecio = PrecioPlatillosAlcohol.getText().toString();
                                    int PRECIO = Integer.parseInt(mPrecio);

                                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                    Alcohol alcohol = new Alcohol(downloadUri, mNombre, PRECIO, uid);
                                    String ID_IMAGEN = DatabaseReference.push().getKey();

                                    DatabaseReference.child(ID_IMAGEN).setValue(alcohol);

                                    progressDialog.dismiss();
                                    Toast.makeText(AgregarAlcohol.this, "Agregado Exitosamente", Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(AgregarAlcohol.this, AlcoholA.class));
                                    finish();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AgregarAlcohol.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            progressDialog.setTitle("Publicando");
                            progressDialog.setCancelable(false);
                        }
                    });
        } else {
            Toast.makeText(this, "Debe asignar una imagen", Toast.LENGTH_SHORT).show();
        }
    }

    //OBTENEMOS LA EXTENSION .JPG O .PNG
    private String ObtenerExtensionDelArchivo(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    //COMPROBAR SI LA IMAGEN SELECCIONADA POR EL ADMIN FUE CORRECTA
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODIGO_DE_SOLICITUD_IMAGEN
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            RutaArchivoUri = data.getData();
            try {
                //CONVERTIMOS A UN BITMAP
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), RutaArchivoUri);
                //SET LA IMAGEN
                ImagenPlatillo.setImageBitmap(bitmap);
            } catch (Exception e) {
                Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
