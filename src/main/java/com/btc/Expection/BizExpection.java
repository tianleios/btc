package com.btc.Expection;

/**
 * Created by tianlei on 2018/一月/22.
 */
public class BizExpection extends RuntimeException {

    private String code;
    private String msg;

    public BizExpection( String code, String msg){

        this.code = code;
        this.msg = msg;

    }

    public BizExpection(EBizError bizError) {

        this(bizError.getCode(),bizError.getMsg());

    }

}
