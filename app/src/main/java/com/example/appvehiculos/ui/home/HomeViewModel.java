package com.example.appvehiculos.ui.home;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.auth.FirebaseUser;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Establecer valor inicial para el texto de la bienvenida
        mText.setValue("Cargando...");

        // Obtenemos el nombre del usuario después de la autenticación
        obtenerNombreUsuario();
    }

    private void obtenerNombreUsuario() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            // Obtener el UID del usuario autenticado
            String uid = user.getUid();

            // Buscar el nombre del usuario en la base de datos (Firestore)
            db.collection("users")  // Cambié "usuarios" por "users"
                    .document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Obtener el nombre del usuario desde Firestore (campo "name")
                            String nombreUsuario = documentSnapshot.getString("name");  // "name" es el campo
                            if (nombreUsuario != null) {
                                mText.setValue("¡Bienvenido, " + nombreUsuario + "!");
                            } else {
                                mText.setValue("¡Bienvenido!");
                            }
                        } else {
                            mText.setValue("¡Bienvenido!");
                        }
                    })
                    .addOnFailureListener(e -> {
                        mText.setValue("Error al cargar el nombre");
                        Log.e("HomeViewModel", "Error obteniendo datos del usuario", e);
                    });
        } else {
            mText.setValue("Usuario no autenticado");
        }
    }

    public LiveData<String> getText() {
        return mText;
    }
}
