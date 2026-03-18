package com.example.interview.enums;

public enum QuestionLevel {
    X1, A5, B1, B2, C1, C2;

    public static boolean contains(String val) {
        if (val == null) return false;
        try {
            QuestionLevel.valueOf(val);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
