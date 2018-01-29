package com.btc.dao;

import com.btc.domain.BTCWithdraw;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BTCWithdrawMapper {

    List<BTCWithdraw> selectList(@Param("status") String status,@Param("start") int start, @Param("limit") int limit);


    int deleteByPrimaryKey(Integer id);

    int insert(BTCWithdraw record);

    int insertSelective(BTCWithdraw record);

    BTCWithdraw selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BTCWithdraw record);

    int updateByPrimaryKey(BTCWithdraw record);
}