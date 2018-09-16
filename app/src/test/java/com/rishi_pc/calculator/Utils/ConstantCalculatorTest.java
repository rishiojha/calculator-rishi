
package com.rishi_pc.calculator.Utils;

import com.rishi_pc.calculator.Models.ValuesModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link Constant_Calculator}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ConstantCalculatorTest {

    private Constant_Calculator mConstantCalculator;

    @Mock
    private ValuesModel mFirstValuesModel;

    @Mock
    private ValuesModel mSecondValuesModel;

    private static final int VALUE_A = 2;
    private static final int VALUE_B = 3;

    @Before
    public void setUp() {
        // Inject the Mockito mocks
        MockitoAnnotations.initMocks(this);

        when(mFirstValuesModel.getValue()).thenReturn(Integer.toString(VALUE_A));
        when(mSecondValuesModel.getValue()).thenReturn(Integer.toString(VALUE_B));

        mConstantCalculator = new Constant_Calculator();
    }

    @Test
    public void testAddition() {
        String expectedResult = Integer.toString(VALUE_A + VALUE_B);

        assertThat("Addition was executed correctly",
                mConstantCalculator.add(mFirstValuesModel, mSecondValuesModel), is(equalTo(expectedResult)));
    }

    @Test
    public void testSubtraction() {
        String expectedResult = Integer.toString(VALUE_A - VALUE_B);

        assertThat("Subtraction was executed correctly",
                mConstantCalculator.subtract(mFirstValuesModel, mSecondValuesModel), is(equalTo(expectedResult)));
    }

    @Test
    public void testMultiplication() {
        String expectedResult = Integer.toString(VALUE_A * VALUE_B);

        assertThat("Multiplication was executed correctly",
                mConstantCalculator.multiply(mFirstValuesModel, mSecondValuesModel), is(equalTo(expectedResult)));
    }

    @Test
    public void testDivision() {
        // Limit decimal digits as specified in the operand
        double digits = Math.pow(10, ValuesModel.MAX_DECIMAL_DIGITS);
        double result = Math.round(((double) VALUE_A / (double) VALUE_B) * digits) / digits;

        String expectedResult = Double.toString(result);

        assertThat("Division was executed correctly",
                mConstantCalculator.divide(mFirstValuesModel, mSecondValuesModel), is(equalTo(expectedResult)));
    }
}