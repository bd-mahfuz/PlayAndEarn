package com.kcirqueit.playandearn.viewModel;

import android.app.Application;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.kcirqueit.playandearn.model.Participant;
import com.kcirqueit.playandearn.repository.ParticipantRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class ParticipantViewModel extends AndroidViewModel {

    private ParticipantRepository participantRepository;

    public ParticipantViewModel(@NonNull Application application) {
        super(application);

        participantRepository = ParticipantRepository.getInstance();

    }

    public Task addParticipant(Participant participant, String quizId) {
        return participantRepository.addParticipant(participant, quizId);
    }

    public LiveData<DataSnapshot> isParticipate(String userId, String quizId) {
        return participantRepository.isParticipate(userId, quizId);
    }

    public LiveData<DataSnapshot> getAllParticipant(String quizId) {

        return participantRepository.getAllParticipant(quizId);

    }

    public LiveData<DataSnapshot> getAllQuiz() {
        return participantRepository.getAllQuiz();
    }
}
