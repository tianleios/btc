package com.btc.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class BTCWithdraw implements Serializable {
    private Integer id;

    private String toAddress;

    private BigDecimal count;

    private Date applyDatetime;

    private Date updateDatetime;

    private String status;

    private String withdrawTxid;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress == null ? null : toAddress.trim();
    }

    public BigDecimal getCount() {
        return count;
    }

    public void setCount(BigDecimal count) {
        this.count = count;
    }

    public Date getApplyDatetime() {
        return applyDatetime;
    }

    public void setApplyDatetime(Date applyDatetime) {
        this.applyDatetime = applyDatetime;
    }

    public Date getUpdateDatetime() {
        return updateDatetime;
    }

    public void setUpdateDatetime(Date updateDatetime) {
        this.updateDatetime = updateDatetime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getWithdrawTxid() {
        return withdrawTxid;
    }

    public void setWithdrawTxid(String withdrawTxid) {
        this.withdrawTxid = withdrawTxid == null ? null : withdrawTxid.trim();
    }
}