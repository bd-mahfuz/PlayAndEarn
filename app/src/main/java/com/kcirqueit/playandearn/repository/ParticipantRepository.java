package com.kcirqueit.playandearn.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kcirqueit.playandearn.model.Participant;

import androidx.lifecycle.LiveData;

public class ParticipantRepository {

    private static ParticipantRepository participantRepository;

    private DatabaseReference mRootRef;
    private DatabaseReference mParticipantRef;

    private ParticipantRepository() {
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mParticipantRef = mRootRef.child("Participants");
    }

    public static ParticipantRepository getInstance() {

        if (participantRepository == null) {
            participantRepository = new ParticipantRepository();
            return participantRepository;
        }

        return participantRepository;
    }

    public Task addParticipant(Participant participant, String quizId) {
        return mParticipantRef.child(quizId).child(participant.getId()).setValue(participant);
    }

    public LiveData<DataSnapshot> isParticipate(String userId, String quizId) {
        return new FirebaseQueryLiveData(mParticipantRef.child(quizId).child(userId));
    }

    public LiveData<DataSnapshot> getAllParticipant(String quizId) {
        return new FirebaseQueryLiveData(mParticipantRef.child(quizId));
    }

    public LiveData<DataSnapshot> getAllQuiz() {
        return new FirebaseQueryLiveData(mParticipantRef);
    }



}
