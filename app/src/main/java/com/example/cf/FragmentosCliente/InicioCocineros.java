package com.example.cf.FragmentosCliente;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cf.Modelo.Platillo;
import com.example.cf.Modelo.PlatilloAdapter;
import com.example.cf.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class InicioCocineros extends Fragment {

    private Button Notificar;
    private Spinner spinner;
    private RecyclerView OrdenCuenta;
    private PlatilloAdapter platilloAdapter;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio_cocinero, container, false);

        spinner = view.findViewById(R.id.spinner);
        OrdenCuenta = view.findViewById(R.id.OrdenCuenta);
        Notificar = view.findViewById(R.id.Notificar);

        // Configuración del LayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        OrdenCuenta.setLayoutManager(layoutManager);

        // Inicialización del adaptador
        platilloAdapter = new PlatilloAdapter(new ArrayList<>());
        OrdenCuenta.setAdapter(platilloAdapter);

        // Inicialización de la base de datos
        databaseReference = FirebaseDatabase.getInstance().getReference().child("mesas");

        // Cargar las opciones del spinner desde la base de datos
        cargarOpcionesSpinner();

        // Manejar la selección del spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String mesaSeleccionada = parent.getItemAtPosition(position).toString();
                cargarPlatillos(mesaSeleccionada);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No se hace nada si no se selecciona nada en el spinner
            }
        });

        // Manejar la acción del botón Notificar
        Notificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mesaSeleccionada = spinner.getSelectedItem().toString();
                sendNotification(mesaSeleccionada);
            }
        });

        return view;
    }

    // Método para cargar las opciones del spinner desde la base de datos
    private void cargarOpcionesSpinner() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> mesas = new ArrayList<>();
                for (DataSnapshot mesaSnapshot : snapshot.getChildren()) {
                    mesas.add(mesaSnapshot.getKey());
                }
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, mesas);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(spinnerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al cargar las mesas: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para cargar los platillos de la mesa seleccionada en el RecyclerView
    private void cargarPlatillos(String mesaSeleccionada) {
        databaseReference.child(mesaSeleccionada).child("platillos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Platillo> platillos = new ArrayList<>();
                for (DataSnapshot platilloSnapshot : snapshot.getChildren()) {
                    Platillo platillo = platilloSnapshot.getValue(Platillo.class);
                    platillos.add(platillo);
                }
                platilloAdapter.setPlatillos(platillos);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al cargar los platillos de la mesa: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para enviar una notificación
    private void sendNotification(String mesaSeleccionada) {
        final String CHANNEL_ID = "channel_id";
        final String CHANNEL_NAME = "Platillo completado";
        final String CHANNEL_DESC = "Notificaciones de platillos completados";

        // Crear el canal de notificación en dispositivos con Android O y superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon_foreground) // Reemplaza con tu propio ícono
                .setContentTitle("Platillo completado")
                .setContentText("El platillo de la mesa " + mesaSeleccionada + " ha sido completado.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }
}



