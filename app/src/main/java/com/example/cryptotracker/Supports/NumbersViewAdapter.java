package com.example.cryptotracker.Supports;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cryptotracker.R;

import java.util.ArrayList;

public class NumbersViewAdapter extends ArrayAdapter<NumbersView> {

    // invoke the suitable constructor of the ArrayAdapter class
    public NumbersViewAdapter(@NonNull Context context, ArrayList<NumbersView> arrayList) {

        // pass the context and arrayList for the super
        // constructor of the ArrayAdapter class
        super(context, 0, arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // convertView which is recyclable view
        View currentItemView = convertView;

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_view, parent, false);
        }

        // get the position of the view from the ArrayAdapter
        NumbersView currentNumberPosition = getItem(position);
        assert currentNumberPosition != null;

        // then according to the position of the view assign the desired TextView 1 for the same
        TextView textView1 = currentItemView.findViewById(R.id.textView2);
        textView1.setText(currentNumberPosition.getNumberInDigit());
        textView1.setTextColor(Color.WHITE);

        // then according to the position of the view assign the desired TextView 2 for the same
        TextView textView2 = currentItemView.findViewById(R.id.textView3);
        textView2.setText(currentNumberPosition.getNumbersInText());
        textView2.setTextColor(Color.WHITE);

        TextView textView3 = currentItemView.findViewById(R.id.textView1);
        textView3.setText(currentNumberPosition.getNumberInDigit2());
        textView3.setTextColor(Color.WHITE);

        // then return the recyclable view
        return currentItemView;
    }
}

