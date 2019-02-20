package com.kcirqueit.playandearn.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.kcirqueit.playandearn.R;
import com.kcirqueit.playandearn.adapter.DynamicFragmentAdapter;
import com.kcirqueit.playandearn.fragment.DynamicFragment;
import com.kcirqueit.playandearn.model.Quiz;
import com.kcirqueit.playandearn.viewModel.QuizViewModel;
import com.rilixtech.CountryCodePicker;

import java.util.ArrayList;
import java.util.List;

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


//    private Quiz currentQuiz;
//    private int quizCount = 1;
//
//    private List<Quiz> quizList = new ArrayList<>();

    private QuizViewModel mQuizViewModel;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mQuizViewModel = ViewModelProviders.of(this).get(QuizViewModel.class);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Play And Earn");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



    }

    @Override
    protected void onStart() {
        super.onStart();

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

       /* mQuizViewModel.getAllQuiz().observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                quizList.clear();
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot quizSnapshot :  dataSnapshot.getChildren()) {
                        Quiz quiz = quizSnapshot.getValue(Quiz.class);
                        quizList.add(quiz);
                        //Log.d("quiz name:", quiz.getQuestionTitle());
                    }
                    setFirstQuiz(quizList.get(0));
                    progressDialog.dismiss();

                } else {
                    progressDialog.dismiss();
                }
            }
        });*/

    }

    /*@OnClick(R.id.next_bt)
    public void onNextClick() {
        setFirstQuiz(quizList.get(quizCount));
        quizCount++;
        //Toast.makeText(this, "quiz list size:"+quizList.get(0).getCorrectAns(), Toast.LENGTH_SHORT).show();

    }


    public void setFirstQuiz(Quiz quiz) {
        this.currentQuiz = quiz;
        mQTitleTv.setText(quiz.getQuestionTitle());
        mOption1Tv.setText(quiz.getOptions().get(0));
        mOption2Tv.setText(quiz.getOptions().get(1));
        mOption3Tv.setText(quiz.getOptions().get(2));
        mOption4Tv.setText(quiz.getOptions().get(3));
    }*/

    @OnClick(R.id.option1_card)
    public void onOption1Click() {
        String selectedAnswer = "1";

        if (isCorrect(selectedAnswer)) {
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
    }


    @OnClick(R.id.option2_card)
    public void onOption2Click() {
        String selectedAnswer = "2";

        if (isCorrect(selectedAnswer)) {
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

    }

    @OnClick(R.id.option3_card)
    public void onOption3Click() {
        String selectedAnswer = "3";

        if (isCorrect(selectedAnswer)) {
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

    }

    @OnClick(R.id.option4_card)
    public void onOption4Click() {
        String selectedAnswer = "4";

        if (isCorrect(selectedAnswer)) {
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

    }


    public boolean isCorrect(String selectedAns) {
        if (/*currentQuiz.getCorrectAns()*/"".equals(selectedAns)) {
            return true;
        }
        return false;
    }


    public void moveToNextQuiz() {



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
}
