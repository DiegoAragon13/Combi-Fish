package com.example.cf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Carga extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carga);


        final int Duracion = 3000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //CODIGO QUE SE EJECUTARA
                Intent intent = new Intent(Carga.this, MainActivityAdministrador.class);
                startActivity(intent);
                finish();
            }
        },Duracion);
    }
}