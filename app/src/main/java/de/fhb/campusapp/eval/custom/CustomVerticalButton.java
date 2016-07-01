package de.fhb.campusapp.eval.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.Button;

/**
 * Created by Admin on 02.01.2016.
 */
public class CustomVerticalButton extends Button {
    final boolean topDown;

    public CustomVerticalButton(Context context, AttributeSet attrs){
        super(context, attrs);
        final int gravity = getGravity();
        if(Gravity.isVertical(gravity) && (gravity&Gravity.VERTICAL_GRAVITY_MASK) == Gravity.BOTTOM) {
            setGravity((gravity&Gravity.HORIZONTAL_GRAVITY_MASK) | Gravity.CENTER);
            topDown = false;
        }else {
            setGravity(Gravity.CENTER);
            topDown = true;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    @Override
    protected void onDraw(Canvas canvas){
        TextPaint textPaint = getPaint();
        textPaint.setColor(getCurrentTextColor());
        textPaint.drawableState = getDrawableState();

        canvas.save();

        if(topDown){
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
    }
}

