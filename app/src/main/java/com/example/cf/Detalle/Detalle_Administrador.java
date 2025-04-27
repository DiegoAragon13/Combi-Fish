package com.example.cf.Detalle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.cf.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Detalle_Administrador extends AppCompatActivity {

    CircleImageView ImagenDetalleAdmin;
    TextView UidDetalleAdmin,NombreDetalleAdmin,ApellidoDetalleAdmin,CorreoDetalleAdmin,EdadDetalleAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_administrador);


        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Detalle");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        ImagenDetalleAdmin = findViewById(R.id.ImagenDetalleAdmin);
        UidDetalleAdmin = findViewById(R.id.UidDetalleAdmin);
        NombreDetalleAdmin = findViewById(R.id.NombreDetalleAdmin);
        ApellidoDetalleAdmin = findViewById(R.id.ApellidoDetalleAdmin);
        CorreoDetalleAdmin = findViewById(R.id.CorreoDetalleAdmin);
        EdadDetalleAdmin = findViewById(R.id.EdadDetalleAdmin);

        String UidDetalle = getIntent().getStringExtra("UID");
        String NombreDetalle = getIntent().getStringExtra("NOMBRES");
        String ApellidoDetalle = getIntent().getStringExtra("APELLIDOS");
        String CorreoDetalle = getIntent().getStringExtra("CORREO");
        String EdadDetalle = getIntent().getStringExtra("EDAD");
        String ImagenDetalle = getIntent().getStringExtra("Imagen");

        UidDetalleAdmin.setText("UID = " + UidDetalle);
        NombreDetalleAdmin.setText("NOMBRES = " + NombreDetalle);
        ApellidoDetalleAdmin.setText("APELLIDOS = " +ApellidoDetalle);
        CorreoDetalleAdmin.setText("CORREO = " + CorreoDetalle);
        EdadDetalleAdmin.setText("EDAD = " + EdadDetalle);


            try{
                Picasso.get().load(ImagenDetalle).placeholder(R.drawable.admin_item).into(ImagenDetalleAdmin);
            }catch (Exception e){
                Picasso.get().load(R.drawable.perfil_item).into(ImagenDetalleAdmin);
            }



    }

    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }
}