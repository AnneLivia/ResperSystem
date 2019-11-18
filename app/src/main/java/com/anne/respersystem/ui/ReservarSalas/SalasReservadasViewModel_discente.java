package com.anne.respersystem.ui.ReservarSalas;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SalasReservadasViewModel_discente extends ViewModel {

    private MutableLiveData<String> mText;

    public SalasReservadasViewModel_discente() {
        mText = new MutableLiveData<>();
        mText.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }
}