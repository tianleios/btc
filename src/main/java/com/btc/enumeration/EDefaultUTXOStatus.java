package com.btc.enumeration;

/**
 * Created by tianlei on 2018/一月/22.
 */
public enum EDefaultUTXOStatus  {

    UN_COLLECTION("0","未归集"),
    COLLECTION_ING("1","归集中"),
    COLLECTION_ED("2","已归集"),
    ;

    private String code;
    private String msg;

    EDefaultUTXOStatus(String code, String msg){

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
