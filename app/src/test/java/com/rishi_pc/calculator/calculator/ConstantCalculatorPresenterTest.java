package com.rishi_pc.calculator.calculator;

import com.rishi_pc.calculator.Activities.CalculatorContract;
import com.rishi_pc.calculator.Activities.CalculatorPresenter;
import com.rishi_pc.calculator.Models.SignatureModels;
import com.rishi_pc.calculator.Models.ValuesModel;
import com.rishi_pc.calculator.Utils.Constant_Calculator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ConstantCalculatorPresenterTest {

    @Mock
    private Constant_Calculator mConstantCalculator;

    @Mock
    private CalculatorContract.View mView;

    @Mock
    private ValuesModel mPreviousValuesModel;

    @Mock
    private ValuesModel mCurrentValuesModel;

    private CalculatorPresenter mPresenter;

    private static final String SHORT_INPUT_A = "8";
    private static final String SHORT_INPUT_B = "5";
    private static final String LONG_INPUT = "38493";
    private static final SignatureModels[] SIGNATURE_MODELS = {SignatureModels.PLUS, SignatureModels.MULTIPLY, SignatureModels.DIVIDE, SignatureModels.MINUS};

    @Before
    public void setupCalculatorPresenter() {

        MockitoAnnotations.initMocks(this);

        when(mCurrentValuesModel.getValue()).thenReturn(ValuesModel.EMPTY_VALUE);
        when(mPreviousValuesModel.getValue()).thenReturn(ValuesModel.EMPTY_VALUE);

        when(mConstantCalculator.add(any(ValuesModel.class), any(ValuesModel.class))).thenReturn("0");
        when(mConstantCalculator.subtract(any(ValuesModel.class), any(ValuesModel.class))).thenReturn("0");
        when(mConstantCalculator.multiply(any(ValuesModel.class), any(ValuesModel.class))).thenReturn("0");
        when(mConstantCalculator.divide(any(ValuesModel.class), any(ValuesModel.class))).thenReturn("0");

        mPresenter = new CalculatorPresenter(mConstantCalculator, mView, mCurrentValuesModel, mPreviousValuesModel);
    }

    @Test
    public void afterInitialization_operatorShouldBeEmpty() {
        assertThat("SignatureModels is empty after initialization",
                mPresenter.getOperator(), is(equalTo(SignatureModels.EMPTY)));
    }

    @Test
    public void afterInitialization_presenterShouldSetDisplayToZero() {
        verify(mView).displayOperand(ValuesModel.EMPTY_VALUE);
    }

    @Test
    public void userEventDelete_shouldResetCalculator() {
        mPresenter.appendValue(SHORT_INPUT_B);
        mPresenter.appendOperator(SignatureModels.PLUS.toString());
        mPresenter.appendValue(SHORT_INPUT_B);

        // When the delete ("C") button is clicked
        mPresenter.clearCalculation();

        // All operands and operators should be removed from the calculation
        verify(mPreviousValuesModel, times(2)).reset();
        verify(mCurrentValuesModel, times(3)).reset();
        assertThat("SignatureModels was reset", mPresenter.getOperator(), is(equalTo(SignatureModels.EMPTY)));

        // ValuesModel and operator display should be reset
        verify(mView, times(5)).displayOperand(anyString());
        verify(mView, times(5)).displayOperator(anyString());
    }

    @Test
    public void numbersEntered_shouldBeStoredAsOperand() {
        for (int i = 0; i < LONG_INPUT.length(); i++) {
            mPresenter.appendValue(LONG_INPUT.substring(i, i + 1));
        }

        verify(mCurrentValuesModel, times(LONG_INPUT.length())).appendValue(anyString());
        verify(mView, times(LONG_INPUT.length() + 1)).displayOperand(anyString());
    }

    @Test
    public void operatorsEntered_shouldBeStoredAndDisplayed() {
        for (SignatureModels signatureModels : SIGNATURE_MODELS) {
            mPresenter.appendOperator(signatureModels.toString());
        }

        assertThat("SignatureModels has been stored and updated",
                mPresenter.getOperator(), is(equalTo(SIGNATURE_MODELS[SIGNATURE_MODELS.length - 1])));
        verify(mView, times(SIGNATURE_MODELS.length + 1)).displayOperator(anyString());
    }

    @Test
    public void numericalInputAfterOperator_shouldBeStoredAsNewOperand() {
        mPresenter.appendValue(SHORT_INPUT_A);
        when(mCurrentValuesModel.getValue()).thenReturn(SHORT_INPUT_A);
        mPresenter.appendOperator(SignatureModels.PLUS.toString());
        mPresenter.appendValue(SHORT_INPUT_B);

        verify(mPreviousValuesModel).setValue(SHORT_INPUT_A);
        verify(mCurrentValuesModel).appendValue(SHORT_INPUT_B);
    }

    @Test
    public void secondDistinctOperator_shouldExecutePartialCalculation() {
        String result = calculateResult(SHORT_INPUT_A, SHORT_INPUT_B, SignatureModels.PLUS);
        when(mConstantCalculator.add(any(ValuesModel.class), any(ValuesModel.class))).thenReturn(result);

        mPresenter.appendValue(SHORT_INPUT_A);
        mPresenter.appendOperator(SignatureModels.PLUS.toString());
        mPresenter.appendValue(SHORT_INPUT_B);
        when(mCurrentValuesModel.getValue()).thenReturn(result);
        mPresenter.appendOperator(SignatureModels.DIVIDE.toString());

        verify(mConstantCalculator).add(any(ValuesModel.class), any(ValuesModel.class));
        verify(mView, atLeastOnce()).displayOperand(result);
    }

    @Test
    public void operatorEnteredBeforeFirstOperand_shouldSetFirstOperandToZero() {
        when(mCurrentValuesModel.getValue()).thenReturn(ValuesModel.EMPTY_VALUE);
        mPresenter.appendOperator(SignatureModels.PLUS.toString());
        mPresenter.appendValue(SHORT_INPUT_A);

        verify(mPreviousValuesModel).setValue(ValuesModel.EMPTY_VALUE);
    }

    @Test
    public void userEventCalculate_shouldExecuteCalculationAndUpdateDisplay() {
        mPresenter.appendValue(SHORT_INPUT_B);
        mPresenter.appendOperator(SignatureModels.MULTIPLY.toString());
        mPresenter.appendValue(SHORT_INPUT_A);
        mPresenter.performCalculation();

        verify(mConstantCalculator).multiply(Mockito.any(ValuesModel.class), Mockito.any(ValuesModel.class));
        // Views should have been updated 5 times in total (1 initialization, 4 operations)
        verify(mView, times(5)).displayOperand(anyString());
        verify(mView, times(5)).displayOperator(anyString());
    }

    @Test
    public void resultCalculation_shouldResetOperator() {
        mPresenter.appendOperator(SignatureModels.MULTIPLY.toString());
        mPresenter.performCalculation();

        assertThat("SignatureModels has been reset",
                mPresenter.getOperator(), is(equalTo(SignatureModels.EMPTY)));
    }

    @Test
    public void numberAfterCalculation_shouldStartNewCalculation() {
        mPresenter.appendValue(SHORT_INPUT_A);
        mPresenter.appendOperator(SignatureModels.PLUS.toString());
        mPresenter.appendValue(SHORT_INPUT_B);
        mPresenter.performCalculation();
        mPresenter.appendValue(SHORT_INPUT_B);

        verify(mCurrentValuesModel, times(3)).reset();
        verify(mPreviousValuesModel, times(3)).reset();

        assertThat("Previous operator has been reset",
                mPresenter.getOperator(), is(equalTo(SignatureModels.EMPTY)));
    }

    @Test
    public void divisionByZero_shouldActivateErrorState() {
        performZeroDivision();

        verify(mCurrentValuesModel, times(1)).setValue(anyString());
        verify(mCurrentValuesModel, atLeastOnce()).setValue(ValuesModel.ERROR_VALUE);
    }

    @Test
    public void inErrorState_newOperatorsShouldBeForbidden() {
        performZeroDivision();

        mPresenter.appendOperator(SignatureModels.PLUS.toString());
        assertThat("New operator was not permitted",
                mPresenter.getOperator(), is(equalTo(SignatureModels.EMPTY)));
    }

    @Test
    public void inErrorState_clearShouldStartNewCalculation() {
        performZeroDivision();

        mPresenter.clearCalculation();
        mPresenter.appendValue(SHORT_INPUT_B);
        mPresenter.appendOperator(SignatureModels.PLUS.toString());

        assertThat("Clearing in error state started new calculation",
                mPresenter.getOperator(), is(equalTo(SignatureModels.PLUS)));
    }

    @Test
    public void inErrorState_numbersShouldStartNewCalculation() {
        performZeroDivision();

        mPresenter.appendValue(SHORT_INPUT_B);
        mPresenter.appendOperator(SignatureModels.PLUS.toString());

        assertThat("Number entered in error state started new calculation",
                mPresenter.getOperator(), is(equalTo(SignatureModels.PLUS)));
    }

    @Test
    public void calculationErrorsByOperatorEntered_shouldNotDisplayOperator() {
        prepareZeroDivision();
        mPresenter.appendOperator(SignatureModels.PLUS.toString());

        verify(mCurrentValuesModel, atLeastOnce()).setValue(ValuesModel.ERROR_VALUE);
        assertThat("SignatureModels has been reset",
                mPresenter.getOperator(), is(equalTo(SignatureModels.EMPTY)));
    }

    @Test
    public void maxLengthOfInputs_shouldBeLimited() {
        String value = "";
        for (int i = 0; i < ValuesModel.MAX_LENGTH + 1; i++) {
            mPresenter.appendValue(SHORT_INPUT_B);
            value += SHORT_INPUT_B;
            when(mCurrentValuesModel.getValue()).thenReturn(value);
        }

        verify(mCurrentValuesModel, times(ValuesModel.MAX_LENGTH)).appendValue(SHORT_INPUT_B);
    }

    @Test
    public void shouldBeAbleToPerformAddition() {
        mPresenter.appendValue(SHORT_INPUT_A);
        mPresenter.appendOperator(SignatureModels.PLUS.toString());
        mPresenter.appendValue(SHORT_INPUT_B);
        mPresenter.performCalculation();

        verify(mConstantCalculator).add(any(ValuesModel.class), any(ValuesModel.class));
    }

    @Test
    public void shouldBeAbleToPerformSubtraction() {
        mPresenter.appendValue(SHORT_INPUT_A);
        mPresenter.appendOperator(SignatureModels.MINUS.toString());
        mPresenter.appendValue(SHORT_INPUT_B);
        mPresenter.performCalculation();

        verify(mConstantCalculator).subtract(any(ValuesModel.class), any(ValuesModel.class));
    }

    @Test
    public void shouldBeAbleToPerformMultiplication() {
        mPresenter.appendValue(SHORT_INPUT_A);
        mPresenter.appendOperator(SignatureModels.MULTIPLY.toString());
        mPresenter.appendValue(SHORT_INPUT_B);
        mPresenter.performCalculation();

        verify(mConstantCalculator).multiply(any(ValuesModel.class), any(ValuesModel.class));
    }

    @Test
    public void shouldBeAbleToPerformDivision() {
        mPresenter.appendValue(SHORT_INPUT_A);
        mPresenter.appendOperator(SignatureModels.DIVIDE.toString());
        mPresenter.appendValue(SHORT_INPUT_B);
        // Mock of current operand must not return 0 to perform a division
        when(mCurrentValuesModel.getValue()).thenReturn(SHORT_INPUT_B);
        mPresenter.performCalculation();

        verify(mConstantCalculator).divide(any(ValuesModel.class), any(ValuesModel.class));
    }

    @Test
    public void oversizeResult_shouldSwitchToErrorState() {
        // Create oversize result string
        String result = "";
        for (int i = 0; i <= ValuesModel.MAX_LENGTH; i++) {
            result += SHORT_INPUT_A;
        }

        // Make the calculator return the desired result
        when(mConstantCalculator.add(any(ValuesModel.class), any(ValuesModel.class))).thenReturn(result);

        // Perform calculation to trigger mConstantCalculator.add()
        mPresenter.appendValue(SHORT_INPUT_B);
        mPresenter.appendOperator(SignatureModels.PLUS.toString());
        mPresenter.appendValue(SHORT_INPUT_A);
        mPresenter.performCalculation();

        verify(mCurrentValuesModel, atLeastOnce()).setValue(ValuesModel.ERROR_VALUE);
    }

    @Test
    public void calculationWithoutOperator_shouldReturnOperand() {
        mPresenter.appendValue(SHORT_INPUT_A);
        when(mCurrentValuesModel.getValue()).thenReturn(SHORT_INPUT_A);
        mPresenter.performCalculation();

        verify(mCurrentValuesModel).setValue(SHORT_INPUT_A);
    }

    private void prepareZeroDivision() {
        mPresenter.appendValue(SHORT_INPUT_B);
        mPresenter.appendOperator(SignatureModels.DIVIDE.toString());
        mPresenter.appendValue(ValuesModel.EMPTY_VALUE);
        when(mCurrentValuesModel.getValue()).thenReturn(ValuesModel.EMPTY_VALUE);
        verify(mCurrentValuesModel, never()).setValue(anyString());
    }

    private void performZeroDivision() {
        prepareZeroDivision();
        mPresenter.performCalculation();
    }

    private String calculateResult(String firstOperand, String secondOperand, SignatureModels signatureModels) {
        switch (signatureModels) {
            case PLUS:
                return Integer.toString(Integer.valueOf(firstOperand)
                        + Integer.valueOf(secondOperand));
            case MINUS:
                return Integer.toString(Integer.valueOf(firstOperand)
                        - Integer.valueOf(secondOperand));
            case MULTIPLY:
                return Integer.toString(Integer.valueOf(firstOperand)
                        * Integer.valueOf(secondOperand));
            case DIVIDE:
                return Integer.toString(Integer.valueOf(firstOperand)
                        / Integer.valueOf(secondOperand));
        }

        return "";
    }
}
