package com.kcirqueit.playandearn.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.kcirqueit.playandearn.R;
import com.kcirqueit.playandearn.adapter.QuizAdapter;
import com.kcirqueit.playandearn.model.Quiz;
import com.kcirqueit.playandearn.viewModel.QuizViewModel;

import java.util.ArrayList;
import java.util.List;

public class DashBoard2 extends AppCompatActivity implements SearchView.OnQueryTextListener {

    /*@BindView(R.id.dash2_toolbar)
    Toolbar mToolbar;*/

    @BindView(R.id.quiz_rv)
    RecyclerView mQuizRv;

    @BindView(R.id.quiz_search_view)
    SearchView mQuizSearchView;

    @BindView(R.id.no_quiz_Tv)
    TextView mNoQuizMsg;

    private QuizViewModel quizViewModel;

    private List<Quiz> quizzes = new ArrayList<>();

    private QuizAdapter mQuizAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board2);

        ButterKnife.bind(this);

        quizViewModel = ViewModelProviders.of(this).get(QuizViewModel.class);

        /*setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Quiz Dashboard");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        mQuizRv.setLayoutManager(new LinearLayoutManager(this));


        mQuizSearchView.setOnQueryTextListener(this);



    }

    @OnClick(R.id.create_quiz_layout)
    public void onClickCreateQuiz() {
        startActivity(new Intent(this, CreateQuizActivity.class));
    }


    @Override
    protected void onStart() {
        super.onStart();

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        quizViewModel.getAllQuiz().observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                quizzes.clear();
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot quizSnapshot : dataSnapshot.getChildren()) {
                        Quiz quiz = quizSnapshot.getValue(Quiz.class);
                        quizzes.add(quiz);

                    }

                    if (quizzes.size() > 0) {
                        mNoQuizMsg.setVisibility(View.GONE);
                        mQuizAdapter = new QuizAdapter(DashBoard2.this, quizzes);
                        mQuizRv.setAdapter(mQuizAdapter);

                    } else {
                        mNoQuizMsg.setVisibility(View.VISIBLE);
                    }
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                }
            }
        });

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mQuizAdapter.getFilter().filter(newText);
        return true;
    }
}
