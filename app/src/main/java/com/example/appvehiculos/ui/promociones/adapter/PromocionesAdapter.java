package com.example.appvehiculos.ui.promociones.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appvehiculos.R;
import com.example.appvehiculos.ui.promociones.Promocion;

import java.util.List;

public class PromocionesAdapter extends RecyclerView.Adapter<PromocionesAdapter.PromocionViewHolder> {
    private List<Promocion> promocionesList;

    public PromocionesAdapter(List<Promocion> promocionesList) {
        this.promocionesList = promocionesList;
    }

    @NonNull
    @Override
    public PromocionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_promocion, parent, false);
        return new PromocionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromocionViewHolder holder, int position) {
        Promocion promocion = promocionesList.get(position);
        holder.titulo.setText(promocion.getTitulo());
        holder.descripcion.setText(promocion.getDescripcion());
        holder.ubicacion.setText(promocion.getUbicacion());

        Glide.with(holder.itemView.getContext())
                .load(promocion.getImageUrl())
                .placeholder(R.drawable.default_image) // Asegúrate de tener una imagen por defecto en tus recursos
                .into(holder.imagen);
    }

    @Override
    public int getItemCount() {
        return promocionesList.size();
    }

    public static class PromocionViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, descripcion, ubicacion;
        ImageView imagen;

        public PromocionViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.tvTitulo);
            descripcion = itemView.findViewById(R.id.tvDescripcion);
            ubicacion = itemView.findViewById(R.id.tvUbicacion);
            imagen = itemView.findViewById(R.id.ivImagen);
        }
    }
}
