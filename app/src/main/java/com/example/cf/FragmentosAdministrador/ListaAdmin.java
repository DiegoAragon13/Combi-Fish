package com.example.cf.FragmentosAdministrador;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cf.Adaptador.Adaptador;
import com.example.cf.Modelo.Administrador;
import com.example.cf.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ListaAdmin extends Fragment {


        RecyclerView Administradores_recyclerView;
        Adaptador adaptador;
        List<Administrador> administradorList;
        FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lista_admin,container,false);


        Administradores_recyclerView = view.findViewById(R.id.cocinero_pedidos_recyclerview);
        Administradores_recyclerView.setHasFixedSize(true);
        Administradores_recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),1));
        administradorList = new ArrayList<>();

        firebaseAuth = firebaseAuth.getInstance();

        ObtenerLista();

        return view;
    }

    private void ObtenerLista() {
       final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("BASE DE DATOS ADMINISTRADORES");
        reference.orderByChild("APELLIDOS").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                administradorList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    Administrador administrador = ds.getValue(Administrador.class);
                    //CONFICION PARA QUE NE LA LISTA SE VISUALICEN TODOS LOS USUARIOS EXCEPTO EL INICIADO
                    assert administrador != null;
                    assert user != null;
                  /*  if (!administrador.getUID().equals(user.getUid())){
                        administradorList.add(administrador);
                    }*/
                    administradorList.add(administrador);



                    adaptador = new Adaptador(getActivity(),administradorList);
                    Administradores_recyclerView.setAdapter(adaptador);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
