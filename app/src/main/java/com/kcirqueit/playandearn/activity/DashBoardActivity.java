package com.kcirqueit.playandearn.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.kcirqueit.playandearn.R;

public class DashBoardActivity extends AppCompatActivity implements RewardedVideoAdListener {

    private RewardedVideoAd mRewardedVideoAd;
    private InterstitialAd mInterstitialAd;

    @BindView(R.id.dash_toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Dashboard");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        MobileAds.initialize(this, getString(R.string.admob_app_id));

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        mInterstitialAd.loadAd(adRequest);

        // Use an activity context to get the rewarded video instance.
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);

        loadRewardedVideoAd();

        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(adRequest);
            }
        });

    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
    }

    @OnClick(R.id.play_quiz_bt)
    public void onPlayClick() {
        startActivity(new Intent(this, DashBoard2.class));
    }

    @OnClick(R.id.video_bt)
    public void onVideoClick() {
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        } else {
            Toast.makeText(this, "The RewardedVideoAd wasn't loaded yet.", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.wallet_bt)
    public void onWalletBtClick() {
        startActivity(new Intent(this, WalletActivity.class));
    }


    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            //Log.d("TAG", "The interstitial wasn't loaded yet.");
            Toast.makeText(this, "The interstitial wasn't loaded yet.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        Toast.makeText(this, "onRewarded! currency: " + rewardItem.getType() + "  amount: " +
                rewardItem.getAmount(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onRewardedVideoCompleted() {

    }

    @Override
    public void onResume() {
        mRewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.dash_board_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            finish();
        }

        switch (item.getItemId()) {
            case R.id.profile_menu:
                Intent profileIntent = new Intent(this, ProfileActivity.class);
                startActivity(profileIntent);
                break;

        }
        return true;
    }
}
