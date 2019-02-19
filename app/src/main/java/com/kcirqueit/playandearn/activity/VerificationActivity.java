package com.kcirqueit.playandearn.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.kcirqueit.playandearn.R;
import com.kcirqueit.playandearn.sharedPreference.MySharedPreference;
import com.kcirqueit.playandearn.viewModel.UserViewModel;

import java.util.concurrent.TimeUnit;

public class VerificationActivity extends AppCompatActivity {

    private static final String TAG = "VerificationActivity";

    private ProgressDialog mProgressDialog;

    @BindView(R.id.code_et)
    EditText mCodeEt;

    private String mVerificationId;
    private FirebaseAuth mAuth;

    private String mPhoneNumber;
    private String mCountryName;

    private String mCurrentUseId;

    private MySharedPreference sharedPreference;

    private UserViewModel mUserViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        mAuth = FirebaseAuth.getInstance();

        ButterKnife.bind(this);
        sharedPreference = MySharedPreference.getInstance(this);

        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        mPhoneNumber = getIntent().getStringExtra("phoneNumber");
        mCountryName = getIntent().getStringExtra("country");

        Log.d("Phone number:", mPhoneNumber);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please wait ...");

        sendVerificationCode(mPhoneNumber);

    }

    @Override
    protected void onResume() {
        super.onResume();

        mProgressDialog.show();

    }

    @OnClick(R.id.submit_bt)
    public void submitOnClick() {

        String code = mCodeEt.getText().toString().trim();

        if (code.isEmpty() || code.length() < 6) {
            mCodeEt.setError("Enter verification code");
            mCodeEt.requestFocus();
            return;
        }
        mProgressDialog.show();
        verifyCode(code);
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            mUserViewModel.getUserById(mAuth.getCurrentUser().getUid()).observe(VerificationActivity.this, new Observer<DataSnapshot>() {
                                @Override
                                public void onChanged(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() != null) {
                                        Intent dashIntent = new Intent(VerificationActivity.this, DashBoardActivity.class);
                                        startActivity(dashIntent);
                                        mProgressDialog.dismiss();
                                        finish();
                                    } else {

                                        Intent mainIntent = new Intent(VerificationActivity.this, UserInfoActivity.class);
                                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        mainIntent.putExtra("phoneNumber", mPhoneNumber);
                                        mainIntent.putExtra("country", mCountryName);
                                        sharedPreference.saveData("phoneNumber", mPhoneNumber);
                                        sharedPreference.saveData("country", mCountryName);
                                        startActivity(mainIntent);
                                        mProgressDialog.dismiss();
                                        finish();

                                    }
                                }
                            });

                        } else {
                            Toast.makeText(VerificationActivity.this,
                                    task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();
                        }
                    }
                });
    }

    public void sendVerificationCode(String number) {
        Log.d("VerificationCode meth:", "called ");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                this,
                mCallBack
        );


    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    mVerificationId = s;
                    Log.d("VerificationId:", mVerificationId);
                }

                @Override
                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                    String code = phoneAuthCredential.getSmsCode();
                    Log.d("verification code:", code + "");
                    if (code != null) {
                        mCodeEt.setText(code);
                        mProgressDialog.dismiss();
                        //verifyCode(code);
                    }

                }

                @Override
                public void onVerificationFailed(FirebaseException e) {

                    Toast.makeText(VerificationActivity.this, e.getMessage() + "",
                            Toast.LENGTH_SHORT).show();

                }
            };

}
