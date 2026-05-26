package com.example.callthematch.exception;

public class PredictionCutoffPassed extends RuntimeException {

    public PredictionCutoffPassed() {
        super("You cannot make a prediction for this match anymore.");
    }
}
