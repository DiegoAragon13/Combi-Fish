package com.example.cf.CategoriasMesero;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cf.BluetoothPrint;
import com.example.cf.Modelo.Platillo;
import com.example.cf.Modelo.PlatilloAdapter;
import com.example.cf.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Ticket extends AppCompatActivity {
    private RecyclerView OrdenCuenta;
    private Spinner spinner;
    private PlatilloAdapter platilloAdapter;
    private DatabaseReference databaseReference;

    private Button Imprimir, Limpiar;
    private BluetoothPrint bluetoothPrint;
    private EditText nombreMesero;
    private TextView TotalCuenta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        Imprimir = findViewById(R.id.Imprimir);
        Limpiar = findViewById(R.id.Limpiar); // Nuevo botón de limpiar
        bluetoothPrint = new BluetoothPrint(this);

        OrdenCuenta = findViewById(R.id.OrdenCuenta);
        spinner = findViewById(R.id.spinner);
        nombreMesero = findViewById(R.id.nombreMesero);
        TotalCuenta = findViewById(R.id.TotalCuenta);

        // Configuración del LayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
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

        // Manejar la impresión del ticket al hacer clic en el botón Imprimir
        Imprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verificar que el nombre del mesero no esté vacío
                String mesero = nombreMesero.getText().toString().trim();
                if (mesero.isEmpty()) {
                    Toast.makeText(Ticket.this, "Por favor, ingrese el nombre del mesero", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Abrir la conexión Bluetooth
                bluetoothPrint.openBluetoothPrinterWithPermissionCheck();

                // Obtener la lista de platillos del adaptador
                List<Platillo> platillos = platilloAdapter.getPlatillos();

                // Generar el contenido del ticket
                String ticketContent = generatePrintContent(platillos, mesero);

                // Imprimir el contenido del ticket con tamaño de fuente pequeño
                bluetoothPrint.printData(ticketContent, true);

                // Actualizar el TextView con el total de la cuenta
                int total = calcularTotal(platillos);
                TotalCuenta.setText("Total: $" + total);
            }
        });

        // Manejar la limpieza de la base de datos y el envío a "economia" al hacer clic en el botón Limpiar
        Limpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verificar que el nombre del mesero no esté vacío
                String mesero = nombreMesero.getText().toString().trim();
                if (mesero.isEmpty()) {
                    Toast.makeText(Ticket.this, "Por favor, ingrese el nombre del mesero", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Obtener la mesa seleccionada
                String mesaSeleccionada = spinner.getSelectedItem().toString();

                // Obtener la lista de platillos del adaptador
                List<Platillo> platillos = platilloAdapter.getPlatillos();

                // Guardar en la base de datos "economia"
                guardarEnEconomia(mesero, mesaSeleccionada, platillos);

                // Limpiar los datos de la mesa seleccionada en la base de datos
                limpiarDatosMesa(mesaSeleccionada);

                // Actualizar el RecyclerView y el Spinner
                platilloAdapter.setPlatillos(new ArrayList<>());
                TotalCuenta.setText("Total: $0");
                cargarOpcionesSpinner();
            }
        });
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
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(Ticket.this, android.R.layout.simple_spinner_item, mesas);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(spinnerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Ticket.this, "Error al cargar las mesas: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

                // Actualizar el TextView con el total de la cuenta
                int total = calcularTotal(platillos);
                TotalCuenta.setText("Total: $" + total);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Ticket.this, "Error al cargar los platillos de la mesa: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para guardar en la base de datos "economia"
    private void guardarEnEconomia(String mesero, String mesa, List<Platillo> platillos) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String fechaActual = dateFormat.format(new Date());

        DatabaseReference economiaRef = FirebaseDatabase.getInstance().getReference().child("economia").child(fechaActual);

        for (Platillo platillo : platillos) {
            economiaRef.push().setValue(platillo);
        }

        economiaRef.child("mesero").setValue(mesero);
        economiaRef.child("mesa").setValue(mesa);
        economiaRef.child("total").setValue(calcularTotal(platillos));
    }

    // Método para limpiar los datos de la mesa seleccionada en la base de datos
    private void limpiarDatosMesa(String mesaSeleccionada) {
        databaseReference.child(mesaSeleccionada).removeValue();
    }

    // Método para generar el contenido del ticket
    private String generatePrintContent(List<Platillo> platillos, String mesero) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String horaActual = dateFormat.format(new Date());

        StringBuilder contentBuilder = new StringBuilder();
        String espacioCentrado = "       ";

        // Comenzar a construir el contenido del ticket
        contentBuilder.append(espacioCentrado).append("COMBI AND FISH").append("\n\n\n");
        contentBuilder.append("").append(horaActual).append("\n\n");
        contentBuilder.append("Mesero en turno: ").append(mesero).append("\n\n");
        contentBuilder.append("===============================").append("\n");
        contentBuilder.append("Hacienda de lajas #100, Hacienda del saltito").append("\n");
        contentBuilder.append("Tel: 618 319 1428").append("\n");
        contentBuilder.append("E-mail: combiyfish@gmail.com").append("\n");
        contentBuilder.append("===============================").append("\n\n\n");

        // Agregar cada platillo al contenido del ticket
        for (Platillo platillo : platillos) {
            contentBuilder.append(platillo.getNombre()).append(": $").append(platillo.getPrecio()).append("\n");
        }

        // Finalizar el contenido del ticket
        contentBuilder.append("-------------------------------").append("\n");
        contentBuilder.append("Total: $").append(calcularTotal(platillos)).append("\n");
        contentBuilder.append("-------------------------------").append("\n\n\n");

        return contentBuilder.toString();
    }

    // Método para calcular el total de la cuenta
    private int calcularTotal(List<Platillo> platillos) {
        int total = 0;
        for (Platillo platillo : platillos) {
            total += platillo.getPrecio();
        }
        return total;
    }
}

