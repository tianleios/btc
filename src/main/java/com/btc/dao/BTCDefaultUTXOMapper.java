package com.btc.dao;

import com.btc.domain.BTCDefaultUTXO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface BTCDefaultUTXOMapper {

    int insertBatch(List<BTCDefaultUTXO> defaultUTXOList);

//    int updateUTXOStatus(@Param("txId") String txId, @Param("vout") Integer vout, @Param("toStatus") String toStatus);


    /* 获的utxo数量 */
    BigDecimal selectCoinCount(String status);

    List<BTCDefaultUTXO> selectList(@Param("status") String status,@Param("start") int start, @Param("limit") int limit);

//    int insert(BTCDefaultUTXO record);
//
//    int insertSelective(BTCDefaultUTXO record);
//
//
    int updateByPrimaryKeySelective(BTCDefaultUTXO record);
//
//    int updateByPrimaryKeyWithBLOBs(BTCDefaultUTXO record);
//
//    int updateByPrimaryKey(BTCDefaultUTXO record);
}