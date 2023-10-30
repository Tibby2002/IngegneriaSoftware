package com.example.cryptotracker.Supports;

public class NumbersView {


    // TextView 1
    private String mNumberInDigit2;
    private String mNumberInDigit;

    // TextView 1
    private String mNumbersInText;

    // create constructor to set the values for all the parameters of the each single view
    public NumbersView( String NumbersInDigit, String NumbersInText, String n2) {
        mNumberInDigit = NumbersInDigit;
        mNumbersInText = NumbersInText;
        mNumberInDigit2 = n2;
    }
    // getter method for returning the ID of the TextView 1
    public String getNumberInDigit() {
        return mNumberInDigit;
    }
    public String getNumberInDigit2() {
        return mNumberInDigit2;
    }
    public void setmNumberInDigit2(String s){mNumberInDigit2 = s;}

    // getter method for returning the ID of the TextView 2
    public String getNumbersInText() {
        return mNumbersInText;
    }

}

