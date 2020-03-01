package com.example.accounts;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;
    private int numCol;


    public SpacesItemDecoration(int space, int numCol) {
        this.space = space;
        this.numCol = numCol;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;
        // Add top margin only for the first item to avoid double space between items
        if (numCol == 1) {
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = space;
            } else {
                outRect.top = 0;
            }
        } else {
            if (parent.getChildLayoutPosition(view) == 0 || parent.getChildLayoutPosition(view) == 1) {
                outRect.top = space;
            } else {
                outRect.top = 0;
            }
            if (parent.getChildLayoutPosition(view) % 2 == 0) outRect.right = 0;
        }
    }
}