package com.yanxinwei.bluetoothspppro.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by yanxinwei on 16/3/23.
 */
public class NumberTextView extends TextView{

    public NumberTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NumberTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setNumber(int number){
        setText(number+"");
    }
}
