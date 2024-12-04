package com.example.appvehiculos.ui.gallery;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GalleryViewModel extends ViewModel {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // GalleryViewModel.java

    public void agregarGasto(String tipoGasto, double monto) {
        if (monto > 0) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            // Crear un mapa con los datos del gasto
            Map<String, Object> gasto = new HashMap<>();
            gasto.put("fecha", fecha);
            gasto.put("monto", monto);
            gasto.put("tipo", tipoGasto);

            // Guardar en Firestore, en la colección "Gastos"
            db.collection("Gastos")  // Aquí cambiamos "Usuarios" por "Gastos"
                    .document(userId)
                    .collection("Detalle")
                    .add(gasto)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("GalleryViewModel", "Gasto guardado con éxito");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("GalleryViewModel", "Error al agregar gasto", e);
                    });
        }
    }
    }