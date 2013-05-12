package com.jrajav.bedtime;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class TimerSetupView
extends LinearLayout
implements Button.OnClickListener, Button.OnLongClickListener {

    protected int mInputSize = 5;

    protected final Button mNumbers [] = new Button [10];
    protected int mInput [] = new int [mInputSize];
    protected int mInputPointer = -1;
    protected Button mLeft, mRight;
    protected Button mStart;
    protected ImageButton mDelete;
    protected TimerView mEnteredTime;
    protected final Context mContext;

    public TimerSetupView(Context context) {
        this(context, null);
    }

    public TimerSetupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater layoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(getLayoutId(), this);
    }

    protected int getLayoutId() {
        return R.layout.time_setup_view;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        View view1 = findViewById(R.id.first);
        View view2 = findViewById(R.id.second);
        View view3 = findViewById(R.id.third);
        View view4 = findViewById(R.id.fourth);
        mEnteredTime = (TimerView) findViewById(R.id.timer_time_text);
        mDelete = (ImageButton) findViewById(R.id.delete);
        mDelete.setOnClickListener(this);
        mDelete.setOnLongClickListener(this);

        mNumbers[1] = (Button) view1.findViewById(R.id.key_left);
        mNumbers[2] = (Button) view1.findViewById(R.id.key_middle);
        mNumbers[3] = (Button) view1.findViewById(R.id.key_right);

        mNumbers[4] = (Button) view2.findViewById(R.id.key_left);
        mNumbers[5] = (Button) view2.findViewById(R.id.key_middle);
        mNumbers[6] = (Button) view2.findViewById(R.id.key_right);

        mNumbers[7] = (Button) view3.findViewById(R.id.key_left);
        mNumbers[8] = (Button) view3.findViewById(R.id.key_middle);
        mNumbers[9] = (Button) view3.findViewById(R.id.key_right);

        mLeft = (Button) view4.findViewById(R.id.key_left);
        mNumbers[0] = (Button) view4.findViewById(R.id.key_middle);
        mRight = (Button) view4.findViewById(R.id.key_right);
        setLeftRightEnabled(false);

        for (int i = 0; i < 10; i++) {
            mNumbers[i].setOnClickListener(this);
            mNumbers [i].setText(String.format("%d", i));
            mNumbers [i].setTag(R.id.numbers_key, Integer.valueOf(i));
        }
        updateTime();
    }

    public void registerStartButton(Button start) {
        mStart = start;
    }

    public void updateStartButton() {
        boolean enabled = mInputPointer != -1;
        if (mStart != null) {
            mStart.setEnabled(enabled);
        }
    }

    public void updateDeleteButton() {
        boolean enabled = mInputPointer != -1;
        if (mDelete != null) {
            mDelete.setEnabled(enabled);
        }
    }

    @Override
    public void onClick(View view) {
        doOnClick(view);
        updateStartButton();
        updateDeleteButton();
    }

    protected void doOnClick(View view) {
        Integer value = (Integer) view.getTag(R.id.numbers_key);
        // A number was pressed
        if (value != null) {
            // pressing "0" as the first digit does nothing
            if (mInputPointer == -1 && value == 0) {
                return;
            }
            if (mInputPointer < mInputSize - 1) {
                for (int i = mInputPointer; i >= 0; i--) {
                    mInput[i + 1] = mInput[i];
                }
                mInputPointer++;
                mInput[0] = value;
                updateTime();
            }
            return;
        }

        // other keys
        if (view == mDelete) {
            if (mInputPointer >= 0) {
                for (int i = 0; i < mInputPointer; i++) {
                    mInput[i] = mInput[i + 1];
                }
                mInput[mInputPointer] = 0;
                mInputPointer--;
                updateTime();
            }
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (view == mDelete) {
            reset();
            updateStartButton();
            updateDeleteButton();
            return true;
        }
        return false;
    }

    protected void updateTime() {
        mEnteredTime.setTime(-1, mInput[4], mInput[3], mInput[2], mInput[1] * 10 + mInput[0]);
    }

    public void reset() {
        for (int i = 0; i < mInputSize; i++) {
            mInput[i] = 0;
        }
        mInputPointer = -1;
        updateTime();
    }

    public int getTime() {
        return mInput[4] * 3600 + mInput[3] * 600 + mInput[2] * 60 + mInput[1] * 10 + mInput[0];
    }

    public void saveEntryState(Bundle out, String key) {
        out.putIntArray(key, mInput);
    }

    public void restoreEntryState(Bundle in, String key) {
        int[] input = in.getIntArray(key);
        if (input != null && mInputSize == input.length) {
            for (int i = 0; i < mInputSize; i++) {
                mInput[i] = input[i];
                if (mInput[i] != 0) {
                    mInputPointer = i;
                }
            }
            updateTime();
        }
    }

    protected void setLeftRightEnabled(boolean enabled) {
        mLeft.setEnabled(enabled);
        mRight.setEnabled(enabled);
        if (!enabled) {
            mLeft.setContentDescription(null);
            mRight.setContentDescription(null);
        }
    }

}
