package com.anne.respersystem.ui.reservarsalas;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReservarSalasViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ReservarSalasViewModel() {
        mText = new MutableLiveData<>();

    }

    public LiveData<String> getText() {
        return mText;
    }
}