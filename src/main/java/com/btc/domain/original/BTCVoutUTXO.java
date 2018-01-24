package com.btc.domain.original;

public class BTCVoutUTXO {

    private Integer n;
    private Integer vout;
    private String txid;
    private BTCScriptPubKey scriptPubKey;

    public Integer getN() {
        return n;
    }

    public void setN(Integer n) {
        this.n = n;
    }

    public Integer getVout() {
        return vout;
    }

    public void setVout(Integer vout) {
        this.vout = vout;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public BTCScriptPubKey getScriptPubKey() {
        return scriptPubKey;
    }

    public void setScriptPubKey(BTCScriptPubKey scriptPubKey) {
        this.scriptPubKey = scriptPubKey;
    }

    //     "value":"0.10000000",
//             "n":0,
//             "scriptPubKey":{
//        "hex":"a914870e662e12c16557c6a6df2760171069b21cd42b87",
//                "asm":"OP_HASH160 870e662e12c16557c6a6df2760171069b21cd42b OP_EQUAL",
//                "addresses":Array[1],
//                "type":"scripthash"
//    },

}
