package com.example.appvehiculos.ui.promocrear;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PromocrearViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public PromocrearViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Este es el fragmento para crear una promoción");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
