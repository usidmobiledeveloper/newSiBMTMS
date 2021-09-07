package com.usid.mobilebmt.mandirisejahtera.dashboard;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.usid.mobilebmt.mandirisejahtera.R;
import com.usid.mobilebmt.mandirisejahtera.databinding.ActivitySoonBinding;

import static com.usid.mobilebmt.mandirisejahtera.utils.Utility.disableViews;


public class SoonActivity extends AppCompatActivity {

    ActivitySoonBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySoonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent().getExtras().getString("title") != null) {
            String title = getIntent().getExtras().getString("title", getString(R.string.app_name));
            actionBar.setTitle(title);
        }


        loadLottie();

        binding.btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        disableViews(binding.ltComingSoon);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.ltComingSoon.cancelAnimation();
    }

    public void loadLottie() {
        binding.ltComingSoon.setAnimation("coming_soon.json");
        binding.ltComingSoon.loop(true);
        binding.ltComingSoon.setSpeed(1);
        binding.ltComingSoon.enableMergePathsForKitKatAndAbove(true);
        binding.ltComingSoon.playAnimation();

    }
}