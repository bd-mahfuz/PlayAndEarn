package com.kcirqueit.playandearn.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirqueit.playandearn.R;
import com.kcirqueit.playandearn.activity.DashBoardActivity;
import com.kcirqueit.playandearn.activity.FragmentContainerActivity;
import com.kcirqueit.playandearn.activity.InstructionActivity;
import com.kcirqueit.playandearn.activity.MainActivity;
import com.kcirqueit.playandearn.activity.ResultActivity;
import com.kcirqueit.playandearn.adapter.BookmarkAdapter;
import com.kcirqueit.playandearn.model.Quiz;
import com.kcirqueit.playandearn.viewModel.ParticipantViewModel;
import com.kcirqueit.playandearn.viewModel.QuizViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookmarkFragment extends Fragment implements BookmarkAdapter.OnClickListener {

    private static final String TAG = "BookmarkFragment";

    private DatabaseReference mRootRef;
    private DatabaseReference mBookMarkRef;
    private DatabaseReference mParticipantRef;
    private DatabaseReference mQuizRef;
    private FirebaseAuth mAuth;

    private ParticipantViewModel participantViewModel;
    private QuizViewModel quizViewModel;

    @BindView(R.id.qui_bookmark_rv)
    RecyclerView mBookmarkRv;

    @BindView(R.id.no_bookmark_found_tv)
    TextView noBookmarkFoundTv;

    private FragmentContainerActivity activity;

    private List<Map<String, String>> bookmarkList = new ArrayList<>();
    private BookmarkAdapter adapter;

    public BookmarkFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        participantViewModel = ViewModelProviders.of(this).get(ParticipantViewModel.class);
        quizViewModel = ViewModelProviders.of(this).get(QuizViewModel.class);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mBookMarkRef = mRootRef.child("BookMarks");
        mParticipantRef = mRootRef.child("Participants");
        mQuizRef = mRootRef.child("Quizes");


        activity = (FragmentContainerActivity) getActivity();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);
        ButterKnife.bind(this, view);

        activity.getSupportActionBar().setCustomView(null);
        activity.getSupportActionBar().setTitle("Bookmark");

        mBookmarkRv.setLayoutManager(new LinearLayoutManager(activity));


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        Log.d("userId:", mAuth.getCurrentUser().getUid());
        mBookMarkRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookmarkList.clear();
                if (dataSnapshot.getValue() != null) {
                    noBookmarkFoundTv.setVisibility(View.GONE);
                    for (DataSnapshot quizSnapshot : dataSnapshot.getChildren()) {

                        String quizName = quizSnapshot.child("quizName").getValue().toString();
                        String quizId = quizSnapshot.child("quizId").getValue().toString();

                        Map<String, String> map = new HashMap<>();
                        map.put("quizName", quizName);
                        map.put("quizId", quizId);

                        bookmarkList.add(map);
                    }
                    adapter = new BookmarkAdapter(activity, bookmarkList);
                    adapter.setOnClickListener(BookmarkFragment.this);
                    mBookmarkRv.setAdapter(adapter);
                    progressDialog.dismiss();

                } else {
                    noBookmarkFoundTv.setVisibility(View.VISIBLE);
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
    public void onItemClick(String quizId) {

        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        mParticipantRef.child(quizId).child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    mQuizRef.child(quizId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                Quiz quiz = dataSnapshot.getValue(Quiz.class);
                                gotoResultActivity(quiz);
                                progressDialog.dismiss();
                            } else {
                                Toast.makeText(activity, "Quiz not found.", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d(TAG, databaseError.toException().toString());
                            progressDialog.dismiss();
                        }
                    });
                    //Log.d(TAG, dataSnapshot.getValue() + "");

                } else {

                    mQuizRef.child(quizId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                Quiz quiz = dataSnapshot.getValue(Quiz.class);
                                gotoInstructionActivity(quiz);
                                progressDialog.dismiss();
                            } else {
                                Toast.makeText(activity, "Quiz not found.", Toast.LENGTH_SHORT).show();
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.toException().toString());
            }
        });
    }


    public void isParticipate(String quizId) {
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        participantViewModel.isParticipate(mAuth.getCurrentUser().getUid(), quizId).observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                Log.d(TAG, "inside erooor");
                if (dataSnapshot.getValue() != null) {

                    //gotoResultActivity(quizId);
                    progressDialog.dismiss();

                } else {

                    quizViewModel.getQuizById(quizId).observe(BookmarkFragment.this, new Observer<DataSnapshot>() {
                        @Override
                        public void onChanged(DataSnapshot dataSnapshot) {
                            Log.d(TAG, "inside 2 erooor");
                            if (dataSnapshot.getValue() != null) {
                                Quiz quiz = dataSnapshot.getValue(Quiz.class);
                                gotoInstructionActivity(quiz);
                                progressDialog.dismiss();
                            } else {
                                Toast.makeText(activity, "Quiz not found.", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });

                }
            }
        });
    }


    @Override
    public void deleteBookmarkd(String quiId) {

        mBookMarkRef.child(mAuth.getCurrentUser().getUid()).child(quiId)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    adapter.notifyDataSetChanged();
                    Toast.makeText(activity, "Quiz is removed from bookmark.", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(activity, "Something went wrong. Quiz not removed.", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

    public void gotoResultActivity(Quiz quiz) {
        Intent resultIntent = new Intent(activity, ResultActivity.class);
        resultIntent.putExtra("quiz", quiz);
        startActivity(resultIntent);
    }

    public void gotoInstructionActivity(Quiz quiz) {
        Intent mainIntent = new Intent(activity, InstructionActivity.class);
        mainIntent.putExtra("quiz", quiz);
        startActivity(mainIntent);
    }
}
