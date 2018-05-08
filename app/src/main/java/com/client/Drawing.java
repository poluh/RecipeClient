package com.client;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.MotionEvent;
import android.view.View;
import com.client.image.BitMapPreprocessor;
import com.client.server.connection.ServerForwarder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

@SuppressLint("ViewConstructor")
class DrawingView extends View {

    public int width;
    public int height;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    private Paint circlePaint;
    private Path circlePath;
    private Paint mPaint;

    public DrawingView(Context context, Paint mPaint) {
        super(context);
        this.setDrawingCacheEnabled(true);
        this.mPaint = mPaint;
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        circlePaint = new Paint();
        circlePath = new Path();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLACK);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(4f);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        this.height = height;
        this.width = width;
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Paint fill = new Paint();
        mCanvas = new Canvas(mBitmap);
        fill.setColor(Color.WHITE);
        mCanvas.drawPaint(fill);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.BLACK);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);
        canvas.drawPath(circlePath, circlePaint);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touchStart(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;

            circlePath.reset();
            circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
        }
    }

    private void touchUp() {
        mPath.lineTo(mX, mY);
        circlePath.reset();
        mCanvas.drawPath(mPath, mPaint);
        mPath.reset();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchOut(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                try {
                    Bitmap uploadImage = imageProcessing();
                    String processingResult = processingResult();
                    new Shower(getContext()).show(processingResult, uploadImage);
                } catch (FileNotFoundException ex) {
                    System.err.println("File not found, " + ex.getMessage());
                }
                touchUp();
                touchOut(x, y);
                invalidate();
                break;
        }
        return true;
    }

    private String processingResult() throws FileNotFoundException {
        ServerForwarder serverForwarder = new ServerForwarder(
                new File(getContext().getFilesDir() + "image.png"));
        serverForwarder.connect();
        return serverForwarder.getMostLikelyResult();
    }

    private Bitmap imageProcessing() throws FileNotFoundException {
        File image = new File(getContext().getFilesDir() + "image.png");
        FileOutputStream imageFOS = new FileOutputStream(image);
        Bitmap uploadImage =
                new BitMapPreprocessor(getDrawingCache(), width, height)
                        .getImageProcessing();
        uploadImage.compress(Bitmap.CompressFormat.PNG, 0, imageFOS);
        return uploadImage;
    }

    private void touchOut(float x, float y) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        mCanvas.drawPaint(paint);
        touchStart(x, y);
        invalidate();
    }
}
