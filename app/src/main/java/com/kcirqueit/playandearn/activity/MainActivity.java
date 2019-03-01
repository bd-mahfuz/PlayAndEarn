package com.kcirqueit.playandearn.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.kcirqueit.playandearn.R;
import com.kcirqueit.playandearn.model.Participant;
import com.kcirqueit.playandearn.model.Question;
import com.kcirqueit.playandearn.model.Quiz;
import com.kcirqueit.playandearn.viewModel.ParticipantViewModel;
import com.kcirqueit.playandearn.viewModel.QuestionViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {




    @BindView(R.id.main_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.next_bt)
    AppCompatButton mNextBt;

    @BindView(R.id.question_title_tv)
    TextView mQTitleTv;

    @BindView(R.id.option1_tv)
    TextView mOption1Tv;

    @BindView(R.id.option2_tv)
    TextView mOption2Tv;

    @BindView(R.id.option3_tv)
    TextView mOption3Tv;

    @BindView(R.id.option4_tv)
    TextView mOption4Tv;

    @BindView(R.id.option1_card)
    CardView mOption1Card;

    @BindView(R.id.option2_card)
    CardView mOption2Card;

    @BindView(R.id.option3_card)
    CardView mOption3Card;

    @BindView(R.id.option4_card)
    CardView mOption4Card;

    private TextView mTextViewCountDown;

    private CountDownTimer mCountDownTimer;

    private long mTimeLeftInMillis;
    private long mEndTime;


    private Question currentQuestion;
    private int questionCount = 1;
    private int totalCorrectAns = 0;
    private int totalAnswered = 0;
    private boolean isFinished;

    private List<Question> questionList = new ArrayList<>();

    private QuestionViewModel mQuetionViewModel;
    private ParticipantViewModel mParticipantViewModel;
    private Quiz quiz;
    private Participant participant;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        quiz = (Quiz) getIntent().getSerializableExtra("quiz");
        participant = (Participant) getIntent().getSerializableExtra("participant");

        mQuetionViewModel = ViewModelProviders.of(this).get(QuestionViewModel.class);
        mParticipantViewModel = ViewModelProviders.of(this).get(ParticipantViewModel.class);

        mTextViewCountDown = findViewById(R.id.text_view_countdown);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(quiz.getQuizName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        long quizTime = Long.parseLong(quiz.getTimeLimit());
        mTimeLeftInMillis = quizTime;

        startTimer();

    }



    private void startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                updateParticipant();
                finish();
            }
        }.start();

    }

    private void updateCountDownText() {

        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        mTextViewCountDown.setText(timeLeftFormatted);
    }

    @Override
    protected void onStart() {
        super.onStart();

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        mQuetionViewModel.getAllQuestionsByQuizId(quiz.getId()).observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot questionSnapshot :  dataSnapshot.getChildren()) {
                        Question question = questionSnapshot.getValue(Question.class);
                        questionList.add(question);
                        //Log.d("quiz name:", quiz.getQuestionTitle());
                    }
                    setQuiz(questionList.get(0));
                    progressDialog.dismiss();

                } else {
                    progressDialog.dismiss();
                }
            }
        });

    }

    @OnClick(R.id.next_bt)
    public void onNextClick() {
        questionCount++;
        moveToNextQuiz();
        //Toast.makeText(this, "quiz list size:"+quizList.get(0).getCorrectAns(), Toast.LENGTH_SHORT).show();

    }


    public void setQuiz(Question question) {
        this.currentQuestion = question;
        mQTitleTv.setText(questionCount+". "+question.getQuestionTitle());
        mOption1Tv.setText(question.getOptions().get(0));
        mOption2Tv.setText(question.getOptions().get(1));
        mOption3Tv.setText(question.getOptions().get(2));
        mOption4Tv.setText(question.getOptions().get(3));
    }

    @OnClick(R.id.option1_card)
    public void onOption1Click() {
        totalAnswered ++;
        
        int selectedAnswer = 0;
        if (isCorrect(selectedAnswer)) {
            totalCorrectAns ++;

            // change background color green
            mOption1Card.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.rounded_green_transparent));
        } else {
            mOption1Card.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.rounded_red_transparent));
        }

        YoYo.with(Techniques.RubberBand)
                .duration(300)
                .repeat(1)
                .playOn(mOption1Card);

        mOption2Card.setClickable(false);
        mOption3Card.setClickable(false);
        mOption4Card.setClickable(false);
        mOption1Card.setClickable(false);

        mNextBt.setEnabled(true);
    }


    @OnClick(R.id.option2_card)
    public void onOption2Click() {
        totalAnswered ++;
        
        int selectedAnswer = 1;
        if (isCorrect(selectedAnswer)) {
            totalCorrectAns ++;

            // change background color green
            mOption2Card.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.rounded_green_transparent));
        } else {
            mOption2Card.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.rounded_red_transparent));
        }

        YoYo.with(Techniques.RubberBand)
                .duration(300)
                .repeat(1)
                .playOn(mOption2Card);

        mOption2Card.setClickable(false);
        mOption3Card.setClickable(false);
        mOption4Card.setClickable(false);
        mOption1Card.setClickable(false);

        mNextBt.setEnabled(true);

    }

    @OnClick(R.id.option3_card)
    public void onOption3Click() {
        totalAnswered ++;
        
        int selectedAnswer = 2;
        if (isCorrect(selectedAnswer)) {
            totalCorrectAns ++;

            // change background color green
            mOption3Card.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.rounded_green_transparent));
        } else {
            mOption3Card.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.rounded_red_transparent));
        }

        YoYo.with(Techniques.RubberBand)
                .duration(300)
                .repeat(1)
                .playOn(mOption3Card);

        mOption2Card.setClickable(false);
        mOption3Card.setClickable(false);
        mOption4Card.setClickable(false);
        mOption1Card.setClickable(false);

        mNextBt.setEnabled(true);

    }

    @OnClick(R.id.option4_card)
    public void onOption4Click() {
        totalAnswered ++;
        
        int selectedAnswer = 3;
        if (isCorrect(selectedAnswer)) {
            totalCorrectAns ++;

            // change background color green
            mOption4Card.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.rounded_green_transparent));
        } else {
            mOption4Card.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.rounded_red_transparent));
        }

        YoYo.with(Techniques.RubberBand)
                .duration(300)
                .repeat(1)
                .playOn(mOption4Card);

        mOption2Card.setClickable(false);
        mOption3Card.setClickable(false);
        mOption4Card.setClickable(false);
        mOption1Card.setClickable(false);

        mNextBt.setEnabled(true);



    }


    public boolean isCorrect(int selectedAns) {
        if (currentQuestion.getCorrectAns()==selectedAns) {
            return true;
        }
        return false;
    }


    public void moveToNextQuiz() {
        if (questionCount <= questionList.size()) {
            setQuiz(questionList.get(questionCount-1));
            reset();
            mNextBt.setEnabled(false);

        } else {
            mNextBt.setEnabled(true);
            mNextBt.setText("Finish");
           //Toast.makeText(this, "score"+ totalCorrectAns, Toast.LENGTH_SHORT).show();
            if (isFinished) {
                updateParticipant();
            }

            isFinished = true;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.wallet_menu:
                Intent walletIntent = new Intent(this, WalletActivity.class);
                startActivity(walletIntent);
                break;

        }

        return true;
    }
    
    public void updateParticipant() {
        int marksPerQuestion = quiz.getTotalMarks() / quiz.getTotalQuestion();
        participant.setScore(totalCorrectAns * marksPerQuestion);
        participant.setTotalAnswered(totalAnswered);
        participant.setTotalQuestion(quiz.getTotalQuestion());
        participant.setQuizName(quiz.getQuizName());
        participant.setTotalMarks(quiz.getTotalMarks());
        
        mParticipantViewModel.addParticipant(participant, quiz.getId()).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Intent resultIntent = new Intent(MainActivity.this, ResultActivity.class);
                    resultIntent.putExtra("quiz", quiz);
                    startActivity(resultIntent);
                    Toast.makeText(MainActivity.this, "You have successfully completed the quiz.",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    public void reset() {

        mOption3Card.setBackground(ContextCompat.getDrawable(this,
                R.drawable.rounded_white_tranparent));

        mOption2Card.setBackground(ContextCompat.getDrawable(this,
                R.drawable.rounded_white_tranparent));

        mOption4Card.setBackground(ContextCompat.getDrawable(this,
                R.drawable.rounded_white_tranparent));

        mOption1Card.setBackground(ContextCompat.getDrawable(this,
                R.drawable.rounded_white_tranparent));

        mOption1Card.setCardElevation(3);
        mOption2Card.setCardElevation(3);
        mOption3Card.setCardElevation(3);
        mOption4Card.setCardElevation(3);

        mOption2Card.setClickable(true);
        mOption3Card.setClickable(true);
        mOption4Card.setClickable(true);
        mOption1Card.setClickable(true);


    }

    @Override
    public void onBackPressed() {

        showWarningAlert();
        
    }


    public void showWarningAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("You are exiting the quiz.");
        builder.setIcon(R.drawable.ic_block_orange);
        builder.setMessage("Please do not exit the quiz, otherwise you will not get any marks.");
        builder.setPositiveButton("Exit Any Way", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.dismiss();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
