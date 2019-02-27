package com.kcirqueit.playandearn.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.kcirqueit.playandearn.R;
import com.kcirqueit.playandearn.model.Participant;
import com.kcirqueit.playandearn.model.Quiz;
import com.kcirqueit.playandearn.utility.DateUtility;
import com.kcirqueit.playandearn.utility.InternetConnection;
import com.kcirqueit.playandearn.viewModel.ParticipantViewModel;

public class InstructionActivity extends AppCompatActivity {

    @BindView(R.id.start_quiz_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.i_quiz_name_tv)
    TextView mQuiznameTv;

    @BindView(R.id.i_time_limit_tv)
    TextView mTimeLimitTv;

    @BindView(R.id.i_total_marks_tv)
    TextView mTotalMarksTv;

    @BindView(R.id.i_total_q_tv)
    TextView mTotalQuestionTv;

    @BindView(R.id.i_total_participator)
    TextView mTotalParticipator;

    private Quiz quiz;

    private ParticipantViewModel participantViewModel;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
        mAuth = FirebaseAuth.getInstance();
        participantViewModel = ViewModelProviders.of(this).get(ParticipantViewModel.class);

        ButterKnife.bind(this);

        quiz = (Quiz) getIntent().getSerializableExtra("quiz");
        if(quiz != null) {
            setData();
        }

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Start Quiz");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void setData() {

        mQuiznameTv.setText("Quiz Name: "+quiz.getQuizName());
        mTimeLimitTv.setText("Time Limit: "+ DateUtility.milliToHour(Long.parseLong(quiz.getTimeLimit())) +" hours");
        mTotalMarksTv.setText("Total Marks: "+String.valueOf(quiz.getTotalMarks()));
        mTotalQuestionTv.setText("Total No. of Questions: "+String.valueOf(quiz.getTotalQuestion()));

    }

    @OnClick(R.id.start_quiz_bt)
    public void onStartQuizClick() {

        if (InternetConnection.checkConnection(this)) {
            showStartDialog();
        } else {
            InternetConnection.showNoInternetDialog(this);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        participantViewModel.getAllParticipant(quiz.getId()).observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    int count = 0;
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        count ++;
                    }

                    mTotalParticipator.setText("Total Participator Until Now: "+count);
                    progressDialog.dismiss();

                } else {
                    mTotalParticipator.setText("Total Participator Until Now: 0");
                    progressDialog.dismiss();
                }
            }
        });

    }

    public void showStartDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setIcon(R.drawable.ic_info_outline_orange);
        builder.setMessage("Are you read instructions carefully?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                addParticipant();
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

    public void gotoMainActivity(Participant participant) {
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.putExtra("quiz", quiz);
        mainIntent.putExtra("participant", participant);
        startActivity(mainIntent);
    }

    public void addParticipant() {

        Participant participant = new Participant("true", 0, 0, 0);
        participant.setId(mAuth.getCurrentUser().getUid());
        participantViewModel.addParticipant(participant, quiz.getId())
                .addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    gotoMainActivity(participant);
                    finish();
                } else {
                    Toast.makeText(InstructionActivity.this,
                            "Something went Wrong. Plz contact with your admin.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return true;
    }
}
