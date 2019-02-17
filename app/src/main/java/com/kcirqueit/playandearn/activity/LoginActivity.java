package com.kcirqueit.playandearn.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.kcirqueit.playandearn.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, DashBoardActivity.class));
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
