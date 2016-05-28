package org.goldenroute.portfolioclient.adapter;

import android.widget.BaseAdapter;

import java.util.HashSet;
import java.util.Set;

public abstract class ToggleAdapter extends BaseAdapter {
    private Set<Long> mSelectedItemsIds = new HashSet<>();

    public void toggleSelection(Long id) {
        if (mSelectedItemsIds.contains(id)) {
            mSelectedItemsIds.remove(id);
        } else {
            mSelectedItemsIds.add(id);
        }
    }

    public void removeSelection() {
        mSelectedItemsIds = new HashSet<>();
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public Set<Long> getSelectedIds() {
        return mSelectedItemsIds;
    }
}
