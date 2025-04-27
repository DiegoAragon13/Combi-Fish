package com.example.cf.FragmentosAdministrador;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.cf.CategoriasAdmin.AlcoholA.AlcoholA;
import com.example.cf.CategoriasAdmin.CevichesA.CevicheA;
import com.example.cf.CategoriasAdmin.CocinaA.CocinaAdmin;
import com.example.cf.CategoriasAdmin.KidsA.KidsA;
import com.example.cf.CategoriasAdmin.QuesadillasA.QuesadillasA;
import com.example.cf.CategoriasAdmin.SinAlcocholA.SinAlcoholA;
import com.example.cf.R;


public class InicioAdmin extends Fragment {

    Button CevichesTostadas,CocinaCombieKids,QuesadillasTacos,KidsExtra,ConAlcohol,SinAlcohol;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inicio_admin, container, false);



        CevichesTostadas = view.findViewById(R.id.CevichesTostadas);
        CocinaCombieKids = view.findViewById(R.id.CocinaCombieKids);
        QuesadillasTacos = view.findViewById(R.id.QuesadillasTacos);
        KidsExtra = view.findViewById(R.id.KidsExtra);
        ConAlcohol = view.findViewById(R.id.ConAlcohol);
        SinAlcohol = view.findViewById(R.id.SinAlcohol);


        CevichesTostadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    startActivity(new Intent(getActivity(), CevicheA.class));
            }
        });

        CocinaCombieKids.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CocinaAdmin.class));
            }
        });
        QuesadillasTacos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), QuesadillasA.class));
            }
        });
        KidsExtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), KidsA.class));
            }
        });
        ConAlcohol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AlcoholA.class));
            }
        });
        SinAlcohol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SinAlcoholA.class));
            }
        });

        return  view;
    }
}