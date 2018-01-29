package com.btc.enumeration;

public enum EWithdrawStatus {

    /**
     *
     */
    WILL_HANDLE("0", "待处理"),
    ING_HANDLE("1", "处理中"),
    SUCCESS("2", "成功");

    private String code;
    private String msg;

    EWithdrawStatus(String code, String msg) {

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
