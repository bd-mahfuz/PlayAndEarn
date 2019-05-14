package com.kcirqueit.playandearn.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirqueit.playandearn.R;
import com.kcirqueit.playandearn.activity.FragmentContainerActivity;
import com.kcirqueit.playandearn.adapter.MyResultAdapter;
import com.kcirqueit.playandearn.model.Participant;
import com.kcirqueit.playandearn.model.Quiz;
import com.kcirqueit.playandearn.viewModel.ParticipantViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyResultFragment extends Fragment {

    private static final String TAG = "MyResultFragment";

    @BindView(R.id.my_result_rv)
    RecyclerView mMyResultRv;


    @BindView(R.id.no_quiz_result_layout)
    LinearLayout mLinearlayout;

    private ParticipantViewModel participantViewModel;

    private FirebaseAuth mAuth;
    private String mCurrentUserId;

    private DatabaseReference mRootRef;
    private DatabaseReference mQuizRef;

    private FragmentContainerActivity activity;

    private MyResultAdapter adapter;

    private List<Quiz> quizList = new ArrayList<>();

    private AdView mAdView;

    public MyResultFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (FragmentContainerActivity) getActivity();

        MobileAds.initialize(activity,
                getString(R.string.admob_app_id));

        participantViewModel = ViewModelProviders.of(this).get(ParticipantViewModel.class);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mQuizRef = mRootRef.child("Quizes");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_result, container, false);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        ButterKnife.bind(this, view);

        activity.getSupportActionBar().setCustomView(null);
        activity.getSupportActionBar().setTitle("Results");

        mMyResultRv.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new MyResultAdapter(activity, quizList);
        mMyResultRv.setAdapter(adapter);

        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading");
        progressDialog.show();

        if (quizList.size() == 0) {
            participantViewModel.getAllQuiz().observe(this, new Observer<DataSnapshot>() {
                @Override
                public void onChanged(DataSnapshot dataSnapshot) {
                    quizList.clear();
                    if (dataSnapshot.getValue() != null) {

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            String quizId = dataSnapshot1.getKey();
                            if (dataSnapshot1.child(mCurrentUserId).getValue() != null) {
                                mLinearlayout.setVisibility(View.GONE);
                                mQuizRef.child(quizId).addListenerForSingleValueEvent(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue() != null) {
                                            Quiz quiz = dataSnapshot.getValue(Quiz.class);
                                            quizList.add(quiz);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.d(TAG, databaseError.toException().toString());
                                    }
                                });

                                mLinearlayout.setVisibility(View.GONE);
                                progressDialog.dismiss();
                            } else {
                                mLinearlayout.setVisibility(View.VISIBLE);
                            }


                        }
                        mLinearlayout.setVisibility(View.GONE);
                        progressDialog.dismiss();

                    } else {
                        mLinearlayout.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                    }
                }
            });
        } else {
            progressDialog.dismiss();
        }

    }
}
