package com.example.followcheck.ui.onboarding;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.followcheck.MainActivity;
import com.example.followcheck.R;
import android.view.View;
import android.widget.Button;

public class OnboardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        Button btnGetStarted = findViewById(R.id.btn_get_started);
        if (btnGetStarted != null) {
            btnGetStarted.setOnClickListener(v -> {
                // Mark onboarding as completed
                SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                prefs.edit().putBoolean("onboarding_completed", true).apply();

                // Navigate to MainActivity
                startActivity(new Intent(OnboardingActivity.this, MainActivity.class));
                finish();
            });
        }
    }
}
