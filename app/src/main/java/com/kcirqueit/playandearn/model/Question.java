package com.kcirqueit.playandearn.model;

import java.io.Serializable;
import java.util.List;

public class Question implements Serializable {

    private String id;
    private String quizId;
    private String questionTitle;
    private List<String> options;
    private int correctAns;

    public Question(String quizId, String questionTitle, List<String> options, int correctAns) {
        this.quizId = quizId;
        this.questionTitle = questionTitle;
        this.options = options;
        this.correctAns = correctAns;
    }

    public Question() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public int getCorrectAns() {
        return correctAns;
    }

    public void setCorrectAns(int correctAns) {
        this.correctAns = correctAns;
    }
}
