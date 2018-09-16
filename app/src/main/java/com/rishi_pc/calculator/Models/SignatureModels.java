package com.rishi_pc.calculator.Models;

public enum SignatureModels {
    EMPTY(""),
    PLUS("+"),
    MINUS("-"),
    MULTIPLY("*"),
    DIVIDE("/");

    private String operator;

    SignatureModels(String operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return operator;
    }

    public static SignatureModels getOperator(String operator) {
        SignatureModels op = SignatureModels.EMPTY;

        switch (operator) {
            case "+":
                op = PLUS;
                break;
            case "-":
                op = MINUS;
                break;
            case "*":
                op = MULTIPLY;
                break;
            case "/":
                op = DIVIDE;
                break;
        }

        return op;
    }
}
