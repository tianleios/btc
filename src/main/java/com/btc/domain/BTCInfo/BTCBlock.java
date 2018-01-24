package com.btc.domain.BTCInfo;

import java.util.List;

/**
 * Created by tianlei on 2018/一月/22.
 */
public class BTCBlock {

    private String hash;
    private Long ver;
    private String prev_block;
    private String mrkl_root;
    private Long time;
    private Long bits;
    private Long fee;
    private Long nonce;
    private Long n_tx;
    private Long size;
    private Long block_index;
    private Boolean main_chain;

    /*区块高度*/
    private Long height;

    /*交易列表*/
    private List<BTCTx> tx;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Long getVer() {
        return ver;
    }

    public void setVer(Long ver) {
        this.ver = ver;
    }

    public String getPrev_block() {
        return prev_block;
    }

    public void setPrev_block(String prev_block) {
        this.prev_block = prev_block;
    }

    public String getMrkl_root() {
        return mrkl_root;
    }

    public void setMrkl_root(String mrkl_root) {
        this.mrkl_root = mrkl_root;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getBits() {
        return bits;
    }

    public void setBits(Long bits) {
        this.bits = bits;
    }

    public Long getFee() {
        return fee;
    }

    public void setFee(Long fee) {
        this.fee = fee;
    }

    public Long getNonce() {
        return nonce;
    }

    public void setNonce(Long nonce) {
        this.nonce = nonce;
    }

    public Long getN_tx() {
        return n_tx;
    }

    public void setN_tx(Long n_tx) {
        this.n_tx = n_tx;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getBlock_index() {
        return block_index;
    }

    public void setBlock_index(Long block_index) {
        this.block_index = block_index;
    }

    public Boolean getMain_chain() {
        return main_chain;
    }

    public void setMain_chain(Boolean main_chain) {
        this.main_chain = main_chain;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public List<BTCTx> getTx() {
        return tx;
    }

    public void setTx(List<BTCTx> tx) {
        this.tx = tx;
    }
}
