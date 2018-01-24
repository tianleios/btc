package com.btc.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.btc.Expection.BizExpection;
import com.btc.Expection.EBizError;
import com.btc.dao.BTCAddressMapper;
import com.btc.dao.BTCDefaultUTXOMapper;
import com.btc.dao.BTCWithdrawUTXOMapper;
import com.btc.domain.BTCAddress;
import com.btc.domain.BTCDefaultUTXO;
import com.btc.domain.BTCInfo.*;
import com.btc.domain.BTCUTXO;
import com.btc.domain.BTCWithdrawUTXO;
import com.btc.enumeration.EAddressType;
import com.btc.enumeration.EDefaultUTXOStatus;
import com.btc.enumeration.EWithdrawUTXOStatus;
import com.btc.service.IBTCService;
import com.btc.util.BitcoinOfflineRawTxBuilder;
import com.btc.util.OfflineTxInput;
import com.btc.util.OfflineTxOutput;
import okhttp3.*;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.RegTestParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by tianlei on 2018/一月/22.
 */
@Service("iBTCService")
public class BTCService implements IBTCService {

    @Autowired
    BTCDefaultUTXOMapper defaultUTXOMapper;

    @Autowired
    BTCWithdrawUTXOMapper withdrawUTXOMapper;

    @Autowired
    BTCAddressMapper addressMapper;


    private static Long currentBlockHeight = Long.valueOf(1260264);


    @Override
    public String address(boolean isTest) {

        NetworkParameters networkParameters = RegTestParams.get();
        ECKey ecKey = new ECKey();
        //私钥
        String privateKey = ecKey.getPrivateKeyAsHex();
        //需要转换为 base58 check ,然后进行存储
        String base58PrivateKey = ecKey.getPrivateKeyAsWiF(networkParameters);
        //公钥
        String publicKey = ecKey.getPublicKeyAsHex();
        String address = ecKey.toAddress(networkParameters).toString();
        if (address == null || address.length() <= 0) {

            throw new BizExpection(EBizError.ADDRESS_CREATE_ERROR);
        }

        BTCAddress btcAddress = new BTCAddress();
        btcAddress.setAddress(address);
        btcAddress.setPrivatekey(base58PrivateKey);
        btcAddress.setType(EAddressType.DEFAULT.getCode());
        int count = this.addressMapper.insert(btcAddress);
        if (count <= 0) {

            throw new BizExpection(EBizError.ADDRESS_CREATE_ERROR);

        }

        return address;
//        return "123";
    }

    @Override
    public void storeDefaultUTXO(List<BTCDefaultUTXO> utxoList) {

        int count = this.defaultUTXOMapper.insertBatch(utxoList);
        if (count != utxoList.size()) {
            throw new BizExpection(EBizError.UTXO_INSERT_ERROR);
        }

    }

    @Override
    public void storeWithdrawUTXO(List<BTCWithdrawUTXO> utxoList) {


    }

