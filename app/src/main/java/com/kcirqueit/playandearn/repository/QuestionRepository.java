package com.kcirqueit.playandearn.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kcirqueit.playandearn.model.Question;

import androidx.lifecycle.LiveData;

public class QuestionRepository {

    private static QuestionRepository questionRepository;

    private DatabaseReference mRootRef;
    private DatabaseReference mQuestionRef;

    private FirebaseQueryLiveData firebaseQueryLiveData;

    private QuestionRepository() {
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mQuestionRef = mRootRef.child("Questions");
        firebaseQueryLiveData = new FirebaseQueryLiveData(mQuestionRef);
    }

    public static QuestionRepository getInstance() {

        if (questionRepository == null) {
            questionRepository = new QuestionRepository();
        }

        return questionRepository;
    }


    public Task addQuestion(Question question) {
        String key = mQuestionRef.push().getKey();
        question.setId(key);
        return mQuestionRef.child(question.getQuizId()).child(key).setValue(question);
    }

    public Task updateQuestion(Question question) {
        return mQuestionRef.child(question.getQuizId()).child(question.getId()).setValue(question);
    }

    public Task deleteQuestion(Question question) {
        return mQuestionRef.child(question.getQuizId()).child(question.getId()).removeValue();
    }

    public Task deleteAllQuestion(String quizId) {
        return mQuestionRef.child(quizId).removeValue();
    }

    public LiveData<DataSnapshot> getAllQuestionsByQuizId(String quizId) {
        return new FirebaseQueryLiveData(mQuestionRef.child(quizId));
    }


}
