package com.kcirqueit.playandearn.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirqueit.playandearn.R;
import com.kcirqueit.playandearn.adapter.ParticipantAdapter;
import com.kcirqueit.playandearn.model.Participant;
import com.kcirqueit.playandearn.model.Quiz;
import com.kcirqueit.playandearn.viewModel.ParticipantViewModel;

import java.util.ArrayList;
import java.util.List;

public class ViewParticipantActivity extends AppCompatActivity implements ParticipantAdapter.ParticipantListener {

    private static final String TAG = "ViewParticipantActivity";


    @BindView(R.id.participant_rv)
    RecyclerView participantRv;

    @BindView(R.id.participant_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.no_participate_msg_tv)
    TextView noParticipantTv;

    private ParticipantViewModel participantViewModel;
    private DatabaseReference mUserRef;
    private List<Participant> participantList = new ArrayList<>();


    private Quiz quiz;
    private ParticipantAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_participant);

        participantViewModel = ViewModelProviders.of(this).get(ParticipantViewModel.class);

        quiz = (Quiz) getIntent().getSerializableExtra("quiz");
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Participants");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        participantRv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ParticipantAdapter(this, participantList);
        adapter.setParticipantListener(this);
        participantRv.setAdapter(adapter);
        participantRv.setHasFixedSize(true);



    }

    @Override
    protected void onStart() {
        super.onStart();

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading ...");
        progressDialog.show();

        participantViewModel.getAllParticipant(quiz.getId()).observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                participantList.clear();
                if (dataSnapshot.getValue() != null) {
                    noParticipantTv.setVisibility(View.GONE);

                    for (DataSnapshot participantSnapshot : dataSnapshot.getChildren()) {
                        Log.d("dsf", "onChanged: "+participantSnapshot);
                        Participant participant = participantSnapshot.getValue(Participant.class);
                        Log.d("ds", "onChanged: "+participant.getTotalMarks());
                        participantList.add(participant);
                        adapter.notifyDataSetChanged();


                    }
                    progressDialog.dismiss();

                } else {
                    progressDialog.dismiss();
                    noParticipantTv.setVisibility(View.VISIBLE);

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
    public void getParticipator(TextView textView, String userId) {

        mUserRef.child(userId).child("userName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue().toString();
                textView.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.toException().toString());
            }
        });

    }
}
