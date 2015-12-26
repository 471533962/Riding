package com.bingo.riding.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by bingo on 15/10/21.
 */
public class SquarePhotosGridView extends GridView {
    public SquarePhotosGridView(Context context) {
        super(context);
    }

    public SquarePhotosGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquarePhotosGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
