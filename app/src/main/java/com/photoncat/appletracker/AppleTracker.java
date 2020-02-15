package com.photoncat.appletracker;

public class AppleTracker {
    String left_text;
    String middle_text;
    String right_text;
    String left_button;
    String middle_button;
    String right_button;
    String left_text_real;
    String middle_text_real;
    String right_text_real;
    private boolean recording;
    private int first = -1;
    private int second = -1;
    private int second_last = -1;
    private int last = -1;

    public void reset() {
        left_text = "";
        middle_text = "";
        right_text = "";
        left_button = "S";
        middle_button = "H";
        right_button = "A";
        left_text_real = "";
        middle_text_real = "";
        right_text_real = "";
        first = -1;
        second = -1;
        second_last = -1;
        last = -1;
        recording = false;
    }

    void left() {
        if (recording) {
            setNumber(1);
        } else {
            noteLetter("S");
        }
    }

    void middle() {
        if (recording) {
            setNumber(3);
        } else {
            noteLetter("H");
        }
    }

    void right() {
        if (recording) {
            setNumber(2);
        } else {
            noteLetter("A");
        }
    }

    public void record() {
        recording = true;
        left_button = "1";
        middle_button = "3";
        right_button = "2";
    }

    private void setNumber(int number) {
        if (first == -1) {
            first = number;
        } else if (second == -1) {
            second = number;
        }
        second_last = last;
        last = number;
        validate();
    }

    private void validate() {
        left_text = middle_text = right_text = "?";
        if (first == 3 && second == 1) {
            left_text = right_text_real;
            middle_text = middle_text_real;
            right_text = left_text_real;
        }
        if (first == 1 && second == 3) {
            left_text = right_text_real;
            middle_text = left_text_real;
            right_text = middle_text_real;
        }
        if ((first == 3 && second == 3) || (first == 1 && second == 2 && last == 1)) {
            left_text = left_text_real;
            middle_text = middle_text_real;
            right_text = right_text_real;
        }
        if (first == 1 && second == 2 && second_last == 2) {
            left_text = left_text_real;
            middle_text = right_text_real;
            right_text = middle_text_real;
        }
        if (first == 1 && second == 2 && second_last == 1) {
            left_text = middle_text_real;
            middle_text = left_text_real;
            right_text = right_text_real;
        }
        if (first == 1 && second == 2 && second_last == 3 && last == 2) {
            left_text = middle_text_real;
            middle_text = right_text_real;
            right_text = left_text_real;
        }
    }

    private void noteLetter(String letter) {
        if (left_text_real.isEmpty()) {
            left_text_real = left_text = letter;
        } else if (!left_text_real.equals(letter)) {
            if (middle_text_real.isEmpty()) {
                middle_text_real = middle_text = letter;
            } else if (!middle_text_real.equals(letter)) {
                if (right_text_real.isEmpty()) {
                    right_text_real = right_text = letter;
                    record();
                }
            }
        }
    }
}
