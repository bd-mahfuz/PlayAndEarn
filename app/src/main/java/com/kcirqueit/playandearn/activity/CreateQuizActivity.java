package com.kcirqueit.playandearn.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
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
import com.kcirqueit.playandearn.model.Quiz;
import com.kcirqueit.playandearn.viewModel.QuizViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateQuizActivity extends AppCompatActivity {

    @BindView(R.id.create_quiz_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.category_spinner)
    Spinner mCategorySpinner;

    @BindView(R.id.quiz_name_et)
    EditText mQuizNameEt;

    @BindView(R.id.time_limit_et)
    EditText mTimeLimitEt;

    @BindView(R.id.total_marks_et)
    EditText mTotalMarksEt;

    @BindView(R.id.total_question_et)
    EditText mTotalQuestionEt;

    private DatabaseReference mRootRef;
    private DatabaseReference mCategoryRef;
    private FirebaseAuth mAuth;
    String mCurrentUserId;

    private QuizViewModel quizViewModel;

    ProgressDialog mProgressDialog;

    private String requestForEdit;
    private Quiz quiz;

    String timeLimit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);

        // getting data from intent
        requestForEdit = getIntent().getStringExtra("requestForEdit");
        quiz = (Quiz) getIntent().getSerializableExtra("quiz");

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        ButterKnife.bind(this);

        quizViewModel = ViewModelProviders.of(this).get(QuizViewModel.class);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mCategoryRef = mRootRef.child("QuizCategory");

        String toolbarTitle = "Create Quiz";
        if (requestForEdit != null && quiz != null) {
            toolbarTitle = "Edit Quiz";

            setDataForEdit();
        }

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(toolbarTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }


    @OnClick(R.id.time_limit_et)
    public void pickTime() {
        int hour = 00;
        int minute = 30;
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                long timeInMilli = (selectedHour * 3600000) + (selectedMinute * 60 * 1000);
                timeLimit = String.valueOf(timeInMilli);
                mTimeLimitEt.setText( selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        List<String> categoryList = new ArrayList<>();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.show();
        mCategoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    for (DataSnapshot catSnapshot : dataSnapshot.getChildren()) {
                        categoryList.add(catSnapshot.child("name").getValue()+"");
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateQuizActivity.this,
                            android.R.layout.simple_spinner_item,
                            categoryList
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mCategorySpinner.setAdapter(adapter);
                    mProgressDialog.dismiss();
                } else {
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("database error:", databaseError.toException().toString());
                mProgressDialog.dismiss();
            }
        });

    }

    @OnClick(R.id.create_quiz_bt)
    public void onClickCreateQuizBt() {

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please wait until the quiz is created.");

        String quizName = mQuizNameEt.getText().toString();
        String quizCategory = mCategorySpinner.getSelectedItem().toString();
        String timeLimit = mTimeLimitEt.getText().toString();


        if (quizName.isEmpty()) {
            mQuizNameEt.setError("Please enter the quiz name!");
            return;
        } else if (timeLimit.isEmpty()) {
            mTimeLimitEt.setError("Please enter time limit for quiz!");
            return;
        } else if (mTotalMarksEt.getText().toString().isEmpty()) {
            mTimeLimitEt.setError("Please enter total marks for quiz!");
            return;
        } else if (mTotalQuestionEt.getText().toString().isEmpty()) {
            mTimeLimitEt.setError("Please enter total question for quiz!");
            return;
        }

        int totalMarks = Integer.parseInt(mTotalMarksEt.getText().toString());
        int totalQuestion = Integer.parseInt(mTotalQuestionEt.getText().toString());

        if (requestForEdit == null) {
            // create new quiz
            Quiz quiz = new Quiz(mCurrentUserId, quizName, quizCategory, totalMarks, totalQuestion, this.timeLimit);

            mProgressDialog.show();
            quizViewModel.addQuiz(quiz).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {

                        Toast.makeText(CreateQuizActivity.this, "Quiz is successfully created!",
                                Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                        finish();

                    } else {
                        Toast.makeText(CreateQuizActivity.this, "Quiz is not created!",
                                Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                    }
                }
            });
        } else {
            // update existing quiz
            quiz.setQuizName(quizName);
            quiz.setCat_id(quizCategory);
            quiz.setTotalMarks(totalMarks);
            quiz.setTotalQuestion(totalQuestion);
            quiz.setTimeLimit(this.timeLimit);

            mProgressDialog.show();
            quizViewModel.updateQuiz(quiz).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(CreateQuizActivity.this, "Quiz updated Successfully.",
                                Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                        finish();
                    }else {
                        Toast.makeText(CreateQuizActivity.this, "Something went wrong. Quiz is not updated",
                                Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                    }
                }
            });
        }

    }


    private void setDataForEdit() {

        mQuizNameEt.setText(quiz.getQuizName());
        mTimeLimitEt.setText(quiz.getTimeLimit());
        mTotalMarksEt.setText(quiz.getTotalMarks()+"");
        mTotalQuestionEt.setText(quiz.getTotalQuestion()+"");

        mCategorySpinner.setSelection(getSpinnerIndex());

    }

    public int getSpinnerIndex() {
        int index = 0;
        for (int i = 0; i < mCategorySpinner.getCount(); i++) {
            if (mCategorySpinner.getItemAtPosition(i).equals(quiz.getCat_id())){
                index = i;
                Log.d("index:", index+"");
                break;
            }
        }

        return index;
    }

}
