package com.btc.util;

import java.math.BigDecimal;

/**
 * Created by tianlei on 2018/一月/16.
 */
public class OfflineTxOutput {

    private String address;
    private BigDecimal amount;

    public OfflineTxOutput(String address, BigDecimal amount) {
        this.address = address;
        this.amount = amount;
    }

    public String getAddress() {
        return address;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
