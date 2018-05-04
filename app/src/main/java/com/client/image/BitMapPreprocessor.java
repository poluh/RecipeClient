package com.client.image;

import android.graphics.Bitmap;

public class BitMapPreprocessor {

    private Bitmap image;
    private int[][] allImagePixels;

    public BitMapPreprocessor(Bitmap bitmap, int width, int height) {
        this.image = Bitmap.createScaledBitmap(bitmap, width / 10, height / 10, false);
    }

    public Bitmap getImageProcessing() {
        updatePixelsImage();
        cropImage();
        return Bitmap.createScaledBitmap(image, 50, 50, false);
    }

    private void cropImage() {
        Point topPoint = findAnchorPoint(Course.TOP);
        Point leftPoint = findAnchorPoint(Course.LEFT);
        Point leftTopCropPoint = new Point(leftPoint.x, topPoint.y);
        Point leftBottomCropPoint = new Point(leftPoint.x, findAnchorPoint(Course.BOTTOM).y);
        Point rightTopCropPoint = new Point(findAnchorPoint(Course.RIGHT).x, topPoint.y);

        int cropWidth = leftTopCropPoint.distance(rightTopCropPoint);
        int cropHeight = leftTopCropPoint.distance(leftBottomCropPoint);
        this.image = Bitmap.createBitmap(image,leftTopCropPoint.x, leftTopCropPoint.y, cropWidth, cropHeight);
        updatePixelsImage();
    }

    private enum Course {
        TOP,
        BOTTOM,
        LEFT,
        RIGHT
    }


    private Point findAnchorPoint(Course course) {

        int imageHeight = image.getHeight() - 2;
        int imageWidth = image.getWidth() - 2;

        int firstLimiter = course == Course.RIGHT || course == Course.LEFT ? imageWidth : imageHeight;
        int secondLimiter = course == Course.TOP || course == Course.BOTTOM ? imageWidth : imageHeight;

        if (course == Course.LEFT || course == Course.TOP) {
            for (int i = 1; i < firstLimiter; i++) {
                for (int j = 1; j < secondLimiter; j++) {
                    Point answerPoint = isCorrectedPoint(i, j, course);
                    if (!answerPoint.equals(new Point(-1, -1))) {
                        return answerPoint;
                    }
                }
            }
        } else {
            for (int i = firstLimiter; i >= 1; i--) {
                for (int j = secondLimiter; j >= 1; j--) {
                    Point answerPoint = isCorrectedPoint(i, j, course);
                    if (!answerPoint.equals(new Point(-1, -1))) {
                        return answerPoint;
                    }
                }
            }
        }
        throw new IllegalArgumentException("Point not found");
    }

    private Point isCorrectedPoint(int i, int j, Course course) {
        boolean courseLeftOrRight = course == Course.LEFT || course == Course.RIGHT;
        int xSupportPixels = courseLeftOrRight ? i : j;
        int ySupportPixels = courseLeftOrRight ? j : i;
        int topLeftPixel = allImagePixels[xSupportPixels - 1][ySupportPixels - 1];
        int bottomRightPixel = allImagePixels[xSupportPixels + 1][ySupportPixels + 1];
        if (topLeftPixel - bottomRightPixel != 0) {
            return new Point(xSupportPixels, ySupportPixels);
        }
        return new Point(-1, -1);
    }

    private void updatePixelsImage() {
        int[][] allPixels = new int[image.getWidth()][image.getHeight()];

        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                allPixels[i][j] = image.getPixel(i, j);
            }
        }
        this.allImagePixels = allPixels;
    }

    public Bitmap getImage() {
        return image;
    }
}
