package com.example.cf.CategoriasAdmin.AlcoholA;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cf.R;
import com.squareup.picasso.Picasso;

public class ViewHolderAlcohol extends RecyclerView.ViewHolder {

    View mView;

    public ViewHolderAlcohol.ClickListener mClickListener;

    public interface ClickListener {
        void onItemClick(View view, int position); /*ADMIN PRESIONA NORMAL EL ITEM*/
        void OnItemLongClick(View view, int position); /*ADMIN MANTIENE PRECIONADO EL ITEM MÁS TIEMPO*/
    }

    //METODO PARA PODER PRESIONAR O MANTENER PRESIONADO UN ITEM
    public void setOnClickListener(ViewHolderAlcohol.ClickListener clickListener) {
        mClickListener = clickListener;
    }

    public ViewHolderAlcohol(@NonNull View itemView) {
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

    public void SeteoAlcohol(Context context, String nombre, int precio, String imagen) {
        ImageView ImagenAlcohol;
        TextView NombreImagenAlcohol;
        TextView PrecioAlcohol;

        ImagenAlcohol = mView.findViewById(R.id.ImagenAlcohol);
        NombreImagenAlcohol = mView.findViewById(R.id.NombreImagenAlcohol);
        PrecioAlcohol = mView.findViewById(R.id.PrecioAlcohol);

        NombreImagenAlcohol.setText(nombre);

        String PrecioString = String.valueOf(precio);
        PrecioAlcohol.setText(PrecioString);

        try {
            Picasso.get().load(imagen).into(ImagenAlcohol);
        } catch (Exception e) {
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
