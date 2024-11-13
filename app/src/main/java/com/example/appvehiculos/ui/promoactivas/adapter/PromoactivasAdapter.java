package com.example.appvehiculos.ui.promoactivas.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.appvehiculos.R;
import com.example.appvehiculos.ui.promoactivas.Promocion;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.List;

public class PromoactivasAdapter extends RecyclerView.Adapter<PromoactivasAdapter.PromoViewHolder> {

    private List<Promocion> promocionesList;
    private Context context;

    public PromoactivasAdapter(Context context, List<Promocion> promocionesList) {
        this.context = context;
        this.promocionesList = promocionesList;
    }

    @NonNull
    @Override
    public PromoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_promocion, parent, false);
        return new PromoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromoViewHolder holder, int position) {
        Promocion promocion = promocionesList.get(position);

        // Configuración de texto y imagen para cada vista de elemento
        holder.tvTitulo.setText(promocion.getTitulo());
        holder.tvDescripcion.setText(promocion.getDescripcion());

        Glide.with(holder.itemView.getContext())
                .load(promocion.getImageUrl())
                .into(holder.ivImagen);

        // Manejo del evento de clic para mostrar un diálogo de confirmación antes de borrar
        holder.itemView.setOnLongClickListener(view -> {
            mostrarDialogoConfirmacion(promocion);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return promocionesList.size();
    }

    private void mostrarDialogoConfirmacion(Promocion promocion) {
        new AlertDialog.Builder(context)
                .setTitle("Eliminar Promoción")
                .setMessage("¿Estás seguro de que quieres eliminar esta promoción?")
                .setPositiveButton("Sí", (dialog, which) -> borrarPromocion(promocion))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void borrarPromocion(Promocion promocion) {
        // Eliminar el registro de Firestore
        FirebaseFirestore.getInstance().collection("promociones")
                .document(promocion.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Eliminar la imagen del almacenamiento
                    StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(promocion.getImageUrl());
                    imageRef.delete().addOnSuccessListener(aVoid1 -> {
                        Toast.makeText(context, "Promoción eliminada", Toast.LENGTH_SHORT).show();
                        // Actualizar la lista y notificar el adaptador
                        int position = promocionesList.indexOf(promocion);
                        if (position >= 0) {
                            promocionesList.remove(position);
                            notifyItemRemoved(position);
                        }
                    }).addOnFailureListener(e ->
                            Toast.makeText(context, "Error al eliminar la imagen", Toast.LENGTH_SHORT).show()
                    );
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Error al eliminar la promoción", Toast.LENGTH_SHORT).show()
                );
    }

    public static class PromoViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDescripcion;
        ImageView ivImagen;

        public PromoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImagen = itemView.findViewById(R.id.ivImagen);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
        }
    }
}