package com.example.appvehiculos.ui.promocrear;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class PromocrearFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText etTitulo, etDescripcion, etUbicacion;
    private Spinner spDuracion, spTipoVehiculo;
    private Button btnCrearPromocion;
    private ImageButton ibImagen;
    private Uri imageUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_promocrear, container, false);

        etTitulo = root.findViewById(R.id.etTitulo);
        etDescripcion = root.findViewById(R.id.etDescripcion);
        etUbicacion = root.findViewById(R.id.etUbicacion);
        spDuracion = root.findViewById(R.id.spDuracion);
        spTipoVehiculo = root.findViewById(R.id.spTipoVehiculo);
        btnCrearPromocion = root.findViewById(R.id.btnCrearPromocion);
        ibImagen = root.findViewById(R.id.ibImagen);

        ibImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        btnCrearPromocion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearPromocion();
            }
        });

        return root;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            ibImagen.setImageURI(imageUri);
        }
    }

    private void crearPromocion() {
        String titulo = etTitulo.getText().toString();
        String descripcion = etDescripcion.getText().toString();
        String ubicacion = etUbicacion.getText().toString();
        String duracion = spDuracion.getSelectedItem().toString();
        String tipoVehiculo = spTipoVehiculo.getSelectedItem().toString();

        if (titulo.isEmpty() || descripcion.isEmpty() || ubicacion.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String userType = documentSnapshot.getString("userType");
                    if (userType != null) {
                        Toast.makeText(getContext(), "Tipo de usuario: " + userType, Toast.LENGTH_SHORT).show();

                        if ("taller".equals(userType)) {
                            Map<String, Object> promocion = new HashMap<>();
                            promocion.put("titulo", titulo);
                            promocion.put("descripcion", descripcion);
                            promocion.put("ubicacion", ubicacion);
                            promocion.put("duracion", duracion);
                            promocion.put("tipoVehiculo", tipoVehiculo);
                            promocion.put("tallerId", userId);
                            promocion.put("timestamp", System.currentTimeMillis());

                            if (imageUri != null) {
                                uploadImage(imageUri, new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        promocion.put("imageUrl", uri.toString());
                                        savePromocionToFirestore(db, promocion);
                                    }
                                }, new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Error al subir la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.e("UploadImage", "Error al subir la imagen", e);
                                    }
                                });
                            } else {
                                savePromocionToFirestore(db, promocion);
                            }
                        } else {
                            Toast.makeText(getContext(), "Error: Usuario no autorizado para crear promociones", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Error: Tipo de usuario no encontrado", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Error: Documento de usuario no encontrado", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error al obtener datos del usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImage(Uri uri, OnSuccessListener<Uri> onSuccessListener, OnFailureListener onFailureListener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("promociones/" + userId + "/" + System.currentTimeMillis() + ".jpg");

        storageReference.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener))
                .addOnFailureListener(onFailureListener);
    }

    private void savePromocionToFirestore(FirebaseFirestore db, Map<String, Object> promocion) {
        db.collection("promociones")
                .add(promocion)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Promoción creada con éxito", Toast.LENGTH_SHORT).show();
                    // Limpiar campos después de guardar
                    etTitulo.setText("");
                    etDescripcion.setText("");
                    etUbicacion.setText("");
                    spDuracion.setSelection(0);
                    spTipoVehiculo.setSelection(0);
                    ibImagen.setImageResource(R.drawable.default_image);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al crear promoción: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("PromocrearFragment", "Error al crear promoción", e);
                });
    }
}
