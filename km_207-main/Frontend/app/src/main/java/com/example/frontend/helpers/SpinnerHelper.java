package com.example.frontend.helpers;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * This class creates a default spinner object.
 */
public class SpinnerHelper {

    /**
     * create simple spinner with the first item as the hint
     * @param context
     * @param spinnerItemsResourceId
     * @return
     */
    public static ArrayAdapter<CharSequence> createSimpleSpinner(Context context, int spinnerItemsResourceId) {
        CharSequence[] spinnerItems = context.getResources().getTextArray(spinnerItemsResourceId);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(context, android.R.layout.simple_spinner_item, spinnerItems) {
            // Override methods for hint
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                if (position == 0) {
                    textView.setTextColor(Color.GRAY);
                } else {
                    textView.setTextColor(Color.BLACK);
                }
                return view;
            }

            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }
}

