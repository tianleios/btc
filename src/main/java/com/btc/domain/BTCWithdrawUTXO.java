package com.btc.domain;

/**
 * Created by tianlei on 2018/一月/22.
 */
public class BTCWithdrawUTXO extends BTCUTXO  {

    /*取现 txid*/
    private  String withdrawTxid;

    public String getWithdrawTxid() {
        return withdrawTxid;
    }

    public void setWithdrawTxid(String withdrawTxid) {
        this.withdrawTxid = withdrawTxid;
    }
}
