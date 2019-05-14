package com.kcirqueit.playandearn.fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirqueit.playandearn.R;
import com.kcirqueit.playandearn.activity.CreateQuizActivity;
import com.kcirqueit.playandearn.activity.FragmentContainerActivity;
import com.kcirqueit.playandearn.activity.ViewParticipantActivity;
import com.kcirqueit.playandearn.adapter.MyQuizAdapter;
import com.kcirqueit.playandearn.adapter.QuizAdapter;
import com.kcirqueit.playandearn.model.Quiz;
import com.kcirqueit.playandearn.services.FirebaseApi;
import com.kcirqueit.playandearn.services.FirebaseClient;
import com.kcirqueit.playandearn.utility.InternetConnection;
import com.kcirqueit.playandearn.viewModel.QuestionViewModel;
import com.kcirqueit.playandearn.viewModel.QuizViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyQuizFragmnet extends Fragment implements MyQuizAdapter.OnCreateQuizListener {

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

    private AdView mAdView;


    private DatabaseReference mQuestionRef = FirebaseDatabase.getInstance().getReference().child("Questions");
    private DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");


    public MyQuizFragmnet() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (FragmentContainerActivity) getActivity();

        MobileAds.initialize(activity,
                getString(R.string.admob_app_id));

        setHasOptionsMenu(true);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        quizViewModel = ViewModelProviders.of(this).get(QuizViewModel.class);
        questionViewModel = ViewModelProviders.of(this).get(QuestionViewModel.class);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_quiz_fragmnet, container, false);
        ButterKnife.bind(this, view);

        activity.getSupportActionBar().setCustomView(null);
        activity.getSupportActionBar().setTitle("My Quiz");

        mQuizRv.setLayoutManager(new LinearLayoutManager(activity));

        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Toast.makeText(activity, mAuth.getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();

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
                        Collections.reverse(quizzes);
                        mNoQuizLayout.setVisibility(View.GONE);
                        mQuizAdapter = new MyQuizAdapter(activity, quizzes);
                        mQuizAdapter.setCreateQuizListener(MyQuizFragmnet.this);
                        mQuizRv.setAdapter(mQuizAdapter);

                    } else {
                        mNoQuizLayout.setVisibility(View.VISIBLE);
                    }
                    progressDialog.dismiss();
                } else {
                    mNoQuizLayout.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                }
            }
        });

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        menu.clear();

        inflater.inflate(R.menu.myquiz_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.search_menu);

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText != null) {
                    mQuizAdapter.getFilter().filter(newText);
                }

                return true;
            }
        });


        super.onCreateOptionsMenu(menu, inflater);
    }


    public void showDeleteDialog(String quizId, Quiz quiz) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Warning");
        builder.setIcon(R.drawable.ic_warning_red);
        builder.setMessage("All questions will also be delete with this quiz.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                deleteQuiz(quizId, quiz);
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

    public void deleteQuiz(String quizId, Quiz quiz) {
        // delete the quiz
        quizViewModel.deleteQuiz(quiz)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {

                            //checking in question ref the quiz is exist or not
                            mQuestionRef.child(quizId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() != null) {
                                        // delete all question related to that quiz
                                        questionViewModel.deleteAllQuestion(quizId)
                                                .addOnCompleteListener(new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(activity, "Successfully deleted", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(activity, "Question is Not deleted", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(activity, "Successfully deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.d("My Quiz Adapter", databaseError.toException().toString());
                                }
                            });
                        } else {
                            Toast.makeText(activity, "Quiz is not deleted", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


    public void showPublishedDialog(Quiz quiz) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Warning");
        builder.setIcon(R.drawable.ic_warning_red);
        builder.setMessage("Please review your quiz and question information. After publish you can't edit or delete them.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mQuestionRef.child(quiz.getId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            int count = 0;
                            for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
                                count++;
                            }

                            if (count == quiz.getTotalQuestion()) {
                                publishQuiz(quiz);
                            } else {
                                Toast.makeText(activity, "You added " + count + " question. You have to add total " + quiz.getTotalQuestion() + " Question.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(activity, "You did't create any question for this quiz. Plz add all question first.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("MyQuiz Adapter", databaseError.toException().toString());
                    }
                });

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


    public void publishQuiz(Quiz quiz) {

        quiz.setStatus("published");
        quizViewModel.updateQuiz(quiz).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {

                    sendNotification();

                    Toast.makeText(activity, "Successfully published.", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(activity, "Quiz is not published. Something wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendNotification() {

        Retrofit retrofit = FirebaseClient.getInstance();

        FirebaseApi api = retrofit.create(FirebaseApi.class);

        mUserRef.child(mAuth.getCurrentUser().getUid()).child("userName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                String name = dataSnapshot.getValue().toString();

                Call<ResponseBody> call = api.sendRequest("quiz", "Quiz app notification",
                        "New Quiz is published by " + name);

                call.enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Log.d("MyQuizFragment", response.body().toString());
                        } else {
                            try {
                                Log.e("My quiz adapter", "onResponse: " + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException();
            }
        });


    }


    public void popupClick(ImageView popupIv, Quiz quiz) {

        PopupMenu popupMenu = new PopupMenu(activity, popupIv);
        Menu menu = popupMenu.getMenu();
        menu.add("Edit");
        menu.add("Delete");
        menu.add("View Participants");
        menu.add("Publish Quiz");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getTitle().equals("Edit")) {

                    if (!isPublished(quiz)) {
                        if (InternetConnection.checkConnection(activity)) {
                            Intent createQuizIntent = new Intent(activity, CreateQuizActivity.class);
                            createQuizIntent.putExtra("requestForEdit", "true");
                            createQuizIntent.putExtra("quiz", quiz);
                            activity.startActivity(createQuizIntent);
                        } else {
                            InternetConnection.showNoInternetDialog(activity);
                        }


                    } else {
                        Toast.makeText(activity, "Quiz is already Publish. You Can't Edit the Quiz", Toast.LENGTH_SHORT).show();
                    }

                } else if (item.getTitle().equals("Delete")) {

                    if (!isPublished(quiz)) {
                        String quizId = quiz.getId();
                        if (InternetConnection.checkConnection(activity)) {
                            showDeleteDialog(quizId, quiz);
                        } else {
                            InternetConnection.showNoInternetDialog(activity);
                        }

                    } else {
                        sendNotification();
                        Toast.makeText(activity, "Quiz is already Publish. You Can't Delete the Quiz", Toast.LENGTH_SHORT).show();
                    }

                } else if (item.getTitle().equals("View Participants")) {

                    // goto ViewParticipantActivity
                    gotoParticipanteActivity(quiz);

                } else if (item.getTitle().equals("Publish Quiz")) {
                    if (isPublished(quiz)) {
                        Toast.makeText(activity, "Quiz is Already Published.", Toast.LENGTH_SHORT).show();
                    } else {

                        if (InternetConnection.checkConnection(activity)) {
                            showPublishedDialog(quiz);
                        } else {
                            InternetConnection.showNoInternetDialog(activity);
                        }
                    }
                }

                return true;
            }
        });

        popupMenu.show();
    }

    public boolean isPublished(Quiz quiz) {
        if (quiz.getStatus().equals("un-published")) {
            return false;
        }
        return true;
    }

    public void gotoParticipanteActivity(Quiz quiz) {

        Intent participantIntent = new Intent(activity, ViewParticipantActivity.class);
        participantIntent.putExtra("quiz", quiz);
        activity.startActivity(participantIntent);
    }

    @Override
    public void onPopupClick(Quiz quiz, ImageView popupIv) {
        popupClick(popupIv, quiz);
    }
}
