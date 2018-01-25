package com.btc.domain.original;

import java.util.List;

public class BTCOriginalTx {

    private String txid;

    private Long blockheight;

    private Boolean isCoinBase;

    /*输入列表*/
    private List<BTCVinUTXO> vin;

    /*输出列表*/
    private List<BTCVoutUTXO> vout;

    public Boolean getCoinBase() {
        return isCoinBase;
    }

    public void setCoinBase(Boolean coinBase) {
        isCoinBase = coinBase;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public Long getBlockheight() {
        return blockheight;
    }

    public void setBlockheight(Long blockheight) {
        this.blockheight = blockheight;
    }

    public List<BTCVinUTXO> getVin() {
        return vin;
    }

    public void setVin(List<BTCVinUTXO> vin) {
        this.vin = vin;
    }

    public List<BTCVoutUTXO> getVout() {
        return vout;
    }

    public void setVout(List<BTCVoutUTXO> vout) {
        this.vout = vout;
    }
}
