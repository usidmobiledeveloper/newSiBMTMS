package com.usid.mobilebmt.mandirisejahtera.dashboard.riwayat;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

public class ZigZag extends FrameLayout {
    public ZigZag(Context context) {
        super(context);
        init(context, null, 0);
    }

    public ZigZag(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ZigZag(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        ZigzagView pieView = new ZigzagView(context, attrs, defStyleAttr);
        addView(pieView);

    }


}
