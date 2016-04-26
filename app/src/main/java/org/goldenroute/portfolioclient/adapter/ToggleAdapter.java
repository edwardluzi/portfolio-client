package org.goldenroute.portfolioclient.adapter;

import android.widget.BaseAdapter;

import java.util.HashSet;
import java.util.Set;

public abstract class ToggleAdapter extends BaseAdapter {
    private Set<Long> mSelectedItemsIds = new HashSet<>();

    public void toggleSelection(Long id) {
        if (this.mSelectedItemsIds.contains(id)) {
            this.mSelectedItemsIds.remove(id);
        } else {
            this.mSelectedItemsIds.add(id);
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
