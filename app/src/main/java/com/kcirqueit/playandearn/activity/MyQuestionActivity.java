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
import android.util.Log;
import android.view.Menu;
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
    private String isPublished;

    MyQuestionAdapter adapter;

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
        isPublished = getIntent().getStringExtra("isPublished");

        //Log.d("totalQuestion", totalQuestion);

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
            Toast.makeText(this, "You can't add more than "+ totalQuestion+ " questions", Toast.LENGTH_SHORT).show();
        } else {
            Intent addQuestionIntent = new Intent(this, AddQuestionActivity.class);
            addQuestionIntent.putExtra("quizId", quizId);
            addQuestionIntent.putExtra("totalQuestion", totalQuestion);
            addQuestionIntent.putExtra("isPublished", isPublished);
            startActivity(addQuestionIntent);
            //finish();
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

                    adapter = new MyQuestionAdapter(MyQuestionActivity.this,
                            questions, questionViewModel, isPublished);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.myquiz_menu, menu);

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
                    adapter.getFilter().filter(newText);
                }

                return true;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }
}
