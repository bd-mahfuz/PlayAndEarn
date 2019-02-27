package com.kcirqueit.playandearn.activity;

import androidx.appcompat.app.AppCompatActivity;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.kcirqueit.playandearn.R;
import com.kcirqueit.playandearn.adapter.MyQuestionAdapter;
import com.kcirqueit.playandearn.model.Question;
import com.kcirqueit.playandearn.viewModel.QuestionViewModel;

import java.util.ArrayList;
import java.util.List;

public class MyQuestionActivity extends AppCompatActivity {

    @BindView(R.id.my_question_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.my_question_rv)
    RecyclerView mQuestionRv;

    @BindView(R.id.noQMsg)
    TextView noQuestionMsg;


    private String quizId;
    private String totalQuestion;

    private QuestionViewModel questionViewModel;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;
    private List<Question> questions = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_question);

        quizId = getIntent().getStringExtra("quizId");
        totalQuestion = getIntent().getStringExtra("totalQuestion");

        Log.d("totalQuestion", totalQuestion);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        questionViewModel = ViewModelProviders.of(this).get(QuestionViewModel.class);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Questions");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mQuestionRv.setLayoutManager(new LinearLayoutManager(this));

    }

    @OnClick(R.id.questionFAB)
    public void onClickAddQuestion() {
        if (questions.size() == Integer.parseInt(totalQuestion)) {
            Toast.makeText(this, "You can't add more than ", Toast.LENGTH_SHORT).show();
        } else {
            Intent addQuestionIntent = new Intent(this, AddQuestionActivity.class);
            addQuestionIntent.putExtra("quizId", quizId);
            startActivity(addQuestionIntent);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..");
        progressDialog.show();
        questionViewModel.getAllQuestionsByQuizId(quizId).observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                questions.clear();
                if (dataSnapshot.getValue() != null) {
                    noQuestionMsg.setVisibility(View.GONE);
                    progressDialog.dismiss();

                    for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
                        Question question = questionSnapshot.getValue(Question.class);
                        questions.add(question);
                    }

                    MyQuestionAdapter adapter = new MyQuestionAdapter(MyQuestionActivity.this,
                            questions, questionViewModel);
                    mQuestionRv.setAdapter(adapter);

                } else {
                    noQuestionMsg.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return true;
    }
}
