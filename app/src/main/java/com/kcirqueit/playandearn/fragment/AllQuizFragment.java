package com.kcirqueit.playandearn.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirqueit.playandearn.R;
import com.kcirqueit.playandearn.activity.FragmentContainerActivity;
import com.kcirqueit.playandearn.activity.InstructionActivity;
import com.kcirqueit.playandearn.activity.ResultActivity;
import com.kcirqueit.playandearn.adapter.QuizAdapter;
import com.kcirqueit.playandearn.model.Quiz;
import com.kcirqueit.playandearn.utility.InternetConnection;
import com.kcirqueit.playandearn.viewModel.QuizViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllQuizFragment extends Fragment implements QuizAdapter.OnItemClickListener{

    private static final String TAG = "AllQuizFragment";

    @BindView(R.id.a_quiz_rv)
    RecyclerView mQuizRv;

    @BindView(R.id.a_no_quiz_Tv)
    TextView mNoQuizMsg;

    private QuizViewModel quizViewModel;

    private List<Quiz> quizzes = new ArrayList<>();

    private QuizAdapter mQuizAdapter;

    private FragmentContainerActivity activity;
    private DatabaseReference mBookMarkRef;
    private DatabaseReference mParticipantRef;
    private DatabaseReference mQuizRef;
    private DatabaseReference mUserRef;

    private FirebaseAuth mAuth;

    private AdView mAdView;



    public AllQuizFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        activity = (FragmentContainerActivity) getActivity();

        MobileAds.initialize(activity,
                getString(R.string.admob_app_id));

        setHasOptionsMenu(true);

        mAuth = FirebaseAuth.getInstance();
        mParticipantRef = FirebaseDatabase.getInstance().getReference().child("Participants");
        mBookMarkRef = FirebaseDatabase.getInstance().getReference().child("BookMarks");
        mQuizRef = FirebaseDatabase.getInstance().getReference().child("Quizes");
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_all_quiz, container, false);


        ButterKnife.bind(this, view);
        quizViewModel = ViewModelProviders.of(this).get(QuizViewModel.class);

        activity.getSupportActionBar().setCustomView(null);
        activity.getSupportActionBar().setTitle("All Quizzes");

        mQuizRv.setLayoutManager(new LinearLayoutManager(activity));

        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        mQuizRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                quizzes.clear();
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot quizSnapshot : dataSnapshot.getChildren()) {
                        Quiz quiz = quizSnapshot.getValue(Quiz.class);
                        if (quiz.getStatus().equals("published")) {
                            quizzes.add(quiz);
                        }
                    }

                    if (quizzes.size() > 0) {
                        mNoQuizMsg.setVisibility(View.GONE);
                        mQuizAdapter = new QuizAdapter(activity, quizzes);
                        mQuizAdapter.setItemClickListener(AllQuizFragment.this);
                        mQuizRv.setAdapter(mQuizAdapter);
                    } else {
                        mNoQuizMsg.setVisibility(View.VISIBLE);
                    }
                    progressDialog.dismiss();
                } else {
                    mNoQuizMsg.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.toException().toString());
            }
        });

    }



    @Override
    public void onBookmarkClick(Quiz quiz) {
        Map map = new HashMap();
        map.put("quizId", quiz.getId());
        map.put("quizName", quiz.getQuizName());

        if (InternetConnection.checkConnection(activity)) {
            mBookMarkRef.child(mAuth.getCurrentUser().getUid()).child(quiz.getId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.getValue() != null) {
                                Toast.makeText(activity, "Bookmarked already added.", Toast.LENGTH_SHORT).show();
                            } else {
                                mBookMarkRef.child(mAuth.getCurrentUser().getUid()).child(quiz.getId())
                                        .setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(activity, "Bookmark is added.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(activity, "Something went wrong. Bookmark is not added.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(activity, "Something is wrong please contact with developer.", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, databaseError.toException().toString());
                        }
                    });
        } else {
            InternetConnection.showNoInternetDialog(activity);
        }
    }

    @Override
    public void onItemClick(Quiz quiz) {
        if (InternetConnection.checkConnection(activity)) {

            if(mAuth.getCurrentUser().getUid().equals(quiz.getUserId())) {

                Toast.makeText(activity, "You can't participate your own created quiz.", Toast.LENGTH_SHORT).show();

            } else {

                mParticipantRef.child(quiz.getId())
                        .child(mAuth.getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {

                                    Intent resultIntent = new Intent(activity, ResultActivity.class);
                                    Log.d("quiz name:", quiz.getQuizName());
                                    resultIntent.putExtra("quizId", quiz.getId());
                                    startActivity(resultIntent);

                                } else {
                                    Intent instructionIntent = new Intent(activity, InstructionActivity.class);
                                    instructionIntent.putExtra("quiz", quiz);
                                    startActivity(instructionIntent);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d("Quiz adapter:", databaseError.toException().toString());
                            }
                        });
            }


        } else {
            InternetConnection.showNoInternetDialog(activity);
        }
    }

    @Override
    public void getBookmarkData(Quiz quiz, QuizAdapter.ViewHolder viewHolder) {

        if (InternetConnection.checkConnection(activity)) {
            mBookMarkRef.child(mAuth.getCurrentUser().getUid()).child(quiz.getId())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                viewHolder.bookmarkIv.setImageDrawable(ContextCompat.getDrawable(activity,
                                        R.drawable.ic_bookmark_fill_orange));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d(TAG, databaseError.toException().toString());
                        }
                    });
        } else {
            InternetConnection.showNoInternetDialog(activity);
        }

    }

    @Override
    public void getCreatorName(String userId, TextView creatorTv) {
        if (mAuth.getUid().equals(userId)) {

            creatorTv.setText("Creator: You");

        } else{
            mUserRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        String userName = dataSnapshot.child("userName").getValue().toString();
                        //Log.d("user name:", userName);
                        creatorTv.setText("Creator: "+userName);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, databaseError.toException().toString());
                }
            });
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        menu.clear();
        inflater.inflate(R.menu.myquiz_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.search_menu);

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText != null){
                    mQuizAdapter.getFilter().filter(newText);
                }

                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (getFragmentManager().getBackStackEntryCount() != 0) {
                getFragmentManager().popBackStack();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
