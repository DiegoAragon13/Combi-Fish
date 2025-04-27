package com.example.cf.CategoriasAdmin.QuesadillasA;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cf.R;
import com.squareup.picasso.Picasso;

public class ViewHolderQuesadillas extends RecyclerView.ViewHolder {

    View mView;
    private ClickListener mClickListener;

    public interface ClickListener {
        void onItemClick(View view, int position); // Cuando se presiona normalmente el elemento
        void OnItemLongClick(View view, int position); // Cuando se mantiene presionado el elemento por más tiempo
    }

    // Método para establecer el listener
    public void setOnClickListener(ClickListener clickListener) {
        mClickListener = clickListener;
    }

    public ViewHolderQuesadillas(@NonNull View itemView) {
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

    public void SeteoQuesadillas(Context context, String nombre, int precio, String imagen) {
        ImageView ImagenQuesadilla;
        TextView NombreImagenQuesadilla;
        TextView PrecioQuesadilla;

        // Conexión con los elementos de la vista
        ImagenQuesadilla = mView.findViewById(R.id.ImagenQuesadilla);
        NombreImagenQuesadilla = mView.findViewById(R.id.NombreImagenQuesadilla);
        PrecioQuesadilla = mView.findViewById(R.id.PrecioQuesadilla);

        NombreImagenQuesadilla.setText(nombre);

        // Convertir el precio a string
        String PrecioString = String.valueOf(precio);
        PrecioQuesadilla.setText(PrecioString);

        // Cargar la imagen con Picasso
        try {
            Picasso.get().load(imagen).into(ImagenQuesadilla);
        } catch (Exception e) {
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
