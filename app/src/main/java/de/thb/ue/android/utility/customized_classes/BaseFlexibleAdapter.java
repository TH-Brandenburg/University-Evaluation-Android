package de.thb.ue.android.utility.customized_classes;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;

/**
 * Created by scorp on 19.02.2017.
 */

public class BaseFlexibleAdapter<T extends IFlexible> extends FlexibleAdapter<T> {

    List<T> mItems;
    String bubbleText ="";

    public BaseFlexibleAdapter(@Nullable List<T> items) {
        super(items);
        mItems = items;
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public String onCreateBubbleText(int position) {
        return bubbleText;
    }
}
