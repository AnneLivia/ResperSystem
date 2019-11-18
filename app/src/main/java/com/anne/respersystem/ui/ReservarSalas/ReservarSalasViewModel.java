package com.anne.respersystem.ui.ReservarSalas;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReservarSalasViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ReservarSalasViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is ReservarSalas fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}