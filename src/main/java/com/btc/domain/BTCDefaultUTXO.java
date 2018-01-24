package com.btc.domain;

public class BTCDefaultUTXO extends BTCUTXO  {

    private String spendTxid;

    public String getSpendTxid() {
        return spendTxid;
    }

    public void setSpendTxid(String spendTxid) {
        this.spendTxid = spendTxid;
    }
}