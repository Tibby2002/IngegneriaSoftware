package com.example.cryptotracker.Supports;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cryptotracker.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PricesOverviewAdapter extends ArrayAdapter<PricesOverview> {
    public PricesOverviewAdapter(@NonNull Context context, ArrayList<PricesOverview> arrayList) {
        super(context, 0, arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // convertView which is recyclable view
        View currentItemView = convertView;

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_prices, parent, false);
        }

        // get the position of the view from the ArrayAdapter
        PricesOverview currentNumberPosition = getItem(position);
        assert currentNumberPosition != null;

        ImageView imageView1 = currentItemView.findViewById(R.id.logo);
        Picasso.get().load(currentNumberPosition.getSymbol()).placeholder(R.drawable.currency_bitcoin).into(imageView1);


        // then according to the position of the view assign the desired TextView 1 for the same
        TextView textView1 = currentItemView.findViewById(R.id.name_token);
        textView1.setText(currentNumberPosition.getName());

        // then according to the position of the view assign the desired TextView 2 for the same
        TextView textView2 = currentItemView.findViewById(R.id.name_id_token);
        textView2.setText(currentNumberPosition.getSigla());

        TextView textView3 = currentItemView.findViewById(R.id.price);
        textView3.setText(currentNumberPosition.getPrice());


        TextView textView4 = currentItemView.findViewById(R.id.percentage);
        ImageView imageView2 = currentItemView.findViewById(R.id.arrows);
        if(currentNumberPosition.isUpper()){
            textView4.setTextColor(Color.GREEN);
            imageView2.setImageResource(R.drawable.ic_upper);
        }else{
            textView4.setTextColor(Color.RED);
            imageView2.setImageResource(R.drawable.ic_down);
        }
        textView4.setText(currentNumberPosition.getDeltaPrice());

        // then return the recyclable view
        return currentItemView;
    }


}
