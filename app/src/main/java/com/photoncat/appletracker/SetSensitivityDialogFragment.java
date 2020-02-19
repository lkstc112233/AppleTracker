package com.photoncat.appletracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.SeekBar;

public class SetSensitivityDialogFragment extends DialogFragment {
    AppleDetector detector;

    private SeekBar rLow;
    private SeekBar rHigh;
    private SeekBar gLow;
    private SeekBar gHigh;
    private SeekBar bLow;
    private SeekBar bHigh;

    @Override
    public void onStart() {
        super.onStart();
        rLow = getDialog().findViewById(R.id.r_low);
        rLow.setProgress(detector.rLow);
        rHigh = getDialog().findViewById(R.id.r_high);
        rHigh.setProgress(detector.rHigh);
        gLow = getDialog().findViewById(R.id.g_low);
        gLow.setProgress(detector.gLow);
        gHigh = getDialog().findViewById(R.id.g_high);
        gHigh.setProgress(detector.gHigh);
        bLow = getDialog().findViewById(R.id.b_low);
        bLow.setProgress(detector.bLow);
        bHigh = getDialog().findViewById(R.id.b_high);
        bHigh.setProgress(detector.bHigh);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.dialog_sensitivity);
        builder.setPositiveButton("OK", (dialog, id) -> {
            detector.rLow = rLow.getProgress();
            detector.rHigh = rHigh.getProgress();
            detector.gLow = gLow.getProgress();
            detector.gHigh = gHigh.getProgress();
            detector.bLow = bLow.getProgress();
            detector.bHigh = bHigh.getProgress();
            detector.updateBounds();
        });
        builder.setNegativeButton("Cancel", (dialog, id) -> {});
        return builder.create();
    }
}
