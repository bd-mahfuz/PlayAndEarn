package com.kcirqueit.playandearn.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
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
import com.kcirqueit.playandearn.activity.DashBoardActivity;
import com.kcirqueit.playandearn.activity.FragmentContainerActivity;
import com.kcirqueit.playandearn.activity.InstructionActivity;
import com.kcirqueit.playandearn.activity.NotificationActivity;
import com.kcirqueit.playandearn.activity.ProfileActivity;
import com.kcirqueit.playandearn.activity.ResultActivity;
import com.kcirqueit.playandearn.adapter.QuizAdapter;
import com.kcirqueit.playandearn.model.Quiz;
import com.kcirqueit.playandearn.model.User;
import com.kcirqueit.playandearn.services.FirebaseApi;
import com.kcirqueit.playandearn.services.FirebaseClient;
import com.kcirqueit.playandearn.utility.InternetConnection;
import com.kcirqueit.playandearn.viewModel.QuizViewModel;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment implements SearchView.OnQueryTextListener, QuizAdapter.OnItemClickListener {


    private static final String TAG = "DashboardFragment";

    @BindView(R.id.quiz_rv)
    RecyclerView mQuizRv;

    @BindView(R.id.quiz_search_view)
    SearchView mQuizSearchView;

    @BindView(R.id.no_quiz_Tv)
    TextView mNoQuizMsg;

    @BindView(R.id.filter_iv)
    ImageView mFilterIv;

    private PopupMenu popupMenu;

    private CircleImageView mUserIv;

    private QuizViewModel quizViewModel;

    private List<Quiz> quizzes = new ArrayList<>();

    private QuizAdapter mQuizAdapter;

    private FragmentContainerActivity activity;


    private DatabaseReference mBookMarkRef;
    private DatabaseReference mParticipantRef;
    private DatabaseReference mQuizRef;
    private DatabaseReference mUserRef;

    private FirebaseAuth mAuth;

    private User user;

    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        activity = (FragmentContainerActivity) getActivity();

        mAuth = FirebaseAuth.getInstance();
        mParticipantRef = FirebaseDatabase.getInstance().getReference().child("Participants");
        mBookMarkRef = FirebaseDatabase.getInstance().getReference().child("BookMarks");
        mQuizRef = FirebaseDatabase.getInstance().getReference().child("Quizes");
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        ButterKnife.bind(this, view);
        quizViewModel = ViewModelProviders.of(this).get(QuizViewModel.class);


        mQuizRv.setLayoutManager(new LinearLayoutManager(activity));

        mQuizSearchView.setOnQueryTextListener(this);


        return view;
    }

    @OnClick(R.id.create_quiz_layout)
    public void onClickCreateQuiz() {

        startActivity(new Intent(activity, CreateQuizActivity.class));
    }

    @OnClick(R.id.my_quiz_layout)
    public void onClickMyQuizLayout() {
        replaceFragment(new MyQuizFragmnet());
    }

    @OnClick(R.id.bookmark_layout)
    public void onClickBookmarkLayout() {
        replaceFragment(new BookmarkFragment());
    }

    @OnClick(R.id.result_layout)
    public void onClickResultLayout() {
        replaceFragment(new MyResultFragment());
    }

    @OnClick(R.id.achievement_layout)
    public void onClickAchivementLayout() {
        replaceFragment(new AchivementFragment());
    }

    @OnClick(R.id.all_quiz_layout)
    public void onClickAllQuizLayout() {
        replaceFragment(new AllQuizFragment());
    }


    @Override
    public void onStart() {
        super.onStart();

        setToolbar();

        setPopUpMenu();

        mUserRef.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    user = dataSnapshot.getValue(User.class);
                    if (!user.getPhotoUrl().equals("default")) {
                        Picasso.get().load(user.getPhotoUrl())
                                .placeholder(R.drawable.user_avater).into(mUserIv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.toException().toString());
            }
        });


        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        mQuizRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                quizzes.clear();
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot quizSnapshot : dataSnapshot.getChildren()) {
                        Quiz quiz = quizSnapshot.getValue(Quiz.class);
                        if (quiz.getStatus().equals("published")) {
                            quizzes.add(quiz);
                        }

                    }

                    if (quizzes.size() > 0) {
                        Collections.reverse(quizzes);
                        mNoQuizMsg.setVisibility(View.GONE);
                        mQuizAdapter = new QuizAdapter(activity, quizzes);
                        mQuizAdapter.setItemClickListener(DashboardFragment.this);
                        mQuizRv.setAdapter(mQuizAdapter);

                    } else {
                        mNoQuizMsg.setVisibility(View.VISIBLE);
                    }
                    progressDialog.dismiss();
                } else {
                    mNoQuizMsg.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*quizViewModel.getAllQuiz().observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {

            }
        });
