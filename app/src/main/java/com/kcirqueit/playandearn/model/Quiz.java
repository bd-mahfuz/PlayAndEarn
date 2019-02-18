package com.kcirqueit.playandearn.model;

import java.io.Serializable;
import java.util.List;

public class Quiz implements Serializable {

    private String id;
    private String questionTitle;
    private String correctAns;
    private List<String> options;

    public Quiz() {
    }

    public Quiz(String id, String questionTitle, String correctAns, List<String> options) {
        this.id = id;
        this.questionTitle = questionTitle;
        this.correctAns = correctAns;
        this.options = options;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public String getCorrectAns() {
        return correctAns;
    }

    public void setCorrectAns(String correctAns) {
        this.correctAns = correctAns;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }
}
