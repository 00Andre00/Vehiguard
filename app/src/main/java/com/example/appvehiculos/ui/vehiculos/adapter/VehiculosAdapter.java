package com.example.appvehiculos.ui.vehiculos.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appvehiculos.R;
import com.example.appvehiculos.ui.vehiculos.Vehiculo;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class VehiculosAdapter extends RecyclerView.Adapter<VehiculosAdapter.VehiculoViewHolder> {
    private List<Vehiculo> vehiculosList;

    public VehiculosAdapter(List<Vehiculo> vehiculosList) {
        this.vehiculosList = vehiculosList;
    }

    @NonNull
    @Override
    public VehiculoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vehiculo, parent, false);
        return new VehiculoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehiculoViewHolder holder, int position) {
        Vehiculo vehiculo = vehiculosList.get(position);

        // Mostrar las etiquetas descriptivas junto con los valores
        holder.marca.setText("Marca: " + vehiculo.getMarca());
        holder.modelo.setText("Modelo: " + vehiculo.getModelo());
        holder.año.setText("Año: " + vehiculo.getAño());
        holder.kilometraje.setText("Kilometraje: " + vehiculo.getKilometraje());

        Glide.with(holder.itemView.getContext())
                .load(vehiculo.getImageUrl())
                .placeholder(R.drawable.default_image)
                .into(holder.imagen);

        // Botón para actualizar kilometraje
        holder.actualizarKilometrajeBtn.setOnClickListener(v -> mostrarDialogoActualizarKilometraje(vehiculo, holder));
    }

    @Override
    public int getItemCount() {
        return vehiculosList.size();
    }

    private void mostrarDialogoActualizarKilometraje(Vehiculo vehiculo, VehiculoViewHolder holder) {
        // Crear un nuevo AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
        builder.setTitle("Actualizar Kilometraje");

        // Configurar el layout del cuadro de diálogo
        View dialogLayout = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.dialog_actualizar_kilometraje, null);
        final EditText editTextKilometraje = dialogLayout.findViewById(R.id.editTextKilometraje);
        builder.setView(dialogLayout);

        // Configurar los botones del cuadro de diálogo
        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nuevoKilometrajeStr = editTextKilometraje.getText().toString().trim();
            if (!nuevoKilometrajeStr.isEmpty()) {
                try {
                    int nuevoKilometraje = Integer.parseInt(nuevoKilometrajeStr);
                    actualizarKilometrajeEnFirestore(vehiculo.getVehicleId(), nuevoKilometraje, holder);
                } catch (NumberFormatException e) {
                    Toast.makeText(holder.itemView.getContext(), "El kilometraje debe ser un número válido", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(holder.itemView.getContext(), "Por favor, ingrese un kilometraje", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", null);

        // Mostrar el cuadro de diálogo
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void actualizarKilometrajeEnFirestore(String vehicleId, int nuevoKilometraje, VehiculoViewHolder holder) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("vehiculos").document(vehicleId)
                .update("kilometraje", nuevoKilometraje)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(holder.itemView.getContext(), "Kilometraje actualizado", Toast.LENGTH_SHORT).show();
                    // Actualiza el vehículo en la lista
                    actualizarKilometrajeEnLista(vehicleId, nuevoKilometraje);
                })
                .addOnFailureListener(e -> Toast.makeText(holder.itemView.getContext(), "Error al actualizar el kilometraje", Toast.LENGTH_SHORT).show());
    }


    public void actualizarKilometrajeEnLista(String vehicleId, int nuevoKilometraje) {
        for (int i = 0; i < vehiculosList.size(); i++) {
            if (vehiculosList.get(i).getVehicleId().equals(vehicleId)) {
                vehiculosList.get(i).setKilometraje(nuevoKilometraje);
                notifyItemChanged(i); // Notifica solo el ítem que ha cambiado
                break;
            }
        }
    }

    public static class VehiculoViewHolder extends RecyclerView.ViewHolder {
        TextView marca, modelo, año, kilometraje;
        ImageView imagen;
        Button actualizarKilometrajeBtn;

        public VehiculoViewHolder(@NonNull View itemView) {
            super(itemView);
            marca = itemView.findViewById(R.id.tvMarca);
            modelo = itemView.findViewById(R.id.tvModelo);
            año = itemView.findViewById(R.id.tvAño);
            kilometraje = itemView.findViewById(R.id.tvKilometraje);
            imagen = itemView.findViewById(R.id.ivImagen);
            actualizarKilometrajeBtn = itemView.findViewById(R.id.btnActualizarKilometraje);  // Asegúrate de tener este botón en tu XML
        }
    }
}
