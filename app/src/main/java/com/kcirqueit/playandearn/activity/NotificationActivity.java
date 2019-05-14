package com.kcirqueit.playandearn.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.kcirqueit.playandearn.R;

public class NotificationActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        MobileAds.initialize(this, getString(R.string.admob_app_id));
        // initializing ad
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.admob_interstitial_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        initView();

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Notifications");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    private void initView() {
        mToolbar = findViewById(R.id.notification_toolbar);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {

            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }

            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
