package com.example.followcheck.ui.instagram;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.followcheck.scanner.InstagramSessionManager;
import com.example.followcheck.scanner.LoginState;

public class InstagramWebViewViewModel extends ViewModel {
    private final MutableLiveData<Integer> progress = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final InstagramSessionManager sessionManager = InstagramSessionManager.getInstance();

    public LiveData<Integer> getProgress() {
        return progress;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setProgress(int newProgress) {
        progress.setValue(newProgress);
        isLoading.setValue(newProgress < 100);
    }

    public LiveData<LoginState> getLoginState() {
        return sessionManager.getLoginState();
    }

    public void updateLoginState(LoginState state) {
        sessionManager.setLoginState(state);
    }
}
