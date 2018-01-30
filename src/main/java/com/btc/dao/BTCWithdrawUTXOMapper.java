package com.btc.dao;

import com.btc.domain.BTCWithdrawUTXO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by tianlei on 2018/一月/22.
 */
public interface BTCWithdrawUTXOMapper {

    int insertBatch(List<BTCWithdrawUTXO> withdrawUTXOList);
    int updateUTXOToUsed(@Param("txId") String txId,@Param("vout") Integer vout);
    int updateByPrimaryKeySelective(BTCWithdrawUTXO record);

    List<BTCWithdrawUTXO> selectListCanUseWithdraw(@Param("status") String status,
                              @Param("start") int start,
                              @Param("limit") int limit,
                              @Param("orderBy") String orderBy,
                              @Param("sort") String sort);
    BigDecimal selectCoinCount(String status);

}
