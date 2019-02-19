package com.kcirqueit.playandearn.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kcirqueit.playandearn.model.User;

import androidx.lifecycle.LiveData;

public class UserRepository {

    private DatabaseReference mRootRef;
    private DatabaseReference mUserRef;

    private static UserRepository userRepository;

    FirebaseQueryLiveData firebaseQueryLiveData;

    private UserRepository() {
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUserRef = mRootRef.child("Users");
        firebaseQueryLiveData = new FirebaseQueryLiveData(mUserRef);
    }


    public static UserRepository getInstance() {
        if (userRepository == null) {
            userRepository = new UserRepository();
            return userRepository;
        }
        return userRepository;
    }


    public Task addUser(User user) {
        mUserRef = mRootRef.child("Users");
        return mUserRef.child(user.getId()).setValue(user);
    }



    public LiveData<DataSnapshot> getAllUser() {
        return firebaseQueryLiveData;
    }

    public LiveData<DataSnapshot> getUserById(String userId) {
        mUserRef = mUserRef.child(userId);
        FirebaseQueryLiveData firebaseQueryLiveData =  new FirebaseQueryLiveData(mUserRef);
        mUserRef = mRootRef.child("Users");

        return firebaseQueryLiveData;
    }





}
