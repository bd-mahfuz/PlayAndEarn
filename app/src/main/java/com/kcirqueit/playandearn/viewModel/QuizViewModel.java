package com.kcirqueit.playandearn.viewModel;

import android.app.Application;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.kcirqueit.playandearn.model.Quiz;
import com.kcirqueit.playandearn.repository.QuizRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class QuizViewModel extends AndroidViewModel {

    private QuizRepository quizRepository;

    public QuizViewModel(@NonNull Application application) {
        super(application);
        quizRepository = QuizRepository.getInstance();
    }

    public LiveData<DataSnapshot> getAllQuiz() {
        return quizRepository.getAllQuiz();
    }

    public Task addQuiz(Quiz quiz) {
        return quizRepository.addQuiz(quiz);
    }

    public Task deleteQuiz(Quiz quiz) {
        return quizRepository.deleteQuiz(quiz);
    }

    public Task updateQuiz(Quiz quiz) {
        return quizRepository.updateQuiz(quiz);
    }

    public LiveData<DataSnapshot> getQuizById(String quizId) {
        return quizRepository.getQuizById(quizId);
    }


}