    private OkHttpClient okHttpClient() {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        return client;

    }

//    @Transactional(rollbackFor = RuntimeException.class)
    @Scheduled(cron = "*/10 * * * * ?")
    public void exploreBlockChain() {

        OkHttpClient okHttpClient = this.okHttpClient();
        //读取区块高度

        // 1260212 第一次充值地址
        // 1260264
//      Long currentBlockHeight = Long.valueOf(1260212);

        String requestUrl = this.blockUrl(currentBlockHeight);
        Request request = new Request.Builder()
                .get()
                .url(this.blockUrl(currentBlockHeight))
                .build();
        Call call = okHttpClient.newCall(request);
        try {

            Response response = call.execute();
            String jsonStr = response.body().string();

            //获取主链的block
            Map blocks = JSON.parseObject(jsonStr, Map.class);
            JSONArray jsonArray = (JSONArray) blocks.get("blocks");

            BTCBlock btcBlock = new BTCBlock();

            for (int i = 0; i < jsonArray.size(); i++) {
                BTCBlock block = jsonArray.getObject(i, BTCBlock.class);
                if (block.getMain_chain()) {
                    btcBlock = block;
                    break;
                }
            }

            if (btcBlock.getHash() == null) {
                throw new BizExpection("-1", "block 解析错误");
            }

            //用于存储 UTXO
            List<BTCDefaultUTXO> defaultBtcUTXOList = new ArrayList<>();

            //存储提现地址的 utxo
            List<BTCWithdrawUTXO> withdrawBtcUTXOList = new ArrayList<>();

            //用于存储已花费
            List<BTCDefaultUTXO> defaultBtcSpendedTXOList = new ArrayList<>();

            //存储提现地址的 utxo
            List<BTCWithdrawUTXO> withdrawBtcSpendedUTXOList = new ArrayList<>();


            //遍历交易
            for (BTCTx btcTx : btcBlock.getTx()) {

                //遍历输入
                btcTx.getInputs().forEach((BTCInput btcInput) -> {

                    BTCOut preOut = btcInput.getPrev_out();
                    if (preOut != null) {
                        String outAddress = preOut.getAddr();
                        BTCAddress address = this.addressMapper.selectByPrimaryKey(outAddress);
                        if (address != null) {
                            //
                            String addressType = address.getType();


                            if (addressType.equals(EAddressType.DEFAULT.getCode())) {

                                BTCDefaultUTXO spendBtcutxo = new BTCDefaultUTXO();
                                spendBtcutxo.setTxid(preOut.getHash());
                                btcutxo.setVout(preOut.getN());
                                defaultBtcSpendedTXOList.add((BTCDefaultUTXO) btcutxo);

                            } else if (addressType.equals(EAddressType.WITHDRAW.getCode())) {

                                BTCWithdrawUTXO btcutxo = new BTCWithdrawUTXO();
                                btcutxo.setTxid(btcTx.getHash());
                                btcutxo.setVout(preOut.getN());
                                withdrawBtcSpendedUTXOList.add(btcutxo);

                            }

                        }
                    }

                });

                //遍历输出
                for (BTCOut btcOut : btcTx.getOut()) {

                    String outToAddress = btcOut.getAddr();

                    BTCAddress address = this.addressMapper.selectByPrimaryKey(outToAddress);
                    if (address != null) {

                        String addressType = address.getType();
                        if (addressType.equals(EAddressType.DEFAULT.getCode())) {
//普通utxo收集
                            BTCDefaultUTXO btcutxo = this.convertOut(btcOut, btcTx.getHash(), btcBlock.getHeight(), EDefaultUTXOStatus.UN_COLLECTION.getCode());
                            defaultBtcUTXOList.add(btcutxo);

                        } else if (addressType.equals(EAddressType.WITHDRAW.getCode())) {
//提现utxo收集
                            BTCUTXO btcutxo = this.convertOut(btcOut, btcTx.getHash(), btcBlock.getHeight(), EWithdrawUTXOStatus.UN_USE.getCode());
                            withdrawBtcUTXOList.add((BTCWithdrawUTXO) btcutxo);

                        }

                    }
                }

            }


            //变更 输入状态,为已归集

            for (BTCDefaultUTXO utxo : defaultBtcSpendedTXOList) {

                BTCDefaultUTXO updateBtcDefaultUTXO = new BTCDefaultUTXO();
                updateBtcDefaultUTXO.setTxid(utxo.getTxid());
                updateBtcDefaultUTXO.setVout(utxo.getVout());
                updateBtcDefaultUTXO.setStatus(EDefaultUTXOStatus.COLLECTION_ED.getCode());
                this.defaultUTXOMapper.updateByPrimaryKeySelective(updateBtcDefaultUTXO);

            }

            if (withdrawBtcSpendedUTXOList.size() > 0) {

                withdrawBtcSpendedUTXOList.forEach(utxo -> {

                    BTCWithdrawUTXO updateBtcWithdrawUTXO = new BTCWithdrawUTXO();
                    updateBtcWithdrawUTXO.setTxid(utxo.getTxid());
                    updateBtcWithdrawUTXO.setVout(utxo.getVout());
                    updateBtcWithdrawUTXO.setStatus(EWithdrawUTXOStatus.USE_ED.getCode());
                    this.withdrawUTXOMapper.updateByPrimaryKeySelective(updateBtcWithdrawUTXO);


                });
            }

            //存储 utxo
            if (defaultBtcUTXOList.size() > 0) {
                this.defaultUTXOMapper.insertBatch(defaultBtcUTXOList);
            }

            if (withdrawBtcUTXOList.size() > 0) {
                this.withdrawUTXOMapper.insertBatch(withdrawBtcUTXOList);
            }

            //改变遍历的区块高度
            currentBlockHeight += 1;

        } catch (Exception e) {

            e.printStackTrace();
            //todo
            // 此处应该有log
        }

    }

