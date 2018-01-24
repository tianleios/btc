package com.btc.domain.BTCInfo;

/**
 * Created by tianlei on 2018/一月/22.
 */
public class BTCInput {

    private Integer sequence;
    private String witness;
    private String script;

    private BTCOut prev_out;

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getWitness() {
        return witness;
    }

    public void setWitness(String witness) {
        this.witness = witness;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public BTCOut getPrev_out() {
        return prev_out;
    }

    public void setPrev_out(BTCOut prev_out) {
        this.prev_out = prev_out;
    }
}
