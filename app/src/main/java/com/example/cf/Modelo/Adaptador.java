package com.example.cf.Modelo;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cf.Modelo.Platillo;
import com.example.cf.R;

import java.util.List;

public class Adaptador extends RecyclerView.Adapter<Adaptador.PlatilloViewHolder> {

    private Context context;
    private List<Platillo> platilloList;

    public Adaptador(Context context, List<Platillo> platilloList) {
        this.context = context;
        this.platilloList = platilloList;
    }

    @NonNull
    @Override
    public PlatilloViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ticket_item, parent, false);
        return new PlatilloViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlatilloViewHolder holder, int position) {
        Platillo platillo = platilloList.get(position);
        holder.nombrePlatillo.setText(platillo.getNombre());
        holder.precioPlatillo.setText(String.valueOf(platillo.getPrecio()));
    }

    @Override
    public int getItemCount() {
        return platilloList.size();
    }

    public static class PlatilloViewHolder extends RecyclerView.ViewHolder {

        TextView nombrePlatillo, precioPlatillo;

        public PlatilloViewHolder(@NonNull View itemView) {
            super(itemView);
            nombrePlatillo = itemView.findViewById(R.id.NombrePlatillo);
            precioPlatillo = itemView.findViewById(R.id.PrecioPlatillo);
        }
    }
}
