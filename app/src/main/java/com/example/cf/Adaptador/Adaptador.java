package com.example.cf.Adaptador;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cf.Detalle.Detalle_Administrador;
import com.example.cf.Modelo.Administrador;
import com.example.cf.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adaptador extends RecyclerView.Adapter<Adaptador.Myholder> {

    private Context context;
    private List<Administrador> Administradores;

    public Adaptador(Context context, List<Administrador> administradores) {
        this.context = context;
        Administradores = administradores;
    }

    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //INFLAR EL ADMIN_LAYOUD

        View view = LayoutInflater.from(context).inflate(R.layout.admin_item,parent,false);
        return new Myholder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull Myholder holder, int position) {

        //OBTENEMOS LOS DATOS DEL MODELO
        String UID = Administradores.get(position).getUID();
        String Imagen = Administradores.get(position).getImagen();
        String NOMBRES = Administradores.get(position).getNOMBRES();
        String APELLIDOS = Administradores.get(position).getAPELLIDOS();
        String CORREO = Administradores.get(position).getCORREO();
        int EDAD = Administradores.get(position).getEDAD();
        String EdadString = String.valueOf(EDAD);


        //SETEO DE DATOS

        holder.NombresADMIN.setText(NOMBRES);
        holder.CorreoADMIN.setText(CORREO);

        try {
            //SI EXISTE LA IMAGE
            Picasso.get().load(Imagen).placeholder(R.drawable.admin_item).into(holder.PerfilAdmin);
        }catch (Exception e){
            //SI NO EXISTE LA IMAGEN
            Picasso.get().load(R.drawable.admin_item).into(holder.PerfilAdmin);
        }


        //AL HACER CLICK EN UN ADMIN
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, Detalle_Administrador.class);
                    //Pasar datos a la siguiente actividad
                intent.putExtra("UID",UID);
                intent.putExtra("NOMBRES", NOMBRES);
                intent.putExtra("APELLIDOS",APELLIDOS);
                intent.putExtra("CORREO",CORREO);
                intent.putExtra("EDAD",EdadString);
                intent.putExtra("Imagen",Imagen);
                context.startActivity(intent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return Administradores.size();

    }

    public class Myholder extends RecyclerView.ViewHolder{


        //DECLARAMOS LAS VISTAS
        CircleImageView PerfilAdmin;
        TextView NombresADMIN,CorreoADMIN;


        public Myholder(@NonNull View itemView) {
            super(itemView);

            PerfilAdmin = itemView.findViewById(R.id.PerfilAdmin);
            NombresADMIN = itemView.findViewById(R.id.NombresADMIN);
            CorreoADMIN = itemView.findViewById(R.id.CorreoADMIN);
        }
    }
}
