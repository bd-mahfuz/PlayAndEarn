package com.kcirqueit.playandearn.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirqueit.playandearn.R;
import com.kcirqueit.playandearn.activity.FragmentContainerActivity;
import com.kcirqueit.playandearn.adapter.AchievementAdapter;
import com.kcirqueit.playandearn.model.Participant;
import com.kcirqueit.playandearn.viewModel.ParticipantViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AchivementFragment extends Fragment {

    private static final String TAG = "AchivementFragment";

    @BindView(R.id.no_achievement_tv)
    TextView noAchieveTv;

    @BindView(R.id.achievement_rv)
    RecyclerView mAchieveRv;

    private FragmentContainerActivity activity;

    private DatabaseReference mParticipantRef;
    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;

    private String mCurrentUserId;

    private List<Participant> participantList = new ArrayList<>();

    private ParticipantViewModel participantViewModel;
    AchievementAdapter adapter;

    public AchivementFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        activity = (FragmentContainerActivity) getActivity();

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mParticipantRef = mRootRef.child("Participants");
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        participantViewModel = ViewModelProviders.of(this).get(ParticipantViewModel.class);
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_achivement, container, false);
        ButterKnife.bind(this, view);

        activity.getSupportActionBar().setCustomView(null);
        activity.getSupportActionBar().setTitle("My Achievement");

        mAchieveRv.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new AchievementAdapter(participantList, activity);
        mAchieveRv.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading ...");
        progressDialog.show();
        if (participantList.size() == 0) {
            participantViewModel.getAllQuiz().observe(this, new Observer<DataSnapshot>() {
                @Override
                public void onChanged(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {

                        participantList.clear();
                        // fetching participant details using quiz id from Participant Ref
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            String quizId = dataSnapshot1.getKey();
                            //Log.d(TAG, "onDataChange: fjeijal" + quizId);
                            mParticipantRef.child(quizId).child(mCurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    //participantList.clear();
                                    if (dataSnapshot.getValue() != null) {

                                        Participant participant = dataSnapshot.getValue(Participant.class);
                                        participantList.add(participant);
                                        adapter.notifyDataSetChanged();
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
                        progressDialog.dismiss();

                    } else {
                        noAchieveTv.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                    }
                }
            });
        } else {
            progressDialog.dismiss();
        }


    }
}
