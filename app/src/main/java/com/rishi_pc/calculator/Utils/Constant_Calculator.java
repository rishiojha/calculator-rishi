

package com.rishi_pc.calculator.Utils;

import com.rishi_pc.calculator.Models.ValuesModel;

public class Constant_Calculator {

    public String add(ValuesModel firstValuesModel, ValuesModel secondValuesModel) {
        double result = getValue(firstValuesModel) + getValue(secondValuesModel);
        return formatResult(result);
    }

    public String subtract(ValuesModel firstValuesModel, ValuesModel secondValuesModel) {
        double result = getValue(firstValuesModel) - getValue(secondValuesModel);
        return formatResult(result);
    }

    public String multiply(ValuesModel firstValuesModel, ValuesModel secondValuesModel) {
        double result = getValue(firstValuesModel) * getValue(secondValuesModel);
        return formatResult(result);
    }

    public String divide(ValuesModel firstValuesModel, ValuesModel secondValuesModel) {
        double result = getValue(firstValuesModel) / getValue(secondValuesModel);
        return formatResult(result);
    }

    private double getValue(ValuesModel valuesModel) {
        return Double.valueOf(valuesModel.getValue());
    }

    private String formatResult(Double res) {
        // Limit digits
        double digits = Math.pow(10, ValuesModel.MAX_DECIMAL_DIGITS);
        res = Math.round(res * digits) / digits;

        // Split resulting float
        String result = Double.toString(res);
        String decimals = result.substring(0, result.indexOf("."));
        String fractionals = result.substring(result.indexOf(".") + 1);

        // Remove trailing zeros
        while (fractionals.length() > 0 && fractionals.substring(fractionals.length() - 1).equals("0")) {
            fractionals = fractionals.substring(0, fractionals.length() - 1);
        }

        if (fractionals.length() > 0) {
            return decimals + "." + fractionals;
        } else {
            return decimals;
        }
    }
}
