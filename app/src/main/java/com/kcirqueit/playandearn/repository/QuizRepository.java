package com.kcirqueit.playandearn.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kcirqueit.playandearn.model.Quiz;

import androidx.lifecycle.LiveData;

public class QuizRepository {

    private FirebaseQueryLiveData firebaseQueryLiveData;

    private static QuizRepository quizRepository;

    private DatabaseReference mQuizRef;
    private DatabaseReference mRootRef;

    private QuizRepository() {

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mQuizRef = mRootRef.child("Quizes");
        firebaseQueryLiveData = new FirebaseQueryLiveData(mQuizRef);
    }

    public static QuizRepository getInstance() {
        if (quizRepository == null) {

            quizRepository = new QuizRepository();
            return quizRepository;
        }
        return quizRepository;
    }

    public LiveData<DataSnapshot> getAllQuiz() {
        return firebaseQueryLiveData;
    }

    public Task addQuiz(Quiz quiz) {
        String key = mQuizRef.push().getKey();
        quiz.setId(key);
        return mQuizRef.child(key).setValue(quiz);
    }

    public Task updateQuiz(Quiz quiz) {
        return mQuizRef.child(quiz.getId()).setValue(quiz);
    }

    public Task deleteQuiz(Quiz quiz) {
        return mQuizRef.child(quiz.getId()).removeValue();
    }

    public LiveData<DataSnapshot> getQuizById(String quizId) {
       return new FirebaseQueryLiveData( mQuizRef.child(quizId));
    }


}
