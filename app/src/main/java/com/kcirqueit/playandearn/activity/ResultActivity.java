package com.kcirqueit.playandearn.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirqueit.playandearn.R;
import com.kcirqueit.playandearn.model.Participant;
import com.kcirqueit.playandearn.model.Quiz;
import com.kcirqueit.playandearn.utility.DateUtility;

public class ResultActivity extends AppCompatActivity {

    private static final String TAG = "ResultActivity";

    @BindView(R.id.custom_progress5)
    CircleProgressBar mCustomProgressBar;

    @BindView(R.id.result_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.r_quiz_name_et)
    TextView mQuizNameTv;

    @BindView(R.id.r_answered_et)
    TextView mAansweredTv;


    @BindView(R.id.r_total_q_et)
    TextView mTotalQuestionTv;

    @BindView(R.id.r_total_marks_et)
    TextView mTotalMarksEt;

    @BindView(R.id.r_time_limit_et)
    TextView mTtimeLimitEt;



    private String quizId;

    private AdView mAdView;

    private DatabaseReference mRootRef;
    private DatabaseReference mParticipantRef;
    private FirebaseAuth mAuth;
    
    
    private InterstitialAd mInterstitialAd;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        ButterKnife.bind(this);
        MobileAds.initialize(this,
                getString(R.string.admob_app_id));
        
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.admob_interstitial_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mParticipantRef = mRootRef.child("Participants");

        quizId = (String) getIntent().getSerializableExtra("quizId");

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    private void setData() {

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        mParticipantRef.child(quizId).child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Participant participant = dataSnapshot.getValue(Participant.class);

                    //set quiz name in the title bar
                    getSupportActionBar().setTitle(participant.getQuizName());

                    mQuizNameTv.setText(participant.getQuizName());
                    mTotalMarksEt.setText(participant.getTotalMarks()+"");
                    mTtimeLimitEt.setText(DateUtility.milliToHour(Long.parseLong(participant.getTimeLimit()))+" hours");
                    mTotalQuestionTv.setText(participant.getTotalQuestion()+"");

                    mAansweredTv.setText(participant.getTotalAnswered()+"");
                    progressDialog.dismiss();

                    mCustomProgressBar.setProgressFormatter(new CircleProgressBar.ProgressFormatter() {
                        @Override
                        public CharSequence format(int progress, int max) {
                            return progress +"";
                        }
                    });

                    simulateProgress(participant.getScore(), participant.getTotalMarks());


                } else {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.toException().toString());
                progressDialog.dismiss();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (quizId != null) {
            setData();
        }
    }

    private void simulateProgress(int max, int totalMarks) {
        ValueAnimator animator = ValueAnimator.ofInt(max);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int progress = (int) animation.getAnimatedValue();
                mCustomProgressBar.setProgress(progress);
                mCustomProgressBar.setMax(totalMarks);
            }

        });
        animator.setDuration(1000);
        animator.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home)
        {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show(); 
            } else {
                Log.d("ResultActivity", "Failed to load interstitial ads.");
            }
            finish();
        }
        return true;
    }
}
