package com.example.followcheck.ui.instagram;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.followcheck.R;

public class ConnectInstagramFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connect_instagram, container, false);

        view.findViewById(R.id.btn_continue).setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_connect_to_webview)
        );

        return view;
    }
}
