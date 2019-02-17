package com.kcirqueit.playandearn.viewModel;

import android.app.Application;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.kcirqueit.playandearn.model.User;
import com.kcirqueit.playandearn.repository.UserRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class UserViewModel extends AndroidViewModel {

    private UserRepository mUserRepository;

    public UserViewModel(@NonNull Application application) {
        super(application);
        mUserRepository = UserRepository.getInstance();

    }

    public Task addUser(User user) {
        return mUserRepository.addUser(user);
    }

    public LiveData<DataSnapshot> getAllUsers() {
        return mUserRepository.getAllUser();
    }


    public LiveData<DataSnapshot> getUserById(String userId) {
        return mUserRepository.getUserById(userId);
    }

}
