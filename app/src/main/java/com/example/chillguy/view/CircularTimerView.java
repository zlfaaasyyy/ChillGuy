package com.example.chillguy.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CircularTimerView extends View {
    private final Paint trackPaint;
    private final Paint progressPaint;
    private float progress = 1.0f;
    private final RectF oval = new RectF();

    private static final float STROKE_WIDTH = 18f;
    private static final float START_ANGLE  = -90f;

    public CircularTimerView(Context context) {
        this(context, null);
    }

    public CircularTimerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        trackPaint.setStyle(Paint.Style.STROKE);
        trackPaint.setStrokeWidth(STROKE_WIDTH);
        trackPaint.setStrokeCap(Paint.Cap.ROUND);
        trackPaint.setColor(0xFFEED5D8);

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(STROKE_WIDTH);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setColor(0xFFE8A7A1);
    }

    public void setProgress(float progress) {
        this.progress = Math.max(0f, Math.min(1f, progress));
        invalidate();
    }

    public float getProgress() { return progress; }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float w = getWidth();
        float h = getHeight();
        float padding = STROKE_WIDTH / 2f + 4f;

        oval.set(padding, padding, w - padding, h - padding);
        canvas.drawArc(oval, 0f, 360f, false, trackPaint);
        float sweepAngle = 360f * progress;
        canvas.drawArc(oval, START_ANGLE, sweepAngle, false, progressPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = Math.min(
                MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec));
        setMeasuredDimension(size, size);
    }
}