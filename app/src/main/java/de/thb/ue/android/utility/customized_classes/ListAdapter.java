package de.thb.ue.android.utility.customized_classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.HashMap;

/**
 * Created by Sebastian Müller on 12.01.2017.
 */

public class ListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private HashMap<Integer, String> uuidMap;


    public ListAdapter(Context context) {
        super();
        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}
