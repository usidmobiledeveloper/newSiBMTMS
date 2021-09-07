package com.usid.mobilebmt.mandirisejahtera.dashboard.riwayat;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuffColorFilter;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.usid.mobilebmt.mandirisejahtera.R;

import static android.graphics.Bitmap.Config.ALPHA_8;
import static android.graphics.Color.BLACK;
import static android.graphics.Color.TRANSPARENT;
import static android.graphics.PorterDuff.Mode.SRC_IN;

public class ZigzagView extends View {
    private Path mPath = new Path();
    Paint paint;
    Paint shadowPaint;
    private float zigzagHeight;
    float mShadowBlurRadius = 8;
    Bitmap mShadow;

    public ZigzagView(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public ZigzagView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public ZigzagView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    /*@RequiresApi(api = 21)
    public RKZigzagView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }*/


    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ZigzagView, defStyleAttr, defStyleRes);
        this.zigzagHeight = a.getDimension(R.styleable.ZigzagView_zigzagHeight, 0.0f);
        a.recycle();

        this.paint = new Paint();
        this.paint.setColor(Color.WHITE);
        this.paint.setStyle(Style.FILL);
        this.paint.setAntiAlias(true);

        //shadowPaint
        shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadowPaint.setColorFilter(new PorterDuffColorFilter(BLACK, SRC_IN));
        shadowPaint.setAlpha(51); // 20%

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);


    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //calculate bounds

        float left = getPaddingLeft() + mShadowBlurRadius;
        float right = getWidth() - getPaddingRight() - mShadowBlurRadius;
        float top = getPaddingTop() + (mShadowBlurRadius / 2);
        float bottom = getHeight() - getPaddingBottom() - mShadowBlurRadius - (mShadowBlurRadius / 2);
        int width = (int) (right - left);
        //int height = (int) (bottom-top);

        mPath.moveTo(right, bottom);
        mPath.lineTo(right, top);
        mPath.lineTo(left, top);
        mPath.lineTo(left, bottom);

        int h = (int) zigzagHeight;
        int seed = 2 * h;
        int count = width / seed;
        int diff = width - (seed * count);

        int sideDiff = diff / 2;

        float x = (float) (seed / 2);
        float upHeight = bottom - h;

        for (int i = 0; i < count; i++) {
            int startSeed = (i * seed) + sideDiff + (int) left;
            int endSeed = startSeed + seed;

            if (i == 0) {
                startSeed = (int) left + sideDiff;
            } else if (i == count - 1) {
                endSeed = endSeed + sideDiff;
            }

            this.mPath.lineTo(startSeed + x, upHeight);
            this.mPath.lineTo(endSeed, bottom);

        }

        generateShadow();
        canvas.drawBitmap(mShadow, 0, mShadowBlurRadius / 2, null);

        canvas.drawPath(mPath, paint);

    }

    private void generateShadow() {
        mShadow = Bitmap.createBitmap(getWidth(), getHeight(), ALPHA_8);
        mShadow.eraseColor(TRANSPARENT);
        Canvas c = new Canvas(mShadow);
        c.drawPath(mPath, shadowPaint);

        RenderScript rs = RenderScript.create(getContext());
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, Element.U8(rs));
        Allocation input = Allocation.createFromBitmap(rs, mShadow);
        Allocation output = Allocation.createTyped(rs, input.getType());
        blur.setRadius(mShadowBlurRadius);
        blur.setInput(input);
        blur.forEach(output);
        output.copyTo(mShadow);
        input.destroy();
        output.destroy();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int desiredWidth = 100;
        int desiredHeight = 100;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }
}