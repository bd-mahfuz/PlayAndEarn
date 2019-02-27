package com.kcirqueit.playandearn.model;

import java.io.Serializable;
import java.util.List;

public class Quiz implements Serializable {

    private String id;
    private String userId;
    private String quizName;
    private String timeLimit;
    private String cat_id;
    private int totalMarks;
    private int totalQuestion;
    private String status = "un-published"; // published or complete

    public Quiz() {
    }

    public Quiz(String userId, String quizName, String cat_id, int totalMarks, int totalQuestion,
                String timeLimit) {
        this.userId = userId;
        this.quizName = quizName;
        this.cat_id = cat_id;
        this.totalMarks = totalMarks;
        this.totalQuestion = totalQuestion;
        this.timeLimit = timeLimit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getQuizName() {
        return quizName;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }

    public int getTotalQuestion() {
        return totalQuestion;
    }

    public void setTotalQuestion(int totalQuestion) {
        this.totalQuestion = totalQuestion;
    }

    public String getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(String timeLimit) {
        this.timeLimit = timeLimit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
