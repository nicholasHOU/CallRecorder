package com.android.callrecorder.home.ui.callrecord;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CallRecordViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public CallRecordViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }



}