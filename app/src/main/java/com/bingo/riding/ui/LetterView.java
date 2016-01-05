package com.bingo.riding.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bingo.riding.interfaces.OnLetterViewClickListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by bingo on 16/1/4.
 */
public class LetterView extends LinearLayout{
    private OnLetterViewClickListener onLetterViewClickListener;

    public LetterView(Context context) {
        super(context);
        setOrientation(VERTICAL);
        updateLetters();
    }

    public LetterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        updateLetters();
    }

    private void updateLetters() {
        setLetters(getSortLetters());
    }

    /**
     * 设置快速滑动的字母集合
     */
    public void setLetters(List<Character> letters) {
        removeAllViews();
        for(Character content : letters) {
            TextView view = new TextView(getContext());
            view.setText(content.toString());
            addView(view);
        }

        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x = Math.round(event.getX());
                int y = Math.round(event.getY());
                for (int i = 0; i < getChildCount(); i++) {
                    TextView child = (TextView) getChildAt(i);
                    if (y > child.getTop() && y < child.getBottom()) {
                        if (onLetterViewClickListener != null){
                            onLetterViewClickListener.OnLetterClick(child.getText().toString().charAt(0));
                        }
                    }
                }
                return true;
            }
        });
    }

    /**
     * 默认的只包含 A-Z 的字母
     */
    private List<Character> getSortLetters() {
        List<Character> letterList = new ArrayList<Character>();
        for (char c = 'A'; c <= 'Z'; c++) {
            letterList.add(c);
        }
        return letterList;
    }

    public void setOnLetterViewClickListener(OnLetterViewClickListener onLetterViewClickListener) {
        this.onLetterViewClickListener = onLetterViewClickListener;
    }
}
