
package com.rishi_pc.calculator.Models;

public class ValuesModel {

    public static final String EMPTY_VALUE = "0";
    public static final String ERROR_VALUE = "ERROR";
    public static final int MAX_LENGTH = 10;
    public static final int MAX_DECIMAL_DIGITS = 1;

    private String mValue = EMPTY_VALUE;

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
    }

    public void appendValue(String value) {
        if (mValue.equals(EMPTY_VALUE)) {
            mValue = value;
        } else {
            mValue += value;
        }
    }

    public void reset() {
        mValue = EMPTY_VALUE;
    }
}
