package com.example.cf.CategoriasAdmin.SinAlcocholA;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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


import com.example.cf.CategoriasAdmin.AlcoholA.AgregarAlcohol;
import com.example.cf.CategoriasAdmin.AlcoholA.Alcohol;
import com.example.cf.CategoriasAdmin.AlcoholA.AlcoholA;
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

public class AgregarSinAlcohol extends AppCompatActivity {

    TextView PrecioPlatillosSinAlcohol;
    EditText NombrePlatillosSinAlcohol;
    ImageView ImagenPlatillo;
    Button  PublicarPlatilloSinAlcohol;

    String RutaDeAlmacenamiento = "Bebida_SinAlchol_Subida/";
    String RutaDeBaseDeDatos = "BEBIDA_SIN_ALCOHOL";
    Uri RutaArchivoUri;

    StorageReference mStorageReference;
    DatabaseReference DatabaseReference;

    ProgressDialog progressDialog;

    String rNombre, rImagen, rPrecio;//dbfhrsrhdgbc etnf ghnebrdxfcvf

  //  int CODIGO_DE_SOLICITUD_IMAGEN =5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_sin_alcohol);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Publicar");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        PrecioPlatillosSinAlcohol = findViewById(R.id.PrecioPlatillosSinAlcohol);
        NombrePlatillosSinAlcohol = findViewById(R.id.NombrePlatillosSinAlcohol);
        ImagenPlatillo = findViewById(R.id.ImagenPlatillo);
        PublicarPlatilloSinAlcohol = findViewById(R.id.PublicarPlatilloSinAlcohol);

        mStorageReference = FirebaseStorage.getInstance().getReference();
        DatabaseReference = FirebaseDatabase.getInstance().getReference(RutaDeBaseDeDatos);
        progressDialog = new ProgressDialog(AgregarSinAlcohol.this);


        Bundle intent = getIntent().getExtras();
        if (intent != null){


            //RECUPERAR LOS DATOS  DE LA ACTIVIDAD CEVICHEA
            rNombre = intent.getString("NombreEnviado");
            rImagen = intent.getString("ImgeneEnviada");
            rPrecio = intent.getString("PrecioEnviado");

            //setear
            NombrePlatillosSinAlcohol.setText(rNombre);
            PrecioPlatillosSinAlcohol.setText(rPrecio);
            Picasso.get().load(rImagen).into(ImagenPlatillo);

            //cambiar el nombre del action bar
            actionBar.setTitle("Actualizar");
            String actualizar = "Actualizar";
            //cambiar el nombre del boton
            PublicarPlatilloSinAlcohol.setText(actualizar);




        }


        ImagenPlatillo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                //SDK 30
                /*Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Seleccionar imagen"),CODIGO_DE_SOLICITUD_IMAGEN);
                */

                //SDK NUEVOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                ObtenerImagenGaleria.launch(intent);
            }
        });

        PublicarPlatilloSinAlcohol.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (PublicarPlatilloSinAlcohol.getText().equals("Publicar")){
                    SubirImagen();
                }else{
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
        imagen.delete().addOnSuccessListener(new  OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //si la imagen se elimino
                Toast.makeText(AgregarSinAlcohol.this, "La imagen anterior a sido eliminada ", Toast.LENGTH_SHORT).show();
                SubirNuevaImgen();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AgregarSinAlcohol.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }


    private void SubirNuevaImgen() {
        String nuevaImagen = System.currentTimeMillis()+".png";
        StorageReference mStorageReference2 = mStorageReference.child(RutaDeAlmacenamiento + nuevaImagen);
        Bitmap bitmap = ((BitmapDrawable)ImagenPlatillo.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        byte [] data = byteArrayOutputStream.toByteArray();
        UploadTask uploadTask = mStorageReference2.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AgregarSinAlcohol.this, "Nueva imagen cargada", Toast.LENGTH_SHORT).show();
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                Uri downloadUri = uriTask.getResult();
                ActualizarImagenDB(downloadUri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AgregarSinAlcohol.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void ActualizarImagenDB(final String nuevaImagen) {
        final String nombreActualizar = NombrePlatillosSinAlcohol.getText().toString();
        final String precioActualizar = PrecioPlatillosSinAlcohol.getText().toString();
        final int precioInt = Integer.parseInt(precioActualizar);
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("BEBIDA_SIN_ALCOHOL");

        Query query = databaseReference.orderByChild("nombre").equalTo(rNombre);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    // Guardar el registro actual en "Papelera/Actualizaciones"
                    SinAlcohol sinalcoholActual = ds.getValue(SinAlcohol.class);
                    DatabaseReference papeleraRef = firebaseDatabase.getReference("Papelera/Actualizaciones");
                    String idPapelera = papeleraRef.push().getKey();
                    papeleraRef.child(idPapelera).setValue(sinalcoholActual);

                    // Actualizar el registro en "BEBIDA_ALCOHOL"
                    ds.getRef().child("nombre").setValue(nombreActualizar);
                    ds.getRef().child("imagen").setValue(nuevaImagen);
                    ds.getRef().child("precio").setValue(precioInt);

                    // Actualizar la lista de admins que han editado el registro
                    DatabaseReference adminRef = ds.getRef().child("admins");
                    adminRef.child(uid).setValue(true);

                    progressDialog.dismiss();
                    Toast.makeText(AgregarSinAlcohol.this, "Actualizado correctamente", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AgregarSinAlcohol.this, SinAlcoholA.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(AgregarSinAlcohol.this, "Error al actualizar la base de datos", Toast.LENGTH_SHORT).show();
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

                                    String mNombre = NombrePlatillosSinAlcohol.getText().toString();
                                    String mPrecio = PrecioPlatillosSinAlcohol.getText().toString();
                                    int PRECIO = Integer.parseInt(mPrecio);

                                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                    SinAlcohol sinAlcohol = new SinAlcohol(downloadUri, mNombre, PRECIO, uid);
                                    String ID_IMAGEN = DatabaseReference.push().getKey();

                                    DatabaseReference.child(ID_IMAGEN).setValue(sinAlcohol);

                                    progressDialog.dismiss();
                                    Toast.makeText(AgregarSinAlcohol.this, "Agregado Exitosamente", Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(AgregarSinAlcohol.this, SinAlcoholA.class));
                                    finish();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AgregarSinAlcohol.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
    private String ObtenerExtensionDelArchivo(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    /*
    //COMPROBAR SI LA IMAGEN SELECCIONADA POR EL ADMIN FUE CORRECTA
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CODIGO_DE_SOLICITUD_IMAGEN
                && resultCode == RESULT_OK
                && data != null
                && data.getData() !=null){

            RutaArchivoUri = data.getData();
            try{
                //CONVERTIMOS A UN BITMAP
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),RutaArchivoUri);
                //SET LA IMAGEN
                ImagenPlatillo.setImageBitmap(bitmap);
            }catch (Exception e){
                Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }*/

    //Obtener imagen desde galeria
    private ActivityResultLauncher<Intent> ObtenerImagenGaleria = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //SE MANEJA EL RESULTADO DEL INTENT
                    if (result.getResultCode() == Activity.RESULT_OK){
                        //SELECCION
                        Intent data = result.getData();
                        RutaArchivoUri = data.getData();
                        ImagenPlatillo.setImageURI(RutaArchivoUri);
                    }else{
                        Toast.makeText(AgregarSinAlcohol.this, "Cancelado", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );



}