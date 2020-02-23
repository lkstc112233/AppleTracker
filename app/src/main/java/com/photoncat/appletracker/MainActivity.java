package com.photoncat.appletracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.List;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 12345;
    AppleTracker appleTracker;

    TextView left_text;
    TextView middle_text;
    TextView right_text;
    TextView camera_comment;
    Button left_button;
    Button middle_button;
    Button right_button;

    private boolean mIsColorSelected = false;
    private Mat mRgba;
    private Mat mRgbaT;
    private Scalar mBlobColorRgba;
    private Scalar mBlobColorHsv;
    private AppleDetector mDetector;
    private Mat mSpectrum;
    private Size SPECTRUM_SIZE;
    private Scalar CONTOUR_COLOR;

    private CameraBridgeViewBase mOpenCvCameraView;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions_main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (mOpenCvCameraView != null) {
            if (!OpenCVLoader.initDebug()) {
                OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
            } else {
                mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    private void updateView() {
        left_text.setText(appleTracker.left_text);
        middle_text.setText(appleTracker.middle_text);
        right_text.setText(appleTracker.right_text);
        left_button.setText(appleTracker.left_button);
        middle_button.setText(appleTracker.middle_button);
        right_button.setText(appleTracker.right_button);
        if (camera_comment != null && mDetector != null) {
            camera_comment.setText(mDetector.comment);
        }
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

    public void activateCameraHandler(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);

            // MY_PERMISSIONS_REQUEST_CAMERA is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        } else {
            showCamaraView();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                showCamaraView();
            }
        }
    }

    private void showCamaraView() {
        ViewStub viewStub = findViewById(R.id.camera_stub);
        viewStub.inflate();

        camera_comment = findViewById(R.id.camera_comment_text_view);

        mOpenCvCameraView = findViewById(R.id.color_blob_detection_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);

        findViewById(R.id.activate_camera_button).setVisibility(View.GONE);

        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        updateView();
    }

    private void launchSampleActivity() {
        Intent intent = new Intent(this, SampleActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mRgbaT = new Mat(width, height, CvType.CV_8UC4);
        mDetector = new AppleDetector();
        mSpectrum = new Mat();
        mBlobColorRgba = new Scalar(255);
        mBlobColorHsv = new Scalar(255);
        SPECTRUM_SIZE = new Size(200, 64);
        CONTOUR_COLOR = new Scalar(240, 0, 159, 255);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mDetector.process(mRgba);
        List<MatOfPoint> contours = mDetector.getContours();
        Imgproc.drawContours(mRgba, contours, -1, CONTOUR_COLOR, 3);
        Mat transformed = mRgba.t();
        Core.flip(transformed, mRgbaT, 1);
        transformed.release();
        Imgproc.resize(mRgbaT, mRgba, mRgba.size());
        runOnUiThread(() -> updateView());
        return mRgba;
    }

    public void randomActionHandler(MenuItem item) {
        SetSensitivityDialogFragment dialogFragment = new SetSensitivityDialogFragment();
        dialogFragment.detector = mDetector;
        dialogFragment.show(getSupportFragmentManager(), "SensitivityDialogFragment");
    }
}
