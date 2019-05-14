package com.kcirqueit.playandearn.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.kcirqueit.playandearn.R;
import com.kcirqueit.playandearn.model.Question;
import com.kcirqueit.playandearn.model.Quiz;
import com.kcirqueit.playandearn.viewModel.QuestionViewModel;

import java.util.ArrayList;
import java.util.List;

public class AddQuestionActivity extends AppCompatActivity {

    @BindView(R.id.add_question_layout)
    Toolbar  mToolbar;

    @BindView(R.id.question_title_et)
    EditText mQuestionTitleEt;

    @BindView(R.id.option_1_et)
    EditText mOption1Et;

    @BindView(R.id.option_2_et)
    EditText mOption2Et;

    @BindView(R.id.option_3_et)
    EditText mOption3Et;

    @BindView(R.id.option_4_et)
    EditText mOption4Et;

    @BindView(R.id.options_rg)
    RadioGroup mOptionsRg;

    @BindView(R.id.create_question_bt)
    AppCompatButton mCreateQuestionBt;

    private int mCorrectAns;
    private String quizId;
    private String requestForEdit;
    private Question question;

    private FirebaseAuth mAuth;
    private String mCurrentuser;

    String totalQuestion;

    private QuestionViewModel questionViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        questionViewModel = ViewModelProviders.of(this).get(QuestionViewModel.class);

        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mCurrentuser = mAuth.getCurrentUser().getUid();

        Log.d("on create:", "called");

        // fetching data from intent
        quizId = getIntent().getStringExtra("quizId");
        totalQuestion = getIntent().getStringExtra("totalQuestion");

        requestForEdit = getIntent().getStringExtra("requestForEdit");
        question = (Question) getIntent().getSerializableExtra("question");

        String toolBarTitle = "Add Question";
        if (requestForEdit != null) {
            toolBarTitle = "Edit Question";
            mCreateQuestionBt.setText("Update");
        }

        if (question != null) {
            setData();
        }

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(toolBarTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mOptionsRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.option1Rd:
                        mCorrectAns = 0;
                        break;

                    case R.id.option2Rd:
                        mCorrectAns = 1;
                        break;

                    case R.id.option3Rd:
                        mCorrectAns = 2;
                        break;

                    case R.id.option4Rd:
                        mCorrectAns = 3;
                        break;
                }
            }
        });

    }

    @OnClick(R.id.create_question_bt)
    public void onClickCreateQuestionBt() {

        String questionTitle = mQuestionTitleEt.getText().toString();
        String option1 = mOption1Et.getText().toString();
        String option2 = mOption2Et.getText().toString();
        String option3 = mOption3Et.getText().toString();
        String option4 = mOption4Et.getText().toString();

        if (questionTitle.isEmpty()) {
            mQuestionTitleEt.setError("This field should not be empty!");
            return;
        } else if (option1.isEmpty()) {
            mOption1Et.setError("This filed should not be empty!");
            return;
        } else if (option2.isEmpty()) {
            mOption2Et.setError("This filed should not be empty!");
            return;
        } else if (option3.isEmpty()) {
            mOption3Et.setError("This filed should not be empty!");
            return;
        } else if (option4.isEmpty()) {
            mOption4Et.setError("This filed should not be empty!");
            return;
        }

        List<String> options = new ArrayList<>();
        options.add(option1);
        options.add(option2);
        options.add(option3);
        options.add(option4);

        if (requestForEdit != null) {
            // for edit request
            question.setQuestionTitle(questionTitle);
            question.setOptions(options);
            question.setCorrectAns(mCorrectAns);
            questionViewModel.updateQuestion(question).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddQuestionActivity.this, "Question is Updated!",
                                Toast.LENGTH_SHORT).show();
                        //finish();

                        /*Intent myQuestionIntent = new Intent(AddQuestionActivity.this, MyQuestionActivity.class);
                        myQuestionIntent.putExtra("quizId", quizId);
                        myQuestionIntent.putExtra("totalQuestion", totalQuestion);
                        startActivity(myQuestionIntent);*/
                        onBackPressed();

                    } else {
                        Toast.makeText(AddQuestionActivity.this, "Question is not Updated! Something is wrong.",
                                Toast.LENGTH_SHORT).show();
                        /*Intent myQuestionIntent = new Intent(AddQuestionActivity.this, MyQuestionActivity.class);
                        myQuestionIntent.putExtra("quizId", quizId);
                        myQuestionIntent.putExtra("totalQuestion", totalQuestion);
                        startActivity(myQuestionIntent);*/

                        onBackPressed();
                    }
                }
            });
        } else {
            // for add request
            question = new Question(quizId, questionTitle, options, mCorrectAns);
            questionViewModel.addQuestion(question).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddQuestionActivity.this, "Question is Created!",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddQuestionActivity.this, "Question is not Created! Try again.",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
        }





    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    public void setData() {
        mQuestionTitleEt.setText(question.getQuestionTitle());
        mOption1Et.setText(question.getOptions().get(0));
        mOption2Et.setText(question.getOptions().get(1));
        mOption3Et.setText(question.getOptions().get(2));
        mOption4Et.setText(question.getOptions().get(3));

        for(int i = 0; i < mOptionsRg.getChildCount(); i++){

            ((RadioButton)mOptionsRg.getChildAt(i)).setChecked(true);

            if (question.getCorrectAns() == i) {
                mCorrectAns = i;
                break;
            }
        }

        /*if (question.getCorrectAns() == 0) {
            mop.setChecked(true);

        } else if(question.getCorrectAns() == 1) {
            holder.option2.setChecked(true);
        } else if(question.getCorrectAns() == 2) {
            holder.option3.setChecked(true);
        } else if(question.getCorrectAns() == 3) {
            holder.option4.setChecked(true);
        }*/
    }


}
