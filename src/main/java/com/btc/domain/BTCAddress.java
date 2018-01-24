package com.btc.domain;

import java.io.Serializable;

public class BTCAddress implements Serializable {

    /*地址*/
    private String address;

    /*私钥*/
    private String privatekey;

    /*地址类型*/
    private String type;

    private static final long serialVersionUID = 1L;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getPrivatekey() {
        return privatekey;
    }

    public void setPrivatekey(String privatekey) {
        this.privatekey = privatekey == null ? null : privatekey.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }
}