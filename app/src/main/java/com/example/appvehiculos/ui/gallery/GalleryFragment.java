// GalleryFragment.java

package com.example.appvehiculos.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.appvehiculos.R;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private EditText editTextGasolina, editTextMantenimiento, editTextOtros;
    private Button btnAgregarGasto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el layout
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        // Inicializar los campos
        editTextGasolina = root.findViewById(R.id.editTextGasolina);
        editTextMantenimiento = root.findViewById(R.id.editTextMantenimiento);
        editTextOtros = root.findViewById(R.id.editTextOtros);
        btnAgregarGasto = root.findViewById(R.id.btnAgregarGasto);

        // Obtener el ViewModel
        galleryViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);

        // Agregar un listener para el botón
        btnAgregarGasto.setOnClickListener(v -> {
            double gasolina = getMonto(editTextGasolina);
            double mantenimiento = getMonto(editTextMantenimiento);
            double otros = getMonto(editTextOtros);

            // Guardar los gastos en Firebase
            galleryViewModel.agregarGasto("Gasolina", gasolina);
            galleryViewModel.agregarGasto("Mantenimiento", mantenimiento);
            galleryViewModel.agregarGasto("Otros", otros);

            // Mostrar un mensaje de éxito
            Toast.makeText(getContext(), "Gasto agregado exitosamente", Toast.LENGTH_SHORT).show();

            // Limpiar los campos
            editTextGasolina.setText("");
            editTextMantenimiento.setText("");
            editTextOtros.setText("");
        });

        return root;
    }

    private double getMonto(EditText editText) {
        String text = editText.getText().toString();
        return text.isEmpty() ? 0 : Double.parseDouble(text);
    }
}
