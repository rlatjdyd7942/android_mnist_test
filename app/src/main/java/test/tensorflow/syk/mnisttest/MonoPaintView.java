package test.tensorflow.syk.mnisttest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import java.io.ByteArrayOutputStream;

import static java.security.AccessController.getContext;

/**
 * Created by syk on 2017-07-07.
 */

public class MonoPaintView extends LinearLayout {
    public static final int BITMAP_WIDTH = 28;
    public static final int BITMAP_HEIGHT = 28;
    public static final int BITMAP_LENGTH = BITMAP_WIDTH * BITMAP_HEIGHT;

    private Canvas bitmapCanvas;
    private Bitmap bitmap;
    private Paint paint;

    private int width, height;

    private boolean drawing = false;
    private int prevX = 0;
    private int prevY = 0;

    public MonoPaintView(Context context, int width, int height) {
        super(context);
        initView(width, height);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        RectF dest = new RectF(0, 0, width, height);
        canvas.drawBitmap(bitmap, null, dest, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        int x = (int)event.getX();
        int y = (int)event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                drawing = true;
                break;

            case MotionEvent.ACTION_MOVE:
                if (drawing) {
                    bitmapCanvas.drawLine(prevX * 28.f / width, prevY * 28.f / height,
                            x * 28.f / width, y * 28.f / height, paint);
                }
                break;

            case MotionEvent.ACTION_UP:
                drawing = false;
                break;
        }
        prevX = x;
        prevY = y;
        postInvalidate();
        return true;
    }

    private void initView(int width, int height) {
        this.width = width;
        this.height = height;

        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        View v = li.inflate(R.layout.mono_paint_view, this, false);
        addView(v);

        setLayoutParams(new LayoutParams(width, height));

        //matrix.setScale(25, height/28.f);
        bitmap = Bitmap.createBitmap(28, 28, Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);
        paint = new Paint();
        paint.setAntiAlias(true);
        clearBitmap();
    }

    public void clearBitmap() {
        paint.setColor(Color.WHITE);
        RectF dest = new RectF(0.f, 0.f, width, height);
        bitmapCanvas.drawRect(dest, paint);
        paint.setColor(Color.BLACK);
    }

    public float[] getPixelArray() {
        int[] pixels = new int[28 * 28];
        bitmap.getPixels(pixels, 0, 28, 0, 0, 28, 28);

        float[] mono = new float[pixels.length];
        for (int i = 0; i < pixels.length; ++i) {
            mono[i] = (float)(0xff - pixels[i] & 0xff);
        }
        return mono;
    }

    public void setAntiAlias(boolean b) {
        paint.setAntiAlias(b);
    }
}
