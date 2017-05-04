package de.thb.ue.android.utility.customized_classes;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.mikepenz.iconics.view.IconicsButton;

import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by scorp on 26.04.2017.
 */

public class BaseButton extends IconicsButton {

    private List<View.OnClickListener> onClickListeners;

    public BaseButton(Context context) {
        super(context);
        onClickListeners = new ArrayList<>();
    }

    public BaseButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        onClickListeners = new ArrayList<>();
    }

    public BaseButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        onClickListeners = new ArrayList<>();
    }

    public void addOnClickListener(View.OnClickListener listener){
        onClickListeners.add(listener);
    }

    @Override
    public boolean performClick() {
        for(View.OnClickListener listener : onClickListeners){
            listener.onClick(this);
        }
        return true;
    }

    @Override
    @Deprecated
    /**
     * Use addOnClickListener instead.
     *
     * Whatever is set here will not be executed.
     */
    public void setOnClickListener(@Nullable View.OnClickListener l) {
        throw new NotImplementedException("This method is deprecated for this subclass. Use addOnClickListener() instead!");
    }
}
