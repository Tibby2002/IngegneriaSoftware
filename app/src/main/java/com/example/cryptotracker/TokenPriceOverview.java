package com.example.cryptotracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Pair;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.cryptotracker.Supports.PricesOverview;
import com.example.cryptotracker.ui.home.HomeFragment;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TokenPriceOverview extends AppCompatActivity {
    boolean is_upper = false;
    List<Float> values = new ArrayList<>();
    List<Entry> entryGraph = new ArrayList<>();

    ScheduledExecutorService refresh;
    RequestQueue volleyQueue;


    private void populateGraphData(boolean landscape) {
        LineChart mChart;
        if (landscape) {
            mChart = findViewById(R.id.line_chart_landscape);
        } else {
            mChart = findViewById(R.id.line_chart);
        }
        mChart.animateX(3000, Easing.EasingOption.EaseInElastic);
        mChart.setTouchEnabled(true);
        mChart.setPinchZoom(true);
        for (int i = 0; i < values.size(); i++) {
            entryGraph.add(new Entry(i, values.get(i)));
        }
        LineDataSet set1;
        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(entryGraph);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(entryGraph, "Prezzo in chiusura della giornata");
            set1.setDrawIcons(false);
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.DKGRAY);
            set1.setCircleColor(Color.DKGRAY);
            set1.setLineWidth(1f);
            set1.setCircleRadius(2f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);
            if (Utils.getSDKInt() >= 18) {
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_blue);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(Color.DKGRAY);
            }
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);
            mChart.setData(data);
        }
    }

    private void populateInformation(boolean landscape) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime year_ago = LocalDateTime.now().plusYears(-1);
        LocalDateTime now = LocalDateTime.now();
        String url = "https://api.covalenthq.com/v1/pricing/historical_by_addresses_v2/" + getIntent().getExtras().getString("net") + "/USD/" + getIntent().getExtras().getString("token_address") + "/?key=cqt_rQ8GxWCJ4GjfhJc3FJj8Yh6DfbMK" +
                "&from=" + dtf.format(year_ago) + "&to=" + dtf.format(now);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                String jsonString = response.get("data").toString();
                JSONArray obj = new JSONArray(jsonString);
                JSONObject obj1 = obj.getJSONObject(0);
                JSONArray prices = obj1.getJSONArray("prices");
                for (int i = prices.length() - 1; i >= 0; i--) {
                    values.add(Float.parseFloat(prices.getJSONObject(i).getString("price").replaceAll(",", "")));
                }
                if (!landscape) {
                    String curr_price = obj1.getJSONArray("prices").getJSONObject(0).getString("price");
                    String yesterday_price = obj1.getJSONArray("prices").getJSONObject(1).getString("price");
                    TextView varianza = findViewById(R.id.varianza);
                    if (Double.parseDouble(curr_price.replaceAll(",", "")) >= Double.parseDouble(yesterday_price.replaceAll(",", ""))) {
                        is_upper = true;
                        varianza.setTextColor(Color.RED);
                    } else {
                        varianza.setTextColor(Color.RED);
                        is_upper = false;
                    }
                }
                populateGraphData(landscape);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
        });
        volleyQueue.add(jsonObjectRequest);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        volleyQueue = Volley.newRequestQueue(this);
        volleyQueue.cancelAll(request -> true);
        refresh = Executors.newSingleThreadScheduledExecutor();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_token_price_overview_landscape);
            populateInformation(true);
        } else {
            setContentView(R.layout.activity_token_price_overview);
            populateInformation(false);
            TextView name = findViewById(R.id.name);
            TextView prezzo = findViewById(R.id.prezzo);
            TextView varianza = findViewById(R.id.varianza);
            ImageView logo = findViewById(R.id.logo_token);
            String name_s = getIntent().getExtras().getString("name") + " (" + getIntent().getExtras().getString("sigla") + ")";
            name.setText(name_s);
            prezzo.setText(getIntent().getExtras().getString("price"));
            varianza.setText(getIntent().getExtras().getString("delta"));
            if (is_upper) {
                varianza.setTextColor(Color.GREEN);
            } else {
                varianza.setTextColor(Color.RED);
            }
            Picasso.get().load(getIntent().getExtras().getString("logo")).placeholder(R.drawable.currency_bitcoin).into(logo);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        refresh.shutdown();
    }

    @Override
    protected void onPause() {
        super.onPause();
        refresh.shutdown();
    }
}