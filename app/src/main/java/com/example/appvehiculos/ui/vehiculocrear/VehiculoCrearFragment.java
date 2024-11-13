package com.example.appvehiculos.ui.vehiculocrear;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appvehiculos.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VehiculoCrearFragment extends Fragment {

    private EditText editTextAño, editTextPlaca, editTextKilometraje;
    private Spinner spinnerMarca, spinnerModelo;
    private Button buttonGuardar;

    private FirebaseFirestore db;

    private List<String> marcasList;
    private ArrayAdapter<String> marcaAdapter;

    private List<String> modelosList;
    private ArrayAdapter<String> modeloAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_vehiculocrear, container, false);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Inicializar vistas
        editTextAño = root.findViewById(R.id.editTextAño);
        editTextPlaca = root.findViewById(R.id.editTextPlaca);
        editTextKilometraje = root.findViewById(R.id.editTextKilometraje);
        spinnerMarca = root.findViewById(R.id.spinnerMarca);
        spinnerModelo = root.findViewById(R.id.spinnerModelo);
        buttonGuardar = root.findViewById(R.id.buttonGuardar);

        // Inicializar listas y adaptadores
        marcasList = new ArrayList<>();
        modelosList = new ArrayList<>();
        marcaAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, marcasList);
        marcaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMarca.setAdapter(marcaAdapter);

        modeloAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, modelosList);
        modeloAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModelo.setAdapter(modeloAdapter);

        // Cargar las marcas desde Firestore
        cargarMarcasDesdeFirestore();

        // Acción del botón guardar
        buttonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarVehiculo();
            }
        });

        // Acción al seleccionar una marca
        spinnerMarca.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String marcaSeleccionada = (String) parent.getItemAtPosition(position);
                cargarModelosPorMarca(marcaSeleccionada);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada si no se selecciona nada
            }
        });

        return root;
    }

    private void cargarMarcasDesdeFirestore() {
        db.collection("presets")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    marcasList.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String marca = documentSnapshot.getString("marca");
                        if (marca != null && !marca.isEmpty() && !marcasList.contains(marca)) {
                            marcasList.add(marca);
                        }
                    }
                    marcaAdapter.notifyDataSetChanged();

                    if (marcasList.isEmpty()) {
                        Toast.makeText(requireContext(), "No se encontraron marcas", Toast.LENGTH_SHORT).show();
                    } else {
                        // Seleccionar la primera marca por defecto
                        spinnerMarca.setSelection(0);
                        // Cargar los modelos para la primera marca
                        cargarModelosPorMarca(marcasList.get(0));
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error al cargar las marcas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void cargarModelosPorMarca(String marca) {
        db.collection("presets")
                .whereEqualTo("marca", marca)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    modelosList.clear();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        String modelo = documentSnapshot.getString("modelo");
                        if (modelo != null && !modelo.isEmpty() && !modelosList.contains(modelo)) {
                            modelosList.add(modelo);
                        }
                    }
                    modeloAdapter.notifyDataSetChanged();

                    if (modelosList.isEmpty()) {
                        Toast.makeText(requireContext(), "No se encontraron modelos para esta marca", Toast.LENGTH_SHORT).show();
                    } else {
                        // Seleccionar el primer modelo por defecto
                        spinnerModelo.setSelection(0);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error al cargar los modelos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void guardarVehiculo() {
        String marca = spinnerMarca.getSelectedItem().toString();
        String modelo = spinnerModelo.getSelectedItem().toString();
        String año = editTextAño.getText().toString().trim();
        String placa = editTextPlaca.getText().toString().trim();
        String kilometrajeStr = editTextKilometraje.getText().toString().trim();

        // Convertir kilometraje a entero
        int kilometraje;
        try {
            kilometraje = Integer.parseInt(kilometrajeStr);
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "El kilometraje debe ser un número válido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar que todos los campos estén llenos
        if (marca.isEmpty() || modelo.isEmpty() || año.isEmpty() || placa.isEmpty() || kilometrajeStr.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener el userId del usuario actual
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(requireContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = user.getUid();

        // Obtener la URL de la imagen desde presets
        db.collection("presets")
                .whereEqualTo("marca", marca)
                .whereEqualTo("modelo", modelo)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String imageUrl = documentSnapshot.getString("imageUrl");

                        // Crear un nuevo documento con un ID único
                        DocumentReference docRef = db.collection("vehiculos").document();
                        String vehicleId = docRef.getId(); // Obtener el ID del documento recién creado

                        // Crear un mapa de datos para guardar en Firestore
                        Map<String, Object> vehiculo = new HashMap<>();
                        vehiculo.put("marca", marca);
                        vehiculo.put("modelo", modelo);
                        vehiculo.put("año", año);
                        vehiculo.put("placa", placa);
                        vehiculo.put("imageUrl", imageUrl);
                        vehiculo.put("userId", userId);
                        vehiculo.put("kilometraje", kilometraje);
                        vehiculo.put("vehicleId", vehicleId); // Agregar el vehicleId al mapa de datos

                        // Guardar el vehículo con el ID generado
                        docRef.set(vehiculo)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(requireContext(), "Vehículo guardado con éxito", Toast.LENGTH_SHORT).show();
                                    // Limpiar campos después de guardar
                                    limpiarCampos();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(requireContext(), "Error al guardar el vehículo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(requireContext(), "No se encontró la imagen para el vehículo", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error al obtener la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

}

    private void limpiarCampos() {
        editTextAño.setText("");
        editTextPlaca.setText("");
        editTextKilometraje.setText("");
        spinnerMarca.setSelection(0);
        spinnerModelo.setSelection(0);
    }
}
