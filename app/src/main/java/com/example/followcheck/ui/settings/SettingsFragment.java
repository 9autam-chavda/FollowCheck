package com.example.followcheck.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.followcheck.R;
import com.example.followcheck.scanner.InstagramSessionManager;
import com.example.followcheck.scanner.LoginState;
import com.example.followcheck.ui.history.HistoryViewModel;
import com.example.followcheck.ui.onboarding.OnboardingActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SettingsFragment extends Fragment {

    private HistoryViewModel historyViewModel;
    private InstagramSessionManager sessionManager;
    private TextView textAccountStatus;
    private MaterialButton btnDisconnect;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        textAccountStatus = view.findViewById(R.id.text_account_status);
        btnDisconnect = view.findViewById(R.id.btn_disconnect);

        view.findViewById(R.id.btn_delete_data).setOnClickListener(v -> showDeleteConfirmation());

        view.findViewById(R.id.btn_theme).setOnClickListener(v -> {
            // Placeholder for theme selection logic
        });

        view.findViewById(R.id.btn_privacy_policy).setOnClickListener(v -> {
            // Placeholder for privacy policy
        });

        // Diagnostic Prototype Button
        View btnDiagnostic = view.findViewById(R.id.btn_dom_diagnostic);
        if (btnDiagnostic != null) {
            btnDiagnostic.setOnClickListener(v -> 
                Navigation.findNavController(v).navigate(R.id.action_settings_to_diagnostic)
            );
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        sessionManager = InstagramSessionManager.getInstance();

        sessionManager.getLoginState().observe(getViewLifecycleOwner(), state -> {
            if (state == LoginState.LOGGED_IN) {
                textAccountStatus.setText("Connected");
                if (btnDisconnect != null) btnDisconnect.setVisibility(View.VISIBLE);
            } else {
                textAccountStatus.setText("Not connected");
                if (btnDisconnect != null) btnDisconnect.setVisibility(View.GONE);
            }
        });
        
        if (btnDisconnect != null) {
            btnDisconnect.setOnClickListener(v -> showDisconnectConfirmation());
        }
    }

    private void showDisconnectConfirmation() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Disconnect Instagram")
                .setMessage("Are you sure you want to disconnect your Instagram web session? Your scan history will be preserved.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Disconnect", (dialog, which) -> {
                    sessionManager.logout(requireContext());
                })
                .show();
    }

    private void showDeleteConfirmation() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.dialog_delete_title)
                .setMessage(R.string.dialog_delete_message)
                .setNegativeButton(R.string.btn_cancel, null)
                .setPositiveButton(R.string.btn_delete, (dialog, which) -> {
                    historyViewModel.deleteHistory();
                    sessionManager.logout(requireContext());
                    // Clear onboarding status and restart
                    requireContext().getSharedPreferences("prefs", android.content.Context.MODE_PRIVATE)
                            .edit().clear().apply();
                    startActivity(new Intent(requireContext(), OnboardingActivity.class));
                    requireActivity().finish();
                })
                .show();
    }
}
