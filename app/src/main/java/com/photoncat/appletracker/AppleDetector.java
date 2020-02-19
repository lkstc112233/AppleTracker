package com.photoncat.appletracker;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AppleDetector {
    private List<MatOfPoint> mContours = new ArrayList<MatOfPoint>();
    private final static org.opencv.core.Size mGaussianBlurRange = new org.opencv.core.Size(3,3);
    private final static org.opencv.core.Point mErodeAnchor = new org.opencv.core.Point(-1, -1);

    private final Scalar mInRangeLowerBound = new Scalar(0, 0, 190, 0);
    private final Scalar mInRangeHigherBound = new Scalar(50, 50, 255, 255);
    private final Scalar mBlackScalar = new Scalar(0, 0, 0, 255);
    public int rLow = 190;
    public int rHigh = 255;
    public int gLow = 0;
    public int gHigh = 50;
    public int bLow = 0;
    public int bHigh = 50;

    public void updateBounds() {
        mInRangeLowerBound.set(new double[]{bLow, gLow, rLow, 0});
        mInRangeHigherBound.set(new double[]{bHigh, gHigh, rHigh, 255});
    }

    // Cache
    private Mat mPartialResultMat = new Mat();
    private Mat mRangedMat = new Mat();
    private Mat mBlurredMat = new Mat();
    private Mat mThresholdedMat = new Mat();
    private Mat mErodedMat = new Mat();
    private Mat mDilatedMat = new Mat();
    private Mat mEdgedMat = new Mat();
    private Mat mHierarchy = new Mat();

    public void process(Mat rgbaImage) {
        Core.inRange(rgbaImage, mInRangeLowerBound, mInRangeHigherBound, mRangedMat);
        mPartialResultMat.setTo(mBlackScalar);
        rgbaImage.copyTo(mPartialResultMat, mRangedMat);
        mPartialResultMat.copyTo(rgbaImage);
        Imgproc.GaussianBlur(mRangedMat, mBlurredMat, mGaussianBlurRange, 0);
        Imgproc.threshold(mBlurredMat, mThresholdedMat, 64, 255, Imgproc.THRESH_BINARY);
        Imgproc.erode(mThresholdedMat, mErodedMat, new Mat(), mErodeAnchor, 3);
        Imgproc.dilate(mErodedMat, mDilatedMat, new Mat(), mErodeAnchor, 7);
        Imgproc.Canny(mDilatedMat, mEdgedMat, 30, 150);

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        Imgproc.findContours(mEdgedMat, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Find max contour area
        double maxArea = 0;
        Iterator<MatOfPoint> each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint wrapper = each.next();
            double area = Imgproc.contourArea(wrapper);
            if (area > maxArea)
                maxArea = area;
        }

        // Filter contours by area and resize to fit the original image size
        mContours.clear();
        each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint contour = each.next();
//            if (Imgproc.contourArea(contour) > mMinContourArea*maxArea) {
//                Core.multiply(contour, new Scalar(4,4), contour);
            mContours.add(contour);
//            }
        }
    }

    public void processFully(Mat rgbaImage) {
        Core.inRange(rgbaImage, mInRangeLowerBound, mInRangeHigherBound, mRangedMat);
        Imgproc.GaussianBlur(mRangedMat, mBlurredMat, mGaussianBlurRange, 0);
        Imgproc.threshold(mBlurredMat, mThresholdedMat, 64, 255, Imgproc.THRESH_BINARY);
        Imgproc.erode(mThresholdedMat, mErodedMat, new Mat(), mErodeAnchor, 3);
        Imgproc.dilate(mErodedMat, mDilatedMat, new Mat(), mErodeAnchor, 7);
        Imgproc.Canny(mDilatedMat, mEdgedMat, 30, 150);

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        Imgproc.findContours(mEdgedMat, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Find max contour area
        double maxArea = 0;
        Iterator<MatOfPoint> each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint wrapper = each.next();
            double area = Imgproc.contourArea(wrapper);
            if (area > maxArea)
                maxArea = area;
        }

        // Filter contours by area and resize to fit the original image size
        mContours.clear();
        each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint contour = each.next();
//            if (Imgproc.contourArea(contour) > mMinContourArea*maxArea) {
//                Core.multiply(contour, new Scalar(4,4), contour);
            mContours.add(contour);
//            }
        }
    }

    public List<MatOfPoint> getContours() {
        return mContours;
    }
}