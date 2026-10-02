package com.example.followcheck.scanner;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.WebStorage;
import android.webkit.WebView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class InstagramSessionManager {
    private static InstagramSessionManager instance;
    private final MutableLiveData<LoginState> loginState = new MutableLiveData<>(LoginState.UNKNOWN);

    private InstagramSessionManager() {}

    public static synchronized InstagramSessionManager getInstance() {
        if (instance == null) {
            instance = new InstagramSessionManager();
        }
        return instance;
    }

    public LiveData<LoginState> getLoginState() {
        return loginState;
    }

    public void setLoginState(LoginState state) {
        loginState.postValue(state);
    }

    public void checkSession() {
        CookieManager cookieManager = CookieManager.getInstance();
        boolean hasSession = cookieManager.hasCookies();
        // In a real implementation, we would check for specific Instagram session cookies 
        // but for now we follow the requirement to use observable signals.
        if (hasSession) {
            loginState.postValue(LoginState.LOGGED_IN);
        } else {
            loginState.postValue(LoginState.LOGGED_OUT);
        }
    }

    public void logout(Context context) {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookies(value -> {
            loginState.postValue(LoginState.LOGGED_OUT);
        });
        cookieManager.flush();
        
        WebStorage.getInstance().deleteAllData();
        
        // Clear WebView database, cache, etc.
        WebView webView = new WebView(context);
        webView.clearCache(true);
        webView.clearHistory();
        webView.clearFormData();
        webView.destroy();
    }
}
