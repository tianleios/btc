package com.btc.util;

//import javax.annotation.Nonnull;

/**
 * Created by tianlei on 2018/一月/16.
 */
public class OfflineTxInput {

    public String txid;
    public int vout;
    private String scriptPubKey;
    private String privateKey;


    public OfflineTxInput( String txid,
                   int vout,
                    String scriptPubKey,
                     String privateKey
    ) {
        this.scriptPubKey = scriptPubKey;
        this.txid = txid;
        this.vout = vout;
        this.privateKey = privateKey;
    }


    @Override
    public int hashCode() {
        return txid.hashCode() + vout;
    }

//    @Override
//    public boolean equals(Object obj) {
//        if (obj == null)
//            return false;
//        if (!(obj instanceof Bitcoin.TxInput))
//            return false;
//        OfflineTxInput other = (OfflineTxInput) obj;
//        return vout == other.vout && txid.equals(other.txid) && scriptPubKey.equals(other.scriptPubKey);
//
//    }

    public String getTxid() {
        return txid;
    }

    public int getVout() {
        return vout;
    }

    public String getScriptPubKey() {
        return scriptPubKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }
}





