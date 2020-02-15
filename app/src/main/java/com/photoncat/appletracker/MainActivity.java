package com.photoncat.appletracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    AppleTracker appleTracker;

    TextView left_text;
    TextView middle_text;
    TextView right_text;
    Button left_button;
    Button middle_button;
    Button right_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appleTracker = new AppleTracker();
        left_text = findViewById(R.id.textview_left);
        middle_text = findViewById(R.id.textview_middle);
        right_text = findViewById(R.id.textview_right);
        left_button = findViewById(R.id.button_left);
        middle_button = findViewById(R.id.button_middle);
        right_button = findViewById(R.id.button_right);

        reset();
        updateView();
    }

    private void updateView() {
        left_text.setText(appleTracker.left_text);
        middle_text.setText(appleTracker.middle_text);
        right_text.setText(appleTracker.right_text);
        left_button.setText(appleTracker.left_button);
        middle_button.setText(appleTracker.middle_button);
        right_button.setText(appleTracker.right_button);
    }

    private void reset() {
        appleTracker.reset();
    }

    public void leftClickHandler(View view) {
        appleTracker.left();
        updateView();
    }

    public void rightClickHandler(View view) {
        appleTracker.right();
        updateView();
    }

    public void middleClickHandler(View view) {
        appleTracker.middle();
        updateView();
    }

    public void resetClickHandler(View view) {
        reset();
        updateView();
    }
}
