package com.example.appvehiculos.ui.promociones;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
public class PromocionesViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public PromocionesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Esta es la pestaña de mis promociones");
    }

    public LiveData<String> getText() {
        return mText;
    }
}