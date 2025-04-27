package com.example.cf.CategoriasAdmin.CevichesA;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cf.R;
import com.squareup.picasso.Picasso;

public class ViewHolderCeviche extends RecyclerView.ViewHolder {

    View mView;

    public ViewHolderCeviche.ClickListener mClickListener;

    public interface ClickListener{
        void onItemClick(View view, int position); /*ADMIN PRESIONA NORMAL EL ITEM*/
        void OnItemLongClick(View view, int position); /*ADMIN MANTIENE PRECIONADO EL ITEM MÁS TIEMPO*/
    }
        //METODO PARA PODER PRESIONAR O MANTENER PRESIONADO UN ITEM
    public void setOnClickListener(ViewHolderCeviche.ClickListener clickListener){
        mClickListener = clickListener;
    }

    public ViewHolderCeviche(@NonNull View itemView) {
        super(itemView);
        mView = itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(view, getBindingAdapterPosition());
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClickListener.OnItemLongClick(view, getBindingAdapterPosition());
                return true;
            }
        });
    }

    public void SeteoCeviche(Context context, String nombre, int precio, String imagen){
        ImageView ImagenCeviche;
        TextView NombreImagenCeciche;
        TextView PrecioPelicula;

        //CONEXION CON EL ITEM
        // SI, ASÍ ES ESTOY GRITANDO ORLANDOOOOO
        ImagenCeviche = mView.findViewById(R.id.ImagenCeviche);
        NombreImagenCeciche = mView.findViewById(R.id.NombreImagenCeciche);
        PrecioPelicula = mView.findViewById(R.id.PrecioPelicula);


        NombreImagenCeciche.setText(nombre);

        //CONVERTIR A STRING EL PARAMETRO VISTA
        String PrecioString = String.valueOf(precio);
        PrecioPelicula.setText(PrecioString);


        //POR SI ALGO SALE MAL CON LA LIBRERIA PICASSO AL TARER LAS IMAGENES DE LA BASE DE DATOS (FIREBASE) :(
        try{
            Picasso.get().load(imagen).into(ImagenCeviche);
        }catch (Exception e){
            Toast.makeText(context, "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }


}
