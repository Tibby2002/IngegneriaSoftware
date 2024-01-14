package com.example.cryptotracker;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.cryptotracker.Supports.NumbersView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ExchangeFragment extends Fragment {

    private Spinner spinnerSource;
    private Spinner spinnerTarget;

    private EditText inputExchangeText;
    private EditText outputExchangeText;

    private TextView outputExchangeTextLabel;

    private String convertedValue = "0";
    private String inputTextValue = "";

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_exchange, container, false);

        spinnerSource = rootView.findViewById(R.id.spinnerSource);
        spinnerTarget = rootView.findViewById(R.id.spinnerTarget);
        inputExchangeText = rootView.findViewById(R.id.inputExchangeText);
        outputExchangeText = rootView.findViewById(R.id.outputExchangeText);
        outputExchangeTextLabel = rootView.findViewById(R.id.textViewOutputValue);
        outputExchangeTextLabel.setVisibility(View.GONE);
        outputExchangeText.setVisibility(View.GONE);

        ImageButton switchButton = rootView.findViewById(R.id.switchButton);

        setAdapterSpinners();

        spinnerSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (!inputTextValue.isEmpty()) {
                    getConvertedValue((String) spinnerSource.getSelectedItem(), (String) spinnerTarget.getSelectedItem(), inputTextValue, new VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {
                            convertedValue = result;
                            updateOutputText(true);
                        }

                        @Override
                        public void onError() {
                            // Handle error if needed
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing when nothing is selected
            }
        });

        spinnerTarget.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (!inputTextValue.isEmpty()) {
                    getConvertedValue((String) spinnerSource.getSelectedItem(), (String) spinnerTarget.getSelectedItem(), inputTextValue, new VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {
                            convertedValue = result;
                            updateOutputText(true);
                        }

                        @Override
                        public void onError() {
                            // Handle error if needed
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing when nothing is selected
            }
        });

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sourcePosition = spinnerSource.getSelectedItemPosition();
                int targetPosition = spinnerTarget.getSelectedItemPosition();

                String sourceSelectedItem = (String) spinnerSource.getSelectedItem();
                String targetSelectedItem = (String) spinnerTarget.getSelectedItem();

                spinnerSource.setSelection(getPosition(spinnerSource, targetSelectedItem));
                spinnerTarget.setSelection(getPosition(spinnerTarget, sourceSelectedItem));

                ArrayAdapter<CharSequence> tempAdapter = (ArrayAdapter<CharSequence>) spinnerSource.getAdapter();
                spinnerSource.setAdapter((ArrayAdapter<CharSequence>) spinnerTarget.getAdapter());
                spinnerTarget.setAdapter(tempAdapter);

                spinnerSource.setSelection(targetPosition);
                spinnerTarget.setSelection(sourcePosition);

                getConvertedValue((String) spinnerSource.getSelectedItem(), (String) spinnerTarget.getSelectedItem(), inputTextValue, new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        convertedValue = result;
                        updateOutputText(true);
                    }

                    @Override
                    public void onError() {
                        // Handle error if needed
                    }
                });
            }
        });

        inputExchangeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().trim().isEmpty()) {
                    inputTextValue = editable.toString().replace(",", ".");
                    getConvertedValue((String) spinnerSource.getSelectedItem(), (String) spinnerTarget.getSelectedItem(), editable.toString().replace(",", "."), new VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {
                            convertedValue = result;
                            updateOutputText(true);
                        }

                        @Override
                        public void onError() {
                            // Handle error if needed
                        }
                    });
                } else {
                    convertedValue = "0";
                    updateOutputText(false);
                }
            }
        });

        return rootView;
    }

    private void updateOutputText(Boolean isVisible) {
        if (isVisible) {
            outputExchangeText.setVisibility(View.VISIBLE);
            outputExchangeTextLabel.setVisibility(View.VISIBLE);
        } else {
            outputExchangeText.setVisibility(View.GONE);
        }
        outputExchangeText.setText(convertedValue.replace(".", ","));
    }


    private int getPosition(Spinner spinner, String item) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinner.getAdapter();
        return adapter.getPosition(item);
    }

    private void setAdapterSpinners() {
        ArrayAdapter<CharSequence> adapterFirst = ArrayAdapter.createFromResource(getContext(),
                R.array.exchangeCriptoEnum, android.R.layout.simple_spinner_item);
        adapterFirst.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSource.setAdapter(adapterFirst);

        ArrayAdapter<CharSequence> adapterSecond = ArrayAdapter.createFromResource(getContext(),
                R.array.exchangeCurrencyEnum, android.R.layout.simple_spinner_item);
        adapterSecond.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTarget.setAdapter(adapterSecond);
    }

    private void getConvertedValue(String from, String to, String amount, VolleyCallback callback) {
        RequestQueue volleyQueue = Volley.newRequestQueue(getContext());
        String url = "https://api.freecryptoapi.com/v1/getConversion?from=" + from + "&to=" + to + "&amount=" + amount;

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer q8kxs48aexvovhay6sh2");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            BigDecimal value = new BigDecimal(response.optString("result", "0"));
            callback.onSuccess(value.toString());
        }, error -> {
            callback.onError();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }
        };
        volleyQueue.add(jsonObjectRequest);
    }

    interface VolleyCallback {
        void onSuccess(String result);

        void onError();
    }
}
