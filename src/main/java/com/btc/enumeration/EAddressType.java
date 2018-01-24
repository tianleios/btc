package com.btc.enumeration;

/**
 * Created by tianlei on 2018/一月/22.
 */
public enum  EAddressType {

    DEFAULT("0","普通账户地址"),
    WITHDRAW("1","提现地址"),
    ;

    private String code;
    private String msg;

    EAddressType(String code, String msg){

        this.code = code;
        this.msg = msg;

    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
