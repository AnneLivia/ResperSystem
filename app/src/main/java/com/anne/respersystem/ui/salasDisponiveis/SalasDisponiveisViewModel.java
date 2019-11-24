package com.anne.respersystem.ui.salasdisponiveis;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SalasDisponiveisViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SalasDisponiveisViewModel() {
        mText = new MutableLiveData<>();

    }

    public LiveData<String> getText() {
        return mText;
    }
}