package com.example.cryptotracker.Connections;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.cryptotracker.Supports.Assets;
import com.example.cryptotracker.Supports.Encrypt;
import com.example.cryptotracker.Supports.SharedPreferencesManager;

import org.json.JSONObject;

public class CovalentAPI {

    public static void getBalanceAsset(Context context, String network, String addr,
                                      OnRequestSuccess successCallback,
                                      OnRequestFailure failureCallback) {
        RequestQueue volleyQueue = Volley.newRequestQueue(context);
        String url = "https://api.covalenthq.com/v1/" + Assets.values.get(network) + "/address/" + addr + "/balances_v2/?key=cqt_rQ8GxWCJ4GjfhJc3FJj8Yh6DfbMK";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                successCallback::onSuccess,
                failureCallback::onFailure
        );

        volleyQueue.add(jsonObjectRequest);
    }
    public interface OnRequestSuccess {
        void onSuccess(JSONObject response);
    }

    public interface OnRequestFailure {
        void onFailure(VolleyError error);
    }
}
