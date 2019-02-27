package com.kcirqueit.playandearn.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.kcirqueit.playandearn.R;
import com.kcirqueit.playandearn.activity.FragmentContainerActivity;
import com.kcirqueit.playandearn.adapter.MyQuizAdapter;
import com.kcirqueit.playandearn.adapter.QuizAdapter;
import com.kcirqueit.playandearn.model.Quiz;
import com.kcirqueit.playandearn.viewModel.QuestionViewModel;
import com.kcirqueit.playandearn.viewModel.QuizViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyQuizFragmnet extends Fragment {

    @BindView(R.id.no_quiz_layout)
    LinearLayout mNoQuizLayout;

    @BindView(R.id.my_quiz_rv)
    RecyclerView mQuizRv;

    private QuizViewModel quizViewModel;
    private QuestionViewModel questionViewModel;

    private List<Quiz> quizzes = new ArrayList<>();

    private MyQuizAdapter mQuizAdapter;

    private FragmentContainerActivity activity;

    private FirebaseAuth mAuth;
    private String mCurrentUserId;

    public MyQuizFragmnet() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        activity = (FragmentContainerActivity) getActivity();

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        quizViewModel = ViewModelProviders.of(this).get(QuizViewModel.class);
        questionViewModel = ViewModelProviders.of(this).get(QuestionViewModel.class);

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_my_quiz_fragmnet, container, false);
        ButterKnife.bind(this, view);

        activity.getSupportActionBar().setTitle("My Quiz");

        mQuizRv.setLayoutManager(new LinearLayoutManager(activity));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        quizViewModel.getAllQuiz().observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                quizzes.clear();
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot quizSnapshot : dataSnapshot.getChildren()) {
                        Quiz quiz = quizSnapshot.getValue(Quiz.class);
                        if (quiz.getUserId().equals(mCurrentUserId)) {
                            quizzes.add(quiz);
                        }
                    }

                    if (quizzes.size() > 0) {
                        mNoQuizLayout.setVisibility(View.GONE);
                        mQuizAdapter = new MyQuizAdapter(activity, quizzes, quizViewModel, questionViewModel);
                        mQuizRv.setAdapter(mQuizAdapter);

                    } else {
                        mNoQuizLayout.setVisibility(View.VISIBLE);
                    }
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                }
            }
        });

    }
}
