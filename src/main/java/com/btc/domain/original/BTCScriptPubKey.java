package com.btc.domain.original;

import java.util.List;

public class BTCScriptPubKey {

    private String hex;
    private List<String> addresses;

    public String getHex() {
        return hex;
    }

    public void setHex(String hex) {
        this.hex = hex;
    }

    public List<String> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<String> addresses) {
        this.addresses = addresses;
    }


    //     "scriptPubKey":{
//        "hex":"a914870e662e12c16557c6a6df2760171069b21cd42b87",
//                "asm":"OP_HASH160 870e662e12c16557c6a6df2760171069b21cd42b OP_EQUAL",
//                "addresses":Array[1],
//                "type":"scripthash"
//    },
}