*/

        mFilterIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (popupMenu != null) {
                    popupMenu.show();
                }

            }
        });


    }

    public void setPopUpMenu() {
        popupMenu = new PopupMenu(activity, mFilterIv);
        Menu menu = popupMenu.getMenu();
        menu.add("All");

        System.out.println("called");

        DatabaseReference quizCateRef = FirebaseDatabase.getInstance().getReference().child("QuizCategory");
        quizCateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        String catName = dataSnapshot1.child("name").getValue().toString();
                        menu.add(catName);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Error:", databaseError.toException().toString());
            }

        });


        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getTitle().toString()) {
                    case "All":
                        filterByCat("All");
                        break;
                    case "General knowledge":
                        filterByCat("General knowledge");
                        break;
                    case "Science":
                        filterByCat("Science");
                        break;
                    case "Business":
                        filterByCat("Business");
                        break;
                    case "English":
                        filterByCat("English");
                        break;
                    case "Others":
                        filterByCat("Others");
                        break;

                }

                return true;

            }
        });


    }

    public void filterByCat(String category) {

        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        mQuizRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                quizzes.clear();
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot quizSnapshot : dataSnapshot.getChildren()) {
                        Quiz quiz = quizSnapshot.getValue(Quiz.class);

                        if (category.equals("All")) {

                            if (quiz.getStatus().equals("published")) {
                                quizzes.add(quiz);
                            }

                        } else {
                            if (quiz.getStatus().equals("published") && quiz.getCat_id().equals(category)) {
                                quizzes.add(quiz);
                            }
                        }

                    }

                    if (quizzes.size() > 0) {
                        mNoQuizMsg.setVisibility(View.GONE);
                        mQuizRv.setVisibility(View.VISIBLE);

                        mQuizAdapter.notifyDataSetChanged();

                    } else {
                        mNoQuizMsg.setVisibility(View.VISIBLE);
                        mQuizRv.setVisibility(View.GONE);
                    }
                    progressDialog.dismiss();
                } else {
                    mNoQuizMsg.setVisibility(View.VISIBLE);
                    mQuizRv.setVisibility(View.GONE);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException();
            }
        });

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (mQuizAdapter != null && newText != null) {
            mQuizAdapter.getFilter().filter(newText);
        }
        return true;

    }


    public void replaceFragment(Fragment fragment) {
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onBookmarkClick(Quiz quiz) {

        Map map = new HashMap();
        map.put("quizId", quiz.getId());
        map.put("quizName", quiz.getQuizName());

        if (InternetConnection.checkConnection(activity)) {
            mBookMarkRef.child(mAuth.getCurrentUser().getUid()).child(quiz.getId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.getValue() != null) {
                                Toast.makeText(activity, "Bookmarked already added.", Toast.LENGTH_SHORT).show();
                            } else {
                                mBookMarkRef.child(mAuth.getCurrentUser().getUid()).child(quiz.getId())
                                        .setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(activity, "Bookmark is added.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(activity, "Something went wrong. Bookmark is not added.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(activity, "Something is wrong please contact with developer.", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, databaseError.toException().toString());
                        }
                    });

        } else {
            InternetConnection.showNoInternetDialog(activity);
        }
    }

    @Override
    public void onItemClick(Quiz quiz) {
        if (InternetConnection.checkConnection(activity)) {
            if (mAuth.getCurrentUser().getUid().equals(quiz.getUserId())) {

                Toast.makeText(activity, "You can't participate your own created quiz.", Toast.LENGTH_SHORT).show();

            } else {

                mParticipantRef.child(quiz.getId())
                        .child(mAuth.getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {

                                    Intent resultIntent = new Intent(activity, ResultActivity.class);
                                    //Log.d("quiz name:", quiz.getQuizName());
                                    resultIntent.putExtra("quizId", quiz.getId());
                                    startActivity(resultIntent);

                                } else {
                                    Intent instructionIntent = new Intent(activity, InstructionActivity.class);
                                    instructionIntent.putExtra("quiz", quiz);
                                    startActivity(instructionIntent);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d("Quiz adapter:", databaseError.toException().toString());
                            }
                        });

            }
        } else {
            InternetConnection.showNoInternetDialog(activity);
        }
    }

    @Override
    public void getBookmarkData(Quiz quiz, QuizAdapter.ViewHolder viewHolder) {

        if (InternetConnection.checkConnection(activity)) {
            mBookMarkRef.child(mAuth.getCurrentUser().getUid()).child(quiz.getId())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                viewHolder.bookmarkIv.setImageDrawable(ContextCompat.getDrawable(activity,
                                        R.drawable.ic_bookmark_fill_orange));
                            } else {
                                viewHolder.bookmarkIv.setImageDrawable(ContextCompat.getDrawable(activity,
                                        R.drawable.ic_bookmark_border_orange));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d(TAG, databaseError.toException().toString());
                        }
                    });
        } else {
            InternetConnection.showNoInternetDialog(activity);
        }

    }

    @Override
    public void getCreatorName(String userId, TextView creatorTv) {

        if (mAuth.getUid().equals(user)) {
            creatorTv.setText("Creator: You");
        } else {
            mUserRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        String userName = dataSnapshot.child("userName").getValue().toString();
                        //Log.d("user name:", userName);
                        creatorTv.setText("Creator: " + userName);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, databaseError.toException().toString());
                }
            });
        }


    }

    public void setToolbar() {
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Dashboard");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            View view = LayoutInflater.from(activity).inflate(R.layout.profile_toolbar, null);
            mUserIv = view.findViewById(R.id.profile_iv);
            ImageView notificationIv = view.findViewById(R.id.notification_id);
            actionBar.setCustomView(view);
            mUserIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent profileIntent = new Intent(activity, ProfileActivity.class);
                    startActivity(profileIntent);
                }
            });
            
            notificationIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent notificationIntent = new Intent(activity, NotificationActivity.class);
                    startActivity(notificationIntent);
                }
            });

        }
    }
}
