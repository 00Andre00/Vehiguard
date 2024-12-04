package com.example.appvehiculos.ui.gallery.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appvehiculos.R;

import java.util.Map;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GastoViewHolder> {

    private Map<String, Map<String, Double>> gastos; // Estructura que contiene todos los gastos
    private String categoria; // La categoría de gasto que estamos viendo
    private OnGastoClickListener listener;

    // Interfaz para manejar el click en el botón de eliminar
    public interface OnGastoClickListener {
        void onGastoClick(String categoria, String gastoId, double monto);
    }

    // Constructor del Adapter
    public GalleryAdapter(Map<String, Map<String, Double>> gastos, String categoria, OnGastoClickListener listener) {
        this.gastos = gastos;
        this.categoria = categoria;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GastoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el layout del item del gasto
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gasto, parent, false);
        return new GastoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GastoViewHolder holder, int position) {
        // Obtenemos el id del gasto y el monto
        String gastoId = (String) gastos.get(categoria).keySet().toArray()[position];
        Double monto = gastos.get(categoria).get(gastoId);

        // Asignamos los valores al ViewHolder
        holder.textGasto.setText("Gasto: " + monto);
        holder.textFecha.setText(gastoId); // Usamos el ID como una referencia de fecha o ID de documento

        // Establecer el listener para eliminar el gasto
        holder.btnEliminar.setOnClickListener(v -> listener.onGastoClick(categoria, gastoId, monto));
    }

    @Override
    public int getItemCount() {
        return gastos.get(categoria).size();
    }

    // ViewHolder para cada item de gasto
    public static class GastoViewHolder extends RecyclerView.ViewHolder {
        TextView textGasto, textFecha;
        Button btnEliminar;

        public GastoViewHolder(View itemView) {
            super(itemView);
            textGasto = itemView.findViewById(R.id.textGasto);
            textFecha = itemView.findViewById(R.id.textFecha);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}
