package com.btc.Expection;

/**
 * Created by tianlei on 2018/一月/22.
 */
public enum EBizError {

    ADDRESS_CREATE_ERROR("1000","地址创建失败"),

    UTXO_INSERT_ERROR("1200","交易插入失败"),
    TX_FEE_ERROR("1201","拉取手续费失败"),

    UTXO_COLLECTION_ERROR("1300","归集失败");


    private String code;
    private String msg;

    EBizError(String code, String msg){

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
