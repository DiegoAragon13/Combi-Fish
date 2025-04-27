package com.example.cf.Modelo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cf.R;

import java.util.List;

public class PlatilloAdapter extends RecyclerView.Adapter<PlatilloAdapter.PlatilloViewHolder> {

    private List<Platillo> platillos;

    public PlatilloAdapter(List<Platillo> platillos) {
        this.platillos = platillos;
    }

    @NonNull
    @Override
    public PlatilloViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.platillo_item, parent, false);
        return new PlatilloViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlatilloViewHolder holder, int position) {
        Platillo platillo = platillos.get(position);
        holder.bind(platillo);
    }

    @Override
    public int getItemCount() {
        return platillos.size();
    }

    public void setPlatillos(List<Platillo> platillos) {
        this.platillos = platillos;
        notifyDataSetChanged();
    }

    public List<Platillo> getPlatillos() {
        return platillos;
    }

    public static class PlatilloViewHolder extends RecyclerView.ViewHolder {
        private TextView nombreTextView;
        private TextView precioTextView;

        public PlatilloViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.NombrePlatillo);
            precioTextView = itemView.findViewById(R.id.PrecioPlatillo);
        }

        public void bind(Platillo platillo) {
            nombreTextView.setText(platillo.getNombre());
            precioTextView.setText(String.valueOf(platillo.getPrecio()));
        }
    }
}
