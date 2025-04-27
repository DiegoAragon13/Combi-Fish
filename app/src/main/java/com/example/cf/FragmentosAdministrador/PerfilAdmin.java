package com.example.cf.FragmentosAdministrador;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.cf.R;
import com.google.firebase.auth.FirebaseAuth;
import static com.google.firebase.storage.FirebaseStorage.getInstance;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


public class PerfilAdmin extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    DatabaseReference BASE_DE_DATOS_ADMINISTRADORES;


    StorageReference storageReference;
    String RutaDeAlmacenamiento = "Fotos_Perfil_Administrador/*";

    private static final int CODIGO_DE_SOLICITUD_DE_CAMARA = 100;
    private static final int CODIGO_DE_GALERIA_DE_SELECCION_DE_IMAGENES = 200;

    private String [] permisos_De_la_camara;

    private Uri imagen_uri;
    private String imagen_perfil;
    private ProgressDialog progressDialog;

    ImageView FOTOPERFILIMG;
    TextView UAIPERFIL,NOMBRESPERFIL,APELLIDOSPERFIL,CORREOPERFIL,PASSWORDPERFIL,EDADPERFIL;
    Button ACTUALIZARPASS,ACTUALIZARDATOS;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil_admin,container,false);

        FOTOPERFILIMG = view.findViewById(R.id.FOTOPERFILIMG);
        UAIPERFIL = view.findViewById(R.id.UAIPERFIL);
        NOMBRESPERFIL = view.findViewById(R.id.NOMBRESPERFIL);
        APELLIDOSPERFIL = view.findViewById(R.id.APELLIDOSPERFIL);
        CORREOPERFIL = view.findViewById(R.id.CORREOPERFIL);
        PASSWORDPERFIL = view.findViewById(R.id.PASSWORDPERFIL);
        EDADPERFIL = view.findViewById(R.id.EDADPERFIL);



        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        storageReference = getInstance().getReference();




        progressDialog = new ProgressDialog(getActivity());

        BASE_DE_DATOS_ADMINISTRADORES = FirebaseDatabase.getInstance().getReference("BASE DE DATOS ADMINISTRADORES");

        BASE_DE_DATOS_ADMINISTRADORES.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    String uid = ""+snapshot.child("UID").getValue();
                    String nombre = ""+snapshot.child("NOMBRES").getValue();
                    String apellidos = ""+snapshot.child("APELLIDOS").getValue();
                    String correo = ""+snapshot.child("CORREO").getValue();
                    String password = ""+snapshot.child("PASSWORD").getValue();
                    String edad = ""+snapshot.child("EDAD").getValue();
                    String imagen = ""+snapshot.child("Imagen").getValue();

                    UAIPERFIL.setText(uid);
                    NOMBRESPERFIL.setText(nombre);
                    APELLIDOSPERFIL.setText(apellidos);
                    CORREOPERFIL.setText(correo);
                    PASSWORDPERFIL.setText(password);
                    EDADPERFIL.setText(edad);

                    try {
                        //SI EXISTE IMAGEN EN LA BASE DE DATOS ADMIN
                        Picasso.get().load(imagen).placeholder(R.drawable.perfil).into(FOTOPERFILIMG);

                    }catch (Exception e){
                        //NO EXISTE IMAGEN EN LA BASE DE DATOS ADMIN
                        Picasso.get().load(R.drawable.perfil).into(FOTOPERFILIMG);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        return view;
    }





}