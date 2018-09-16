
package com.rishi_pc.calculator.Models;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for the {@link ValuesModel}.
 */
public class ValuesModelTest {

    private ValuesModel mValuesModel;

    private static final String VALUE = "537";

    @Before
    public void setUp() {
        mValuesModel = new ValuesModel();
    }

    @Test
    public void initialValue_shouldBeZero() {
        assertThat("Initial value was zero", mValuesModel.getValue(), is(equalTo(ValuesModel.EMPTY_VALUE)));
    }

    @Test
    public void appendValue_shouldAppendNumbers() {
        appendValues(VALUE);

        assertThat("Values were stored in the operand", mValuesModel.getValue(), is(equalTo(VALUE)));
    }

    @Test
    public void reset_shouldSetValueToZero() {
        appendValues(VALUE);
        mValuesModel.reset();

        assertThat("After reset, value was zero", mValuesModel.getValue(), is(equalTo(ValuesModel.EMPTY_VALUE)));
    }

    @Test
    public void setValue_shouldSetValue() {
        String value = "4823";
        appendValues(VALUE);
        mValuesModel.setValue(value);

        assertThat("Value was overwritten by setValue()", mValuesModel.getValue(), is(equalTo(value)));
    }

    private void appendValues(String value) {
        for (int i = 0; i < value.length(); i++) {
            mValuesModel.appendValue(value.substring(i, i + 1));
        }
    }
}