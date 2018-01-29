package com.btc.domain;

import java.util.Date;

public class BTCDefaultUTXO extends BTCUTXO  {

    private String spendTxid;

    private Date collectionTime;

    public String getSpendTxid() {
        return spendTxid;
    }

    public void setSpendTxid(String spendTxid) {
        this.spendTxid = spendTxid;
    }

    public Date getCollectionTime() {
        return collectionTime;
    }

    public void setCollectionTime(Date collectionTime) {
        this.collectionTime = collectionTime;
    }
}