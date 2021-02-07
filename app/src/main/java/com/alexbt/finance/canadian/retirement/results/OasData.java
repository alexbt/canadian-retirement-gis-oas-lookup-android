package com.alexbt.finance.canadian.retirement.results;

import java.math.BigDecimal;


public class OasData {
    private int year;
    private BigDecimal lowerBound;

    private BigDecimal higherBound;

    private BigDecimal amount;
    private BigDecimal repaymentRate;

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setHigherBound(BigDecimal higherBound) {
        this.higherBound = higherBound;
    }

    public void setLowerBound(BigDecimal lowerBound) {
        this.lowerBound = lowerBound;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getHigherBound() {
        return higherBound;
    }

    public BigDecimal getLowerBound() {
        return lowerBound;
    }

    public int getYear() {
        return year;
    }

    public BigDecimal getRepaymentRate() {
        return repaymentRate;
    }

    public void setRepaymentRate(BigDecimal repaymentRate) {
        this.repaymentRate = repaymentRate;
    }
}