    @Override
//    @Transactional(rollbackFor = RuntimeException.class)
    //    @Scheduled(cron = "*/30 * * * * ?")
    synchronized public String collection() {

        BigDecimal min = BigDecimal.ZERO;
        String collectionAddress = "mnTjb2QnJrTtssXan9WFwheERn3VpEs9YU";
        String statusCode = EDefaultUTXOStatus.UN_COLLECTION.getCode();
        BigDecimal count = this.defaultUTXOMapper.selectCoinCount(statusCode);

        if (count.compareTo(min) < 0) {
            //不符合归集条件
            return null;

        }

        //先取最多20条
        List<BTCDefaultUTXO> UTXOList = this.defaultUTXOMapper.selectList(statusCode, 0, 20);
        if (UTXOList == null || UTXOList.size() <= 0) {
            throw new BizExpection("-2", "无可归金额");
        }

        BitcoinOfflineRawTxBuilder rawTxBuilder = new BitcoinOfflineRawTxBuilder();

        BigDecimal totalCount = BigDecimal.valueOf(0);
        //
        for (BTCDefaultUTXO utxo : UTXOList) {

            String txid = utxo.getTxid();
            Integer vout = utxo.getVout();

            //统计总额
            totalCount = totalCount.add(utxo.getCount());

            BTCAddress btcAddress = this.addressMapper.selectByPrimaryKey(utxo.getAddress());
            OfflineTxInput offlineTxInput = new OfflineTxInput(txid, vout, utxo.getScriptPubKey(), btcAddress.getPrivatekey());
            rawTxBuilder.in(offlineTxInput);

        }

        //如何估算手续费，先预先给一个size,然后那这个size进行签名。
        //对签名的数据进行解码，拿到真实大小，然后进行旷工费的修正
        int preSize = BitcoinOfflineRawTxBuilder.calculateSize(rawTxBuilder.getInputs().size(), rawTxBuilder.getOutputs().size());

        int prePerByte = 200;

        //预估的手续费 sat
        int preFee = preSize * prePerByte;

        //预先缴纳的手续费
        BigDecimal preReturnCount = totalCount.subtract(BigDecimal.valueOf(preFee));

        //要进行单位转换
        OfflineTxOutput offlineTxOutput = new OfflineTxOutput(collectionAddress, this.convert(preReturnCount));
        rawTxBuilder.out(offlineTxOutput);

        try {

            String signResult = rawTxBuilder.offlineSign();
            //应该先处理我们的数据库，在进行广播。广播失败就回滚
            //拿到结果进行解码，获取真实Size
            rawTxBuilder.decodeRawTxToGetSizeAndTxid(signResult);
            //decode 成功之后，才能获取下面的数据
            Long trueSize = rawTxBuilder.getSize();

            //开始修正输入手续费
            Integer feePerByte = this.getFee();
            BigDecimal trueFee = BigDecimal.valueOf(trueSize).multiply(BigDecimal.valueOf(feePerByte));

            //重新构造交易, 转换为 1.3232 这种格式
            offlineTxOutput.setAmount(this.convert(totalCount.subtract(trueFee)));
            rawTxBuilder.reloadOut(offlineTxOutput);

            //得到最终结果
            String lastSignResult = rawTxBuilder.offlineSign();
            //重新decode 为了获取真实的txid
            rawTxBuilder.decodeRawTxToGetSizeAndTxid(lastSignResult);
            String spendTxid = rawTxBuilder.getTxid();

            // 这里更新数据库， 并且不能使用事务
            for (BTCDefaultUTXO utxo : UTXOList) {

                //更新了数据库状态, 并且不能使用事务
                //这里更新utxo的 status 和 spend_txid
                BTCDefaultUTXO updateDefaultUTXO = new BTCDefaultUTXO();
                updateDefaultUTXO.setTxid(utxo.getTxid());
                updateDefaultUTXO.setVout(utxo.getVout());
                updateDefaultUTXO.setStatus(EDefaultUTXOStatus.COLLECTION_ING.getCode());
                updateDefaultUTXO.setSpendTxid(spendTxid);

                this.defaultUTXOMapper.updateByPrimaryKeySelective(updateDefaultUTXO);

            }


            //2.进行广播
            OkHttpClient okHttpClient = new OkHttpClient();
            FormBody formBody = new FormBody.Builder().add("rawtx", lastSignResult).build();
            Request request = new Request.Builder()
                    .post(formBody)
                    .url(this.broadcastRawTxUrl())
                    .build();
            //
            Call call = okHttpClient.newCall(request);
            Response response = call.execute();
            String jsonStr = response.body().string();
            Map map = JSON.parseObject(jsonStr, HashMap.class);
            String trueTxid = (String) map.get("txid");
            if (trueTxid == null && trueTxid.length() <= 0) {

                throw new BizExpection(EBizError.UTXO_COLLECTION_ERROR);

            } else {

                return trueTxid;
            }

        } catch (Exception e) {

            throw new BizExpection("-100", e.getMessage());

        }

    }

