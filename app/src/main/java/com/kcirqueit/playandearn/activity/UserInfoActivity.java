package com.kcirqueit.playandearn.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kcirqueit.playandearn.R;
import com.kcirqueit.playandearn.model.User;
import com.kcirqueit.playandearn.sharedPreference.MySharedPreference;
import com.kcirqueit.playandearn.viewModel.UserViewModel;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class UserInfoActivity extends AppCompatActivity {

    private static final String TAG = "UserInfoActivity";

    @BindView(R.id.white_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.user_name_et)
    EditText mUserNameEt;

    @BindView(R.id.p_email_et)
    EditText mEmailEt;

    @BindView(R.id.u_dob_et)
    EditText mDobEt;

    @BindView(R.id.u_pass_et)
    EditText mPassEt;

    @BindView(R.id.gender_rg)
    RadioGroup mGenderRg;

    @BindView(R.id.male_rb)
    RadioButton mMaleRadioBt;

    @BindView(R.id.female_rb)
    RadioButton mFemaleRadioBt;

    @BindView(R.id.others_rb)
    RadioButton mOthersRadioBt;

    @BindView(R.id.user_iv)
    CircleImageView mUserIv;

    private ProgressDialog mProgressDialog;


    private String mGender = "Male";

    private Calendar mCalendar;

   /* private String mPhoneNumber;
    private String mCountryName;*/

    private UserViewModel mUserViewModel;

    private static final int GALLERY_REQUEST_CODE = 1;

    StorageReference rootStorageRef;

    private FirebaseAuth mAuth;


    private Uri mResultUri;
    private byte[] thumbByte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        ButterKnife.bind(this);


        /*mPhoneNumber = getIntent().getStringExtra("phoneNumber");
        mCountryName = getIntent().getStringExtra("country");
        if ((mPhoneNumber == null || mPhoneNumber.isEmpty())|| (mCountryName == null || mCountryName.isEmpty())) {

            mPhoneNumber = sharedPreference.getData("phoneNumber");
            mCountryName = sharedPreference.getData("country");

        }*/

        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        mProgressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        rootStorageRef = FirebaseStorage.getInstance().getReference();

        // set up toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // radio button check change listener
        mGenderRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.male_rb:
                        mGender = "Male";
                        break;
                    case R.id.female_rb:
                        mGender = "Female";
                        break;
                    case R.id.others_rb:
                        mGender = "Others";
                        break;
                }
            }
        });

        mCalendar = Calendar.getInstance();

    }

    @OnClick(R.id.u_dob_et)
    public void getDateString() {

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        new DatePickerDialog(UserInfoActivity.this, date, mCalendar
                .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        mDobEt.setText(sdf.format(mCalendar.getTime()));
    }


    @OnClick(R.id.upload_tv)
    public void selectImage() {

        openGallery();

    }

    public void openGallery() {
        //open photo from the gallery
        Intent galleryIntent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = data.getData();

            //croping the image
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);


        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                //progress bar
                mProgressDialog = new ProgressDialog(UserInfoActivity.this);
                mProgressDialog.setTitle("Image uploading...");
                mProgressDialog.setMessage("Please wait while we upload you image.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                mResultUri = result.getUri();

                // ------------- image compressing for thumb image
                File thumbFilePath = new File(mResultUri.getPath());
                Bitmap thumbBitMap = null;
                try {
                    thumbBitMap = new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(60)
                            .compressToBitmap(thumbFilePath);
                    mUserIv.setImageBitmap(thumbBitMap);
                    mProgressDialog.dismiss();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumbBitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                thumbByte = baos.toByteArray();
                //-------------------------------------


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Error in user Account:" + error.getMessage(), Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Cropping error! Please contact with developer.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @OnClick(R.id.submit_profile_bt)
    public void submitProfileInfo() {

        // getting data from edit text
        String userName = mUserNameEt.getText().toString();
        String email = mEmailEt.getText().toString();
        String dob = mDobEt.getText().toString();
        String pass = mPassEt.getText().toString();

        // checking for validation
        if (userName.isEmpty()) {
            mUserNameEt.setError("User name should not be empty!");
            return;
        } else if (email.isEmpty()) {
            mEmailEt.setError("Email should not be empty");
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailEt.setError("Email is not valid");
            return;
        } else if (dob.isEmpty()) {
            mDobEt.setError("Email is not valid");
            return;
        } else if (pass.isEmpty() || pass.length() < 6) {
            mPassEt.setError("Password should not be empty or less than 6 char.");
            return;
        }

        // save data to the firebase
        final User user = new User("", userName, mGender, email,
                "", dob, "");
        user.setCountry("");
        user.setPassword(pass);
        mProgressDialog.setMessage("Please wait until we create your profile.");
        mProgressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");

                            user.setId(mAuth.getCurrentUser().getUid());

                            //for main image
                            final StorageReference filePathRef = rootStorageRef.child("user_profile_images")
                                    .child(mAuth.getCurrentUser().getUid() + ".jpg");
                            //for thumb image
                            final StorageReference thumbFilePathRef = rootStorageRef.child("user_profile_images")
                                    .child("thumb_images").child(mAuth.getCurrentUser().getUid() + ".jpg");

                            if (mResultUri == null) {
                                //mResultUri = Uri.parse("android.resource://com.kcirqueit.spinandearn/drawable/user_avater");
                                // if photo is not upload set as default for user avater
                                user.setPhotoUrl("default");
                                mUserViewModel.addUser(user).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(UserInfoActivity.this, "Your Profile is created!", Toast.LENGTH_SHORT).show();
                                            //sharedPreference.saveData("profileCreated", "true");
                                            // go to the main activity
                                            startActivity(new Intent(UserInfoActivity.this, FragmentContainerActivity.class));
                                            finish();

                                            mProgressDialog.dismiss();
                                        }
                                    }
                                });

                            } else {

                                filePathRef.putFile(mResultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful()) {

                                            try {
                                                //getting the download uri
                                                //final String userImageUrl = task.getResult().getDownloadUrl().toString();
                                                //final String userImageUrl = filePathRef.getDownloadUrl().;


                                                UploadTask uploadTask = thumbFilePathRef.putBytes(thumbByte);
                                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                        if (task.isSuccessful()) {


                                                            filePathRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                @Override
                                                                public void onSuccess(Uri uri) {
                                                                    Log.d("User image url:", uri.toString());
                                                                    user.setPhotoUrl(uri.toString());
                                                                    mUserViewModel.addUser(user).addOnCompleteListener(new OnCompleteListener() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task task) {
                                                                            if (task.isSuccessful()) {

                                                                                // updating user
                                                                                mAuth.getCurrentUser().updateProfile(updateProfile(userName)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                    }
                                                                                });

                                                                                Toast.makeText(UserInfoActivity.this, "Your Profile is created!", Toast.LENGTH_SHORT).show();
                                                                                //sharedPreference.saveData("profileCreated", "true");
                                                                                // go to the main activity
                                                                                startActivity(new Intent(UserInfoActivity.this, FragmentContainerActivity.class));
                                                                                finish();

                                                                                mProgressDialog.dismiss();
                                                                            }
                                                                        }
                                                                    });

                                                                }
                                                            });


                                                            //download the thumbimage
                                           /* String thumImageUrl = task.getResult().getDownloadUrl().toString();

                                            Map map = new HashMap();
                                            map.put("userImage", userImageUrl);
                                            map.put("thumbnailImage", thumImageUrl);*/

                                            /*//upload the map url to the database
                                            mDbRef.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        mProgressDialog.dismiss();
                                                        Toast.makeText(UserInfoActivity.this, "Image Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });*/

                                                        } else {
                                                            mProgressDialog.dismiss();
                                                            Toast.makeText(UserInfoActivity.this, "Image is not uploaded, Please contact with developer.", Toast.LENGTH_SHORT).show();

                                                        }
                                                    }
                                                });


                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                Toast.makeText(UserInfoActivity.this, "No Image Found to Upload!", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            mProgressDialog.dismiss();
                                            Toast.makeText(UserInfoActivity.this, "Image is not uploaded, Please contact with developer.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                            }

                            // ----------------- finish the registration part -----------------

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(UserInfoActivity.this, "Authentication failed. Please check your email id.",
                                    Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                        }

                        // ...
                    }
                });





    }

    public UserProfileChangeRequest updateProfile(String name) {

        return new UserProfileChangeRequest.Builder()
                .setDisplayName("Jane Q. User")
                .build();
    }


}
