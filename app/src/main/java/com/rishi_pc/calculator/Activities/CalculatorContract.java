
package com.rishi_pc.calculator.Activities;

import com.rishi_pc.calculator.Models.SignatureModels;

public class CalculatorContract {

    public interface View {

        void displayOperand(String calculation);

        void displayOperator(String operator);
    }

    interface Presenter {

        void clearCalculation();

        SignatureModels getOperator();

        void appendValue(String value);

        void appendOperator(String operator);

        void performCalculation();
    }
}
