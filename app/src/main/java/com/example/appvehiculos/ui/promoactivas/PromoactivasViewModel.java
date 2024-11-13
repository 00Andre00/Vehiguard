package com.example.appvehiculos.ui.promoactivas;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PromoactivasViewModel extends ViewModel {

    private final MutableLiveData<List<Promocion>> promocionesLiveData;

    public PromoactivasViewModel() {
        promocionesLiveData = new MutableLiveData<>();
        loadPromociones();
    }

    public LiveData<List<Promocion>> getPromociones() {
        return promocionesLiveData;
    }

    private void loadPromociones() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("promociones").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Promocion> promociones = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Promocion promocion = document.toObject(Promocion.class);
                            promociones.add(promocion);
                        }
                        promocionesLiveData.setValue(promociones);
                    } else {
                        // Manejar errores aquí
                    }
                });
    }
}