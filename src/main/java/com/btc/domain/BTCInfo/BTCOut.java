package com.btc.domain.BTCInfo;

import java.math.BigDecimal;

/**
 * Created by tianlei on 2018/一月/22.
 */
public class BTCOut {

    private Boolean spent;
    private Long tx_index;
    private Integer type;
    private String addr;
    private BigDecimal value;
    private Integer n;
    private String script;


    public Boolean getSpent() {
        return spent;
    }

    public void setSpent(Boolean spent) {
        this.spent = spent;
    }

    public Long getTx_index() {
        return tx_index;
    }

    public void setTx_index(Long tx_index) {
        this.tx_index = tx_index;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Integer getN() {
        return n;
    }

    public void setN(Integer n) {
        this.n = n;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
}
