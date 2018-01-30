package com.btc.domain.BTCInfo;

/**
 * Created by tianlei on 2018/一月/23.
 */
public class BTCFee {

    /**
     *  {"fastestFee":250,"halfHourFee":240,"hourFee":130}
     * 1.fastestFee: The lowest fee (in satoshis per byte) that will currently result in the fastest transaction    *   confirmations (usually 0 to 1 block delay).
     * 2.halfHourFee: The lowest fee (in satoshis per byte) that will confirm transactions within half an hour      *   (with 90% probability).
     * 3.hourFee: The lowest fee (in satoshis per byte) that will confirm transactions within an hour (with 90%     * probability).
     */
    private Integer fastestFee;
    private Integer halfHourFee;
    private Integer hourFee;

    public Integer getFastestFee() {
        return fastestFee;
    }

    public void setFastestFee(Integer fastestFee) {
        this.fastestFee = fastestFee;
    }

    public Integer getHalfHourFee() {
        return halfHourFee;
    }

    public void setHalfHourFee(Integer halfHourFee) {
        this.halfHourFee = halfHourFee;
    }

    public Integer getHourFee() {
        return hourFee;
    }

    public void setHourFee(Integer hourFee) {
        this.hourFee = hourFee;
    }
}
