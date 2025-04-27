package com.example.cf.CategoriasAdmin.KidsA;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cf.R;
import com.squareup.picasso.Picasso;

public class ViewHolderKids extends RecyclerView.ViewHolder {

    View mView;
    private ClickListener mClickListener;

    public interface ClickListener {
        void onItemClick(View view, int position); // ADMIN PRESIONA NORMAL EL ITEM
        void OnItemLongClick(View view, int position); // ADMIN MANTIENE PRESIONADO EL ITEM MÁS TIEMPO
    }

    public void setOnClickListener(ClickListener clickListener) {
        mClickListener = clickListener;
    }

    public ViewHolderKids(@NonNull View itemView) {
        super(itemView);
        mView = itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) {
                    mClickListener.onItemClick(view, getBindingAdapterPosition());
                }
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mClickListener != null) {
                    mClickListener.OnItemLongClick(view, getBindingAdapterPosition());
                }
                return true;
            }
        });
    }

    public void SeteoKids(Context context, String nombre, int precio, String imagen) {
        ImageView ImagenKids = mView.findViewById(R.id.ImagenKids);
        TextView NombreImagenKids = mView.findViewById(R.id.NombreImagenKids);
        TextView PrecioKids = mView.findViewById(R.id.PrecioKids);

        NombreImagenKids.setText(nombre);
        PrecioKids.setText(String.valueOf(precio));

        try {
            Picasso.get().load(imagen).into(ImagenKids);
        } catch (Exception e) {
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
