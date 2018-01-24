package com.btc.domain.BTCInfo;

/**
 * Created by tianlei on 2018/一月/23.
 */
public class BTCFee {

//    {"fastestFee":250,"halfHourFee":240,"hourFee":130}
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
