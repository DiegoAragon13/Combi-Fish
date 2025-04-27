package com.example.cf.FragmentosCliente;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.cf.CategoriasAdmin.AlcoholA.AlcoholA;
import com.example.cf.CategoriasAdmin.CevichesA.CevicheA;
import com.example.cf.CategoriasAdmin.KidsA.KidsA;
import com.example.cf.CategoriasAdmin.QuesadillasA.QuesadillasA;
import com.example.cf.CategoriasAdmin.SinAlcocholA.SinAlcoholA;
import com.example.cf.CategoriasMesero.Alcohol.AlcoholMesero;
import com.example.cf.CategoriasMesero.Ceviche.CevicheMesero;
import com.example.cf.CategoriasMesero.Cocina.CocinaMesero;
import com.example.cf.CategoriasMesero.Kids.KidsMesero;
import com.example.cf.CategoriasMesero.Quesadillas.QuesadillaMesero;
import com.example.cf.CategoriasMesero.SinAlcohol.SinAlcoholMesero;
import com.example.cf.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InicioCliente extends Fragment {

    private Spinner spinner;
    private DatabaseReference mDatabase;

    private Button CevichesTostadas, CocinaCombieKids, QuesadillasTacos, KidsExtra, ConAlcohol, SinAlcohol;
    private String mesaSeleccionadaAnterior;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio_cliente, container, false);

        // Inicialización del spinner y Firebase Database
        spinner = view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.spinner_values, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Inicialización de la referencia a Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference().child("mesas");

        // Listener para el spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String mesaSeleccionada = parent.getItemAtPosition(position).toString();
                if (mesaSeleccionada != null && !mesaSeleccionada.equals(mesaSeleccionadaAnterior)) {
                    guardarNumeroDeMesaEnFirebase(mesaSeleccionada);
                    mesaSeleccionadaAnterior = mesaSeleccionada;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Manejar caso en que no se selecciona nada
            }
        });

        // Inicialización de botones
        CevichesTostadas = view.findViewById(R.id.CevichesTostadas);
        CocinaCombieKids = view.findViewById(R.id.CocinaCombieKids);
        QuesadillasTacos = view.findViewById(R.id.QuesadillasTacos);
        KidsExtra = view.findViewById(R.id.KidsExtra);
        ConAlcohol = view.findViewById(R.id.ConAlcohol);
        SinAlcohol = view.findViewById(R.id.SinAlcohol);

        // Configuración de listeners de botones
        configurarListenersDeBotones();

        return view;
    }

    private void guardarNumeroDeMesaEnFirebase(String numeroDeMesa) {
        // Usar updateChildren en lugar de setValue para no borrar datos existentes
        mDatabase.child(numeroDeMesa).updateChildren(new HashMap<String, Object>());
        //Toast.makeText(requireContext(), "Mesa " + numeroDeMesa + " seleccionada", Toast.LENGTH_SHORT).show();
    }

    private void configurarListenersDeBotones() {
        CevichesTostadas.setOnClickListener(v -> startActivity(new Intent(getActivity(), CevicheMesero.class)));
        CocinaCombieKids.setOnClickListener(v -> startActivity(new Intent(getActivity(), CocinaMesero.class)));
        QuesadillasTacos.setOnClickListener(v -> startActivity(new Intent(getActivity(), QuesadillaMesero.class)));
        KidsExtra.setOnClickListener(v -> startActivity(new Intent(getActivity(), KidsMesero.class)));
        ConAlcohol.setOnClickListener(v -> startActivity(new Intent(getActivity(), AlcoholMesero.class)));
        SinAlcohol.setOnClickListener(v -> startActivity(new Intent(getActivity(), SinAlcoholMesero.class)));
    }

}
