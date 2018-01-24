package com.btc.dao;

import com.btc.domain.BTCWithdrawUTXO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by tianlei on 2018/一月/22.
 */
public interface BTCWithdrawUTXOMapper {

    int insertBatch(List<BTCWithdrawUTXO> withdrawUTXOList);
    int updateUTXOToUsed(@Param("txId") String txId,@Param("vout") Integer vout);
    int updateByPrimaryKeySelective(BTCWithdrawUTXO record);

}
