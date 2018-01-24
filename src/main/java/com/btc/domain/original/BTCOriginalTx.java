package com.btc.domain.original;

import com.btc.domain.BTCInfo.BTCInput;
import com.btc.domain.BTCInfo.BTCOut;

import java.math.BigDecimal;
import java.util.List;

public class BTCOriginalTx {

    private String txid;

    private BigDecimal blockheight;

    /*输入列表*/
    private List<BTCInput> vin;

    /*输出列表*/
    private List<BTCOut> vout;

}
