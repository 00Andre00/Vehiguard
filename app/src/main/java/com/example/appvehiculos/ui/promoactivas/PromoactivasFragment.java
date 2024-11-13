package com.example.appvehiculos.ui.promoactivas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appvehiculos.R;
import com.example.appvehiculos.ui.promoactivas.adapter.PromoactivasAdapter;
import com.example.appvehiculos.ui.promoactivas.Promocion;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PromoactivasFragment extends Fragment {
    private RecyclerView recyclerView;
    private PromoactivasAdapter promoactivasAdapter;
    private List<Promocion> promocionesList = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_promoactivas, container, false);

        recyclerView = root.findViewById(R.id.recyclerViewPromoactivas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        promoactivasAdapter = new PromoactivasAdapter(getContext(), promocionesList);
        recyclerView.setAdapter(promoactivasAdapter);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        loadPromociones();

        return root;
    }

    private void loadPromociones() {
        if (user == null) {
            Toast.makeText(getContext(), "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        CollectionReference promocionesRef = db.collection("promociones");

        promocionesRef.whereEqualTo("tallerId", userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        promocionesList.clear();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Promocion promocion = document.toObject(Promocion.class);
                            promocion.setId(document.getId());
                            promocionesList.add(promocion);
                        }
                        promoactivasAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error al cargar promociones: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}