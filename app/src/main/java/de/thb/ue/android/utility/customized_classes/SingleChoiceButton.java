package de.thb.ue.android.utility.customized_classes;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;

import de.thb.ue.android.data.VOs.ChoiceVO;
import thb.de.ue.android.R;

/**
 * Created by scorp on 11.05.2017.
 */

public class SingleChoiceButton extends BaseButton {
    private ChoiceVO mChoice;
    private boolean mIsVerticalButton;
    private boolean mTopDown;

    public SingleChoiceButton(Context context) {
        super(context);
    }

    public SingleChoiceButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SingleChoiceButton, 0, 0);

        try{
            mIsVerticalButton = typedArray.getBoolean(R.styleable.SingleChoiceButton_isVertical, false);
        } finally {
            typedArray.recycle();
        }

        if(mIsVerticalButton){
            final int gravity = getGravity();
            if(Gravity.isVertical(gravity) && (gravity& Gravity.VERTICAL_GRAVITY_MASK) == Gravity.BOTTOM) {
                setGravity((gravity& Gravity.HORIZONTAL_GRAVITY_MASK) | Gravity.CENTER);
                mTopDown = false;
            }else {
                setGravity(Gravity.CENTER);
                mTopDown = true;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        if(mIsVerticalButton){
            super.onMeasure(heightMeasureSpec, widthMeasureSpec);
            setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
        }
    }

    @Override
    protected void onDraw(Canvas canvas){

        if(mIsVerticalButton){
            TextPaint textPaint = getPaint();
            textPaint.setColor(getCurrentTextColor());
            textPaint.drawableState = getDrawableState();

            canvas.save();

            if(mTopDown){
                canvas.translate(getWidth(), 0);
                canvas.rotate(90);
            }else {
                canvas.translate(0, getHeight());
                canvas.rotate(-90);
            }
            canvas.translate(getCompoundPaddingLeft(), getExtendedPaddingTop());

            if(getLayout() != null){
                getLayout().draw(canvas);
            }
            canvas.restore();
        } else {
            super.onDraw(canvas);
        }
    }

    public void setmIsVerticalButton(boolean mIsVerticalButton) {
        this.mIsVerticalButton = mIsVerticalButton;
    }

    public ChoiceVO getmChoice() {
        return mChoice;
    }

    public void setmChoice(ChoiceVO mChoice) {
        this.mChoice = mChoice;
    }

}
