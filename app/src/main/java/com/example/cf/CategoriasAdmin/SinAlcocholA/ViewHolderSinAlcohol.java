package com.example.cf.CategoriasAdmin.SinAlcocholA;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cf.R;
import com.squareup.picasso.Picasso;

public class ViewHolderSinAlcohol extends RecyclerView.ViewHolder {

    View mView;

    public ViewHolderSinAlcohol.ClickListener mClickListener;

    public interface ClickListener {
        void onItemClick(View view, int position); /* ADMIN PRESIONA NORMAL EL ITEM */
        void OnItemLongClick(View view, int position); /* ADMIN MANTIENE PRESIONADO EL ITEM MÁS TIEMPO */
    }

    // METODO PARA PODER PRESIONAR O MANTENER PRESIONADO UN ITEM
    public void setOnClickListener(ViewHolderSinAlcohol.ClickListener clickListener) {
        mClickListener = clickListener;
    }

    public ViewHolderSinAlcohol(@NonNull View itemView) {
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

    public void SeteoSinAlcohol(Context context, String nombre, int precio, String imagen) {
        ImageView ImagenSinAlcohol;
        TextView NombreImagenSinAlcohol;
        TextView PrecioSinAlcohol;

        // CONEXION CON EL ITEM
        ImagenSinAlcohol = mView.findViewById(R.id.ImagenSinAlcohol);
        NombreImagenSinAlcohol = mView.findViewById(R.id.NombreImagenSinAlcohol);
        PrecioSinAlcohol = mView.findViewById(R.id.PrecioSinAlcohol);

        NombreImagenSinAlcohol.setText(nombre);

        // CONVERTIR A STRING EL PRECIO
        String precioString = String.valueOf(precio);
        PrecioSinAlcohol.setText(precioString);

        // CARGAR IMAGEN CON PICASSO
        try {
            Picasso.get().load(imagen).into(ImagenSinAlcohol);
        } catch (Exception e) {
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
