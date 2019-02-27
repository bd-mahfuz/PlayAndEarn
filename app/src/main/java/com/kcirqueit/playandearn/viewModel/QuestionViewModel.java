package com.kcirqueit.playandearn.viewModel;

import android.app.Application;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.kcirqueit.playandearn.model.Question;
import com.kcirqueit.playandearn.repository.QuestionRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class QuestionViewModel extends AndroidViewModel {

    private QuestionRepository questionRepository;

    public QuestionViewModel(@NonNull Application application) {
        super(application);
        questionRepository = QuestionRepository.getInstance();
    }

    public Task addQuestion(Question question) {
        return questionRepository.addQuestion(question);
    }

    public Task updateQuestion(Question question) {
        return questionRepository.updateQuestion(question);
    }

    public Task deleteQeustion(Question question) {
        return questionRepository.deleteQuestion(question);
    }

    public Task deleteAllQuestion(String quizId) {
        return questionRepository.deleteAllQuestion(quizId);
    }

    public LiveData<DataSnapshot> getAllQuestionsByQuizId(String quizId) {
        return questionRepository.getAllQuestionsByQuizId(quizId);
    }

}
