package com.btc.enumeration;

/**
 * Created by tianlei on 2018/一月/22.
 */
public enum EWithdrawUTXOStatus {

    UN_USE("0","未使用"),
    USE_ING("1","使用中"),
    USE_ED("2","已使用")
    ;

    private String code;
    private String msg;

    EWithdrawUTXOStatus(String code, String msg){

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
