package com.kcirqueit.playandearn.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.kcirqueit.playandearn.R;
import com.kcirqueit.playandearn.sharedPreference.MySharedPreference;
import com.kcirqueit.playandearn.utility.InternetConnection;
import com.rilixtech.CountryCodePicker;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    @BindView(R.id.login_bt)
    AppCompatButton mLoginBt;

    private FirebaseAuth mAuth;

    @BindView(R.id.terms_check_box)
    AppCompatCheckBox mTermsCheckBox;

    @BindView(R.id.phone_et)
    AppCompatEditText mPhoneEt;

    @BindView(R.id.ccp)
    CountryCodePicker mCountryCodePicker;

    private MySharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

/*
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });*/


        mAuth = FirebaseAuth.getInstance();
        sharedPreference = MySharedPreference.getInstance(this);

        if (InternetConnection.checkConnection(this)) {
            if (mAuth.getCurrentUser() != null) {
                String profileCreated = sharedPreference.getData("profileCreated");
                if (profileCreated.equals("true")) {
                    startActivity(new Intent(this, FragmentContainerActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(this, UserInfoActivity.class));
                    finish();
                }

            }
        } else {
            InternetConnection.showNoInternetDialog(this);
        }

        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        mCountryCodePicker.registerPhoneNumberTextView(mPhoneEt);

        mTermsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mLoginBt.setEnabled(true);
                } else {
                    mLoginBt.setEnabled(false);
                }
            }
        });

    }


    @OnClick(R.id.login_bt)
   public void onLoginClick() {
        performLogin();
   }

    public void performLogin() {

        String code = mCountryCodePicker.getSelectedCountryCodeWithPlus();

        String countryName = mCountryCodePicker.getSelectedCountryName();

        String number = mPhoneEt.getText().toString();

        if (number.isEmpty() || number.length() < 10) {
            mPhoneEt.setError("Phone number is not valid!");
            mPhoneEt.requestFocus();
            return;
        }

        String phoneNumber = code + number;

        Intent verifyIntent = new Intent(this, VerificationActivity.class);
        verifyIntent.putExtra("phoneNumber", phoneNumber);
        verifyIntent.putExtra("country", countryName);
        startActivity(verifyIntent);

        finish();

    }
}
