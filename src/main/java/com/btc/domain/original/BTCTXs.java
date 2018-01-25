package com.btc.domain.original;

import java.util.List;

public class BTCTXs {

    private Integer pagesTotal;
    private List<BTCOriginalTx> txs;

    public Integer getPagesTotal() {
        return pagesTotal;
    }

    public void setPagesTotal(Integer pagesTotal) {
        this.pagesTotal = pagesTotal;
    }

    public List<BTCOriginalTx> getTxs() {
        return txs;
    }

    public void setTxs(List<BTCOriginalTx> txs) {
        this.txs = txs;
    }
}
