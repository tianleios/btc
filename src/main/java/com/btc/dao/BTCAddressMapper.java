package com.btc.dao;

import com.btc.domain.BTCAddress;

public interface BTCAddressMapper {

    int getAddressCount(String address);
    BTCAddress selectByPrimaryKey(String address);

//    int deleteByPrimaryKey(String address);
//
    int insert(BTCAddress record);
//
//    int inserBatch(List<BTCAddress> addressList);
//
//    int insertSelective(BTCAddress record);
//
//    BTCAddress selectByPrimaryKey(String address);
//
//    int updateByPrimaryKeySelective(BTCAddress record);
//
//    int updateByPrimaryKey(BTCAddress record);
}