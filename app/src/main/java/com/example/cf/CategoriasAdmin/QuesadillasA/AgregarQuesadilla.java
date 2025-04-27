package com.example.cf.CategoriasAdmin.QuesadillasA;

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
import com.example.cf.CategoriasAdmin.QuesadillasA.AgregarQuesadilla;
import com.example.cf.CategoriasAdmin.QuesadillasA.Quesadilla;
import com.example.cf.CategoriasAdmin.QuesadillasA.QuesadillasA;
import com.example.cf.CategoriasAdmin.SinAlcocholA.AgregarSinAlcohol;
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

public class AgregarQuesadilla extends AppCompatActivity {


    TextView PrecioPlatillosQuesadilla;
    EditText NombrePlatillosQuesadilla;
    ImageView ImagenPlatillo;
    Button PublicarPlatilloQuesadilla;

    String RutaDeAlmacenamiento = "Platillos_Quesadilla_Subida/";
    String RutaDeBaseDeDatos = "PLATILLOS_QUESADILLA";
    Uri RutaArchivoUri;

    StorageReference mStorageReference;
    DatabaseReference DatabaseReference;

    ProgressDialog progressDialog;

    String rNombre, rImagen, rPrecio;//dbfhrsrhdgbc etnf ghnebrdxfcvf

//    int CODIGO_DE_SOLICITUD_IMAGEN =5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_quesadilla);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Publicar");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        PrecioPlatillosQuesadilla = findViewById(R.id.PrecioPlatillosQuesadilla);
        NombrePlatillosQuesadilla = findViewById(R.id.NombrePlatillosQuesadilla);
        ImagenPlatillo = findViewById(R.id.ImagenPlatillo);
        PublicarPlatilloQuesadilla = findViewById(R.id.PublicarPlatilloQuesadilla);

        mStorageReference = FirebaseStorage.getInstance().getReference();
        DatabaseReference = FirebaseDatabase.getInstance().getReference(RutaDeBaseDeDatos);
        progressDialog = new ProgressDialog(AgregarQuesadilla.this);


        Bundle intent = getIntent().getExtras();
        if (intent != null){


            //RECUPERAR LOS DATOS  DE LA ACTIVIDAD QUESADILLA
            rNombre = intent.getString("NombreEnviado");
            rImagen = intent.getString("ImgeneEnviada");
            rPrecio = intent.getString("PrecioEnviado");

            //setear
            NombrePlatillosQuesadilla.setText(rNombre);
            PrecioPlatillosQuesadilla.setText(rPrecio);
            Picasso.get().load(rImagen).into(ImagenPlatillo);

            //cambiar el nombre del action bar
            actionBar.setTitle("Actualizar");
            String actualizar = "Actualizar";
            //cambiar el nombre del boton
            PublicarPlatilloQuesadilla.setText(actualizar);




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

        PublicarPlatilloQuesadilla.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (PublicarPlatilloQuesadilla.getText().equals("Publicar")){
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
                Toast.makeText(AgregarQuesadilla.this, "La imagen anterior a sido eliminada ", Toast.LENGTH_SHORT).show();
                SubirNuevaImgen();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AgregarQuesadilla.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(AgregarQuesadilla.this, "Nueva imagen cargada", Toast.LENGTH_SHORT).show();
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                Uri downloadUri = uriTask.getResult();
                ActualizarImagenDB(downloadUri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AgregarQuesadilla.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void ActualizarImagenDB(final String nuevaImagen) {
        final String nombreActualizar = NombrePlatillosQuesadilla.getText().toString();
        final String precioActualizar = PrecioPlatillosQuesadilla.getText().toString();
        final int precioInt = Integer.parseInt(precioActualizar);
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("PLATILLOS_QUESADILLA");

        Query query = databaseReference.orderByChild("nombre").equalTo(rNombre);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    // Guardar el registro actual en "Papelera/Actualizaciones"
                    Quesadilla QuesadillaActual = ds.getValue(Quesadilla.class);
                    DatabaseReference papeleraRef = firebaseDatabase.getReference("Papelera/Actualizaciones");
                    String idPapelera = papeleraRef.push().getKey();
                    papeleraRef.child(idPapelera).setValue(QuesadillaActual);

                    // Actualizar el registro en "BEBIDA_ALCOHOL"
                    ds.getRef().child("nombre").setValue(nombreActualizar);
                    ds.getRef().child("imagen").setValue(nuevaImagen);
                    ds.getRef().child("precio").setValue(precioInt);

                    // Actualizar la lista de admins que han editado el registro
                    DatabaseReference adminRef = ds.getRef().child("admins");
                    adminRef.child(uid).setValue(true);

                    progressDialog.dismiss();
                    Toast.makeText(AgregarQuesadilla.this, "Actualizado correctamente", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AgregarQuesadilla.this, QuesadillasA.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(AgregarQuesadilla.this, "Error al actualizar la base de datos", Toast.LENGTH_SHORT).show();
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

                                    String mNombre = NombrePlatillosQuesadilla.getText().toString();
                                    String mPrecio = PrecioPlatillosQuesadilla.getText().toString();
                                    int PRECIO = Integer.parseInt(mPrecio);

                                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                    Quesadilla quesadilla = new Quesadilla(downloadUri, mNombre, PRECIO, uid);
                                    String ID_IMAGEN = DatabaseReference.push().getKey();

                                    DatabaseReference.child(ID_IMAGEN).setValue(quesadilla);

                                    progressDialog.dismiss();
                                    Toast.makeText(AgregarQuesadilla.this, "Agregado Exitosamente", Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(AgregarQuesadilla.this, QuesadillasA.class));
                                    finish();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AgregarQuesadilla.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
    /*//COMPROBAR SI LA IMAGEN SELECCIONADA POR EL ADMIN FUE CORRECTA
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
                        Toast.makeText(AgregarQuesadilla.this, "Cancelado", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

}