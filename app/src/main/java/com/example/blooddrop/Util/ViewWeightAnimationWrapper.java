package com.example.blooddrop.Util;

import android.view.View;

import androidx.appcompat.widget.LinearLayoutCompat;

public class ViewWeightAnimationWrapper {
    private View view;

    public ViewWeightAnimationWrapper(View view) {
        if (view.getLayoutParams() instanceof LinearLayoutCompat.LayoutParams) {
            this.view = view;
        }else {
            throw new IllegalArgumentException("The view should have LinearLayout as parent");
        }

    }

    public void setWeight(float weight) {
        LinearLayoutCompat.LayoutParams layoutParams = (LinearLayoutCompat.LayoutParams) view.getLayoutParams();
        layoutParams.weight = weight;
        view.getParent().requestLayout();
    }

    public float getWeight() {
        return ((LinearLayoutCompat.LayoutParams) view.getLayoutParams()).weight;
    }
}