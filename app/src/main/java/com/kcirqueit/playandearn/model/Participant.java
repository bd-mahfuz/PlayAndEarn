package com.kcirqueit.playandearn.model;

import java.io.Serializable;

public class Participant implements Serializable {

    private String id;
    private String isFinished;
    private int score;
    private int totalAnswered;
    private int totalQuestion;

    public Participant() {
    }

    public Participant(String isFinished, int score, int totalAnswered, int totalQuestion) {

        this.isFinished = isFinished;
        this.score = score;
        this.totalAnswered = totalAnswered;
        this.totalQuestion = totalQuestion;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsFinished() {
        return isFinished;
    }

    public void setIsFinished(String isFinished) {
        this.isFinished = isFinished;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalAnswered() {
        return totalAnswered;
    }

    public void setTotalAnswered(int totalAnswered) {
        this.totalAnswered = totalAnswered;
    }

    public int getTotalQuestion() {
        return totalQuestion;
    }

    public void setTotalQuestion(int totalQuestion) {
        this.totalQuestion = totalQuestion;
    }
}
