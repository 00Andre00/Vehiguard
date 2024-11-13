package com.example.appvehiculos.ui.vehiculos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class VehiculosViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public VehiculosViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Esta es la pestaña de mis vehículos");
    }

    public LiveData<String> getText() {
        return mText;
    }
}