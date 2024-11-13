package com.example.appvehiculos.ui.documentos;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appvehiculos.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DocumentosFragment extends Fragment {

    private static final int PICK_IMAGE = 1;
    private static final int PICK_PDF = 2;
    private StorageReference mStorageRef;
    private FirebaseFirestore db;
    private RecyclerView documentsRecyclerView;
    private com.example.appvehiculos.ui.documentos.DocumentosAdapter adapter;
    private List<Document> documentList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_documentos, container, false);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        documentList = new ArrayList<>();

        documentsRecyclerView = view.findViewById(R.id.documentsRecyclerView);
        documentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new com.example.appvehiculos.ui.documentos.DocumentosAdapter(documentList);
        documentsRecyclerView.setAdapter(adapter);

        Button uploadImageButton = view.findViewById(R.id.uploadImageButton);
        Button uploadPdfButton = view.findViewById(R.id.uploadPdfButton);

        uploadImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        });

        uploadPdfButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            startActivityForResult(intent, PICK_PDF);
        });

        loadUserDocuments();

        adapter.setOnItemClickListener(document -> {
            if (document.isImage()) {
                // Aquí puedes implementar la lógica para abrir la imagen en pantalla completa
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(document.getFileUrl()), "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            if (requestCode == PICK_IMAGE) {
                uploadImage(fileUri);
            } else if (requestCode == PICK_PDF) {
                uploadPdf(fileUri);
            }
        }
    }

    private void uploadImage(Uri imageUri) {
        if (imageUri != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference fileRef = mStorageRef.child("images/" + UUID.randomUUID().toString());
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(getContext(), "Imagen subida con éxito", Toast.LENGTH_SHORT).show();
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            saveFileMetadata(uri.toString(), userId, "image");
                        });
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al subir la imagen", Toast.LENGTH_SHORT).show());
        }
    }

    private void uploadPdf(Uri pdfUri) {
        if (pdfUri != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference fileRef = mStorageRef.child("pdfs/" + UUID.randomUUID().toString());
            fileRef.putFile(pdfUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(getContext(), "PDF subido con éxito", Toast.LENGTH_SHORT).show();
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            saveFileMetadata(uri.toString(), userId, "pdf");
                        });
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al subir el PDF", Toast.LENGTH_SHORT).show());
        }
    }

    private void saveFileMetadata(String fileUrl, String userId, String fileType) {
        Map<String, Object> fileMetadata = new HashMap<>();
        fileMetadata.put("fileUrl", fileUrl);
        fileMetadata.put("userId", userId);
        fileMetadata.put("type", fileType);

        db.collection("user_files").add(fileMetadata)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Metadata guardada con éxito", Toast.LENGTH_SHORT).show();
                    loadUserDocuments();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error al guardar metadata", Toast.LENGTH_SHORT).show());
    }

    private void loadUserDocuments() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("user_files")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    documentList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String fileUrl = document.getString("fileUrl");
                        String fileType = document.getString("type");
                        boolean isImage = "image".equals(fileType);

                        Document doc = new Document(userId, fileUrl, isImage);
                        documentList.add(doc);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error al cargar documentos", Toast.LENGTH_SHORT).show());
    }
}