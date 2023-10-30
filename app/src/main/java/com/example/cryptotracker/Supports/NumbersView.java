package com.example.cryptotracker.Supports;

public class NumbersView {


    // TextView 1
    private String mNumberInDigit;

    // TextView 1
    private String mNumbersInText;

    // create constructor to set the values for all the parameters of the each single view
    public NumbersView( String NumbersInDigit, String NumbersInText) {
        mNumberInDigit = NumbersInDigit;
        mNumbersInText = NumbersInText;
    }
    // getter method for returning the ID of the TextView 1
    public String getNumberInDigit() {
        return mNumberInDigit;
    }

    // getter method for returning the ID of the TextView 2
    public String getNumbersInText() {
        return mNumbersInText;
    }
}

