package com.anne.respersystem.ui.suporte;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SuporteViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SuporteViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
}