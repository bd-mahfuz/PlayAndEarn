package com.kcirqueit.playandearn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Retrofit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kcirqueit.playandearn.activity.FragmentContainerActivity;
import com.kcirqueit.playandearn.activity.LoginActivity;
import com.kcirqueit.playandearn.activity.MainActivity;
import com.kcirqueit.playandearn.activity.UserInfoActivity;
import com.kcirqueit.playandearn.utility.InternetConnection;

public class LoginActivity2 extends AppCompatActivity {

    private static final String TAG = LoginActivity2.class.getSimpleName();

    private TextView signupTv;

    private TextInputLayout mEmailEt, mPasswordEt;

    private AppCompatButton loginBt;


    private FirebaseAuth mAuth;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    String allQuizFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // data will coming from myService (on notification click)
        allQuizFragment = getIntent().getStringExtra("allQuizFragment");

        FirebaseMessaging.getInstance().subscribeToTopic("quiz").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("fajhiehf", "successfull");
                } else {
                    Log.d("fajhiehf", "failed");
                }
            }
        });


        setContentView(R.layout.activity_login2);

        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        progressBar.setVisibility(View.GONE);

        if (InternetConnection.checkConnection(this)) {
            if (mAuth.getCurrentUser() != null) {
                Intent dashIntent = new Intent(this, FragmentContainerActivity.class);
                if (allQuizFragment != null) {
                    dashIntent.putExtra("allQuizFragment", "allQuizFragment");
                }
                startActivity(dashIntent);

                finish();
            }
        } else {
            InternetConnection.showNoInternetDialog(this);
        }

        initView();

    }

    private void initView() {
        signupTv = findViewById(R.id.register_tv);
        mEmailEt = findViewById(R.id.email_et);
        mPasswordEt = findViewById(R.id.password_ti);
        loginBt = findViewById(R.id.login_bt);
    }

    public void performSignIn(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithEmail:success");

                            Intent dashIntent = new Intent(LoginActivity2.this, FragmentContainerActivity.class);
                            if (allQuizFragment != null) {
                                dashIntent.putExtra("allQuizFragment", "allQuizFragment");
                            }
                            startActivity(dashIntent);
                            progressBar.setVisibility(View.GONE);
                            finish();


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity2.this, "Authentication failed. Try again.",
                                    Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        signupTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginActivity2.this, UserInfoActivity.class));

            }
        });

        loginBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmailEt.getEditText().getText().toString();
                String pass = mPasswordEt.getEditText().getText().toString();

                if (email.isEmpty()) {
                    mEmailEt.setError("Email should not be empty");
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mEmailEt.setError("Email is not valid");
                    return;
                } else if (pass.isEmpty() || pass.length() < 6) {
                    mPasswordEt.setError("Password should not be empty or less than 6 char.");
                    return;
                }

                performSignIn(email, pass);

            }
        });



    }
}
