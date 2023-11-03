package com.example.cryptotracker.Supports;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cryptotracker.R;

import java.util.ArrayList;

public class ModifyViewAdapter extends ArrayAdapter<ModifyView> {
    public ModifyViewAdapter(@NonNull Context context, ArrayList<ModifyView> arrayList) {
        super(context, 0, arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // convertView which is recyclable view
        View currentItemView = convertView;

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_view_modify, parent, false);
        }

        // get the position of the view from the ArrayAdapter
        ModifyView currentNumberPosition = getItem(position);
        assert currentNumberPosition != null;

        // then according to the position of the view assign the desired TextView 1 for the same
        TextView textView1 = currentItemView.findViewById(R.id.textViewChain);
        textView1.setText(currentNumberPosition.getChain());

        // then according to the position of the view assign the desired TextView 2 for the same
        TextView textView2 = currentItemView.findViewById(R.id.textViewAddress);
        textView2.setText(currentNumberPosition.getAddress());

        // then return the recyclable view
        return currentItemView;
    }
}