    /* 100000000sta 转换为 1btc*/
    private BigDecimal convert(BigDecimal orgNum) {

        return orgNum.divide(BigDecimal.TEN.pow(8));

    }

    private Integer getFee() {

        OkHttpClient okHttpClient = new OkHttpClient();
        Request feeReq = new Request.Builder().get().url(this.feeUrl()).build();

        try {

            Call call = okHttpClient.newCall(feeReq);
            Response response = call.execute();
            String jsonStr = response.body().string();
            BTCFee fee = JSON.parseObject(jsonStr, BTCFee.class);

            // 应该读取一个配置值，获取手续费如果大于这个值，就取这个是
            Integer maxFeePerByte = 300;
            Integer fastFee = fee.getFastestFee();

            return fastFee > maxFeePerByte ? maxFeePerByte : fastFee;

        } catch (Exception e) {

            throw new BizExpection(EBizError.TX_FEE_ERROR);

        }

    }

    private String blockUrl(Long blockHeight) {
        String requestUrl = this.baseUrl() + "/block-height/" + blockHeight + "?format=json";
        return requestUrl;
    }

    private String broadcastRawTxUrl() {

        return "https://testnet.blockexplorer.com/api/tx/send";
//        return "https://blockexplorer.com/api/tx/send";

    }

    private String feeUrl() {
        //        {"fastestFee":250,"halfHourFee":240,"hourFee":130}
        //
        return "https://bitcoinfees.earn.com/api/v1/fees/recommended";
    }

    private String baseUrl() {
        return "https://testnet.blockchain.info";
    }

    private BTCDefaultUTXO convertOut(BTCOut btcOut, String txid, Long blockHeight, String status) {

        BTCDefaultUTXO btcutxo = new BTCDefaultUTXO();
        btcutxo.setAddress(btcOut.getAddr());
        btcutxo.setCount(btcOut.getValue());
        btcutxo.setScriptPubKey(btcOut.getScript());
        btcutxo.setTxid(txid);
        btcutxo.setVout(btcOut.getN());
        btcutxo.setSyncTime(new Date());
        btcutxo.setBlockHeight(blockHeight);
        btcutxo.setStatus(status);
        return btcutxo;
    }


}
