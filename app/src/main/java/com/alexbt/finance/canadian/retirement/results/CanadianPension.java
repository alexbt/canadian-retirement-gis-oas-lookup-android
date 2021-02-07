package com.alexbt.finance.canadian.retirement.results;

import java.math.BigDecimal;


public class CanadianPension {
    private BigDecimal from;

    private BigDecimal to;

    private BigDecimal gis;

    private BigDecimal oas;

    private BigDecimal allowance;

    public BigDecimal getAllowance() {
        return allowance;
    }

    public BigDecimal getFrom() {
        return from;
    }

    public BigDecimal getGis() {
        return gis;
    }

    public BigDecimal getMaximumOas() {
        return oas;
    }

    public BigDecimal getTo() {
        return to;
    }

    public void setAllowance(BigDecimal allowance) {
        this.allowance = allowance;
    }

    public void setFrom(BigDecimal from) {
        this.from = from;
    }

    public void setGis(BigDecimal gis) {
        this.gis = gis;
    }

    public void setMaximumOas(BigDecimal oas) {
        this.oas = oas;
    }

    public void setTo(BigDecimal to) {
        this.to = to;
    }
}
