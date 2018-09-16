package com.rishi_pc.calculator.Activities;

import com.rishi_pc.calculator.Models.SignatureModels;
import com.rishi_pc.calculator.Models.ValuesModel;
import com.rishi_pc.calculator.Utils.Constant_Calculator;


public class CalculatorPresenter implements CalculatorContract.Presenter {

    private Constant_Calculator mConstantCalculator;
    private CalculatorContract.View mView;

    private ValuesModel mCurrentValuesModel;
    private ValuesModel mPreviousValuesModel;
    private SignatureModels mSignatureModels;
    private boolean hasLastInputOperator;
    private boolean hasLastInputEquals;
    private boolean isInErrorState;

    public CalculatorPresenter(Constant_Calculator constantCalculator,
                               CalculatorContract.View view,
                               ValuesModel currentValuesModel,
                               ValuesModel previousValuesModel) {
        mConstantCalculator = constantCalculator;
        mView = view;

        mCurrentValuesModel = currentValuesModel;
        mPreviousValuesModel = previousValuesModel;
        resetCalculator();
        updateDisplay();
    }

    @Override
    public void clearCalculation() {
        resetCalculator();
        updateDisplay();
    }

    @Override
    public SignatureModels getOperator() {
        return mSignatureModels;
    }

    @Override
    public void appendValue(String value) {
        if (hasLastInputOperator) {
            // Last input was an operator - start a new operand
            mPreviousValuesModel.setValue(mCurrentValuesModel.getValue());
            mCurrentValuesModel.reset();
        } else if (hasLastInputEquals) {
            // Last input was calculate - start a new calculation
            resetCalculator();
        }

        // Value should only be appended if the operand size is below the maximum operand size
        if (mCurrentValuesModel.getValue().length() < ValuesModel.MAX_LENGTH) {
            mCurrentValuesModel.appendValue(value);
            hasLastInputOperator = false;
            hasLastInputEquals = false;
            isInErrorState = false;
            updateDisplay();
        }
    }

    @Override
    public void appendOperator(String operator) {
        // Dont append new operators when in error state
        if (!isInErrorState) {
            if (mSignatureModels != SignatureModels.EMPTY && !hasLastInputOperator) {
                // Previous operator exists - perform partical calculation
                performCalculation();

                // When the partial calculation has led to an error state, stop here
                if (isInErrorState) {
                    return;
                }
            }

            mSignatureModels = SignatureModels.getOperator(operator);
            hasLastInputOperator = true;
            updateDisplay();
        }
    }

    /**
     * Executes a calculation with the current parameters in {@link CalculatorPresenter}.
     */
    @Override
    public void performCalculation() {
        String result = "";

        switch (mSignatureModels) {
            case PLUS:
                result = mConstantCalculator.add(mPreviousValuesModel, mCurrentValuesModel);
                break;
            case MINUS:
                result = mConstantCalculator.subtract(mPreviousValuesModel, mCurrentValuesModel);
                break;
            case MULTIPLY:
                result = mConstantCalculator.multiply(mPreviousValuesModel, mCurrentValuesModel);
                break;
            case DIVIDE:
                // Check for division by zero
                if (!mCurrentValuesModel.getValue().equals(ValuesModel.EMPTY_VALUE)) {
                    result = mConstantCalculator.divide(mPreviousValuesModel, mCurrentValuesModel);
                }
                break;
            default:
                result = mCurrentValuesModel.getValue();
        }

        if (result.equals("") || result.length() > ValuesModel.MAX_LENGTH) {
            switchToErrorState();
        } else {
            mCurrentValuesModel.setValue(result);
        }

        // Reset the previous operand and operator
        mPreviousValuesModel.reset();
        mSignatureModels = SignatureModels.EMPTY;
        hasLastInputEquals = true;
        updateDisplay();
    }

    private void switchToErrorState() {
        mCurrentValuesModel.setValue(ValuesModel.ERROR_VALUE);
        isInErrorState = true;
    }

    private void resetCalculator() {
        mCurrentValuesModel.reset();
        mPreviousValuesModel.reset();
        hasLastInputEquals = false;
        hasLastInputOperator = false;
        isInErrorState = false;
        mSignatureModels = SignatureModels.EMPTY;
    }

    private void updateDisplay() {
        mView.displayOperand(mCurrentValuesModel.getValue());
        mView.displayOperator(mSignatureModels.toString());
    }
}
