package com.example.cf.FragmentosAdministrador;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.cf.MainActivityAdministrador;
import com.example.cf.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;


public class RegistrarAdmin extends Fragment {

    TextView FechaRegistro;
    EditText Correo,Password,Nombres,Apellidos,Edad;
    Button Registrar;

    FirebaseAuth auth;
    ProgressDialog progressDialog;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_registrar_admin, container, false);


        FechaRegistro = view.findViewById(R.id.FechaRegistro);
        Correo = view.findViewById(R.id.Correo);
        Password = view.findViewById(R.id.Password);
        Nombres = view.findViewById(R.id.Nombres);
        Apellidos = view.findViewById(R.id.Apellidos);
        Edad = view.findViewById(R.id.Edad);

        Registrar = view.findViewById(R.id.Registrar);
        auth = FirebaseAuth.getInstance();//Iniciaizando firebaseautentication

        Date date = new Date();
        SimpleDateFormat fecha = new SimpleDateFormat("d 'de' MMMM 'del' yyyy");
        String Sfecha = fecha.format(date);//Comvertir la fecha en string
        FechaRegistro.setText(Sfecha);

        //Al hcaer clik en registrar

        Registrar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Conv3rtir a string el edittext de correo y contraseña
                    String correo = Correo.getText().toString();
                    String pass = Password.getText().toString();
                    String nombre =Nombres.getText().toString();
                    String apellidos = Apellidos.getText().toString();
                    String edad = Edad.getText().toString();

                    if (correo.equals("") || pass.equals("") || nombre.equals("") || apellidos.equals("")
                            || edad.equals("")){
                        Toast.makeText(getActivity(), "Porfavor llene todos los campos", Toast.LENGTH_SHORT).show();
                    }else{
                        //Validacion del cooreo
                        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
                            Correo.setError("Correo invalido");
                            Correo.setFocusable(true);
                        }
                        else if (pass.length()<6){
                            Password.setError("La contraseña debe ser mayor o igual a 6 :)");
                            Password.setFocusable(true);
                        }else{
                            RegistroAdministradores(correo,pass);
                        }
                    }

            }




        });


            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Registrando, espera por favor");
            progressDialog.setCancelable(false);//No dejar que se cierre con picarle a cualquier lado de la pantalla


    return view;
    }
    //Metodo para registrar administradores xd

    private void RegistroAdministradores(String correo, String pass) {

        progressDialog.show();
        auth.createUserWithEmailAndPassword(correo,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Si el administrador fue creado correctamente
                        if (task.isSuccessful()){
                            progressDialog.dismiss();
                            FirebaseUser user = auth.getCurrentUser();
                            assert user != null;//Afirmar que el administrador no es nulo

                            //Convertir a cadena los datos de los administradores

                            String UID = user.getUid();
                            String correo = Correo.getText().toString();
                            String pass = Password.getText().toString();
                            String nombre = Nombres.getText().toString();
                            String apellidos = Apellidos.getText().toString();
                            String edad = Edad.getText().toString();
                            int EdadInt = Integer.parseInt(edad);

                            HashMap<Object, Object> Administradores = new HashMap<>();

                            Administradores.put("UID",UID);
                            Administradores.put("CORREO",correo);
                            Administradores.put("PASSWORD",pass);
                            Administradores.put("NOMBRES",nombre);
                            Administradores.put("APELLIDOS",apellidos);
                            Administradores.put("EDAD",EdadInt);
                            Administradores.put("Imagen","");


                            //Inicializar Firebasse
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference reference = database.getReference("BASE DE DATOS ADMINISTRADORES");
                            reference.child(UID).setValue(Administradores);

                            startActivity(new Intent(getActivity(), MainActivityAdministrador.class));
                            Toast.makeText(getActivity(), "Registro exitoso oso :)", Toast.LENGTH_SHORT).show();
                            getActivity().finish();

                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Ha ocurrido algun error", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });



    }
}