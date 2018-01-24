package com.btc.domain.BTCInfo;

import java.util.List;

/**
 * Created by tianlei on 2018/一月/22.
 */
public class BTCTx {

    /*交易hash*/
    private String hash;

    /*输入列表*/
    private List<BTCInput> inputs;

    /*输出列表*/
    private List<BTCOut> out;

    private Integer ver;
    private Integer weight;
    private String relayed_by;
    private Integer lock_time;
    private Integer size;
    private Long time;
    private Long tx_index;
    private Integer vin_sz;
    private Integer vout_sz;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public List<BTCInput> getInputs() {
        return inputs;
    }

    public void setInputs(List<BTCInput> inputs) {
        this.inputs = inputs;
    }

    public List<BTCOut> getOut() {
        return out;
    }

    public void setOut(List<BTCOut> out) {
        this.out = out;
    }

    public Integer getVer() {
        return ver;
    }

    public void setVer(Integer ver) {
        this.ver = ver;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getRelayed_by() {
        return relayed_by;
    }

    public void setRelayed_by(String relayed_by) {
        this.relayed_by = relayed_by;
    }

    public Integer getLock_time() {
        return lock_time;
    }

    public void setLock_time(Integer lock_time) {
        this.lock_time = lock_time;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getTx_index() {
        return tx_index;
    }

    public void setTx_index(Long tx_index) {
        this.tx_index = tx_index;
    }

    public Integer getVin_sz() {
        return vin_sz;
    }

    public void setVin_sz(Integer vin_sz) {
        this.vin_sz = vin_sz;
    }

    public Integer getVout_sz() {
        return vout_sz;
    }

    public void setVout_sz(Integer vout_sz) {
        this.vout_sz = vout_sz;
    }
}
