package com.btc.service.impl;

import com.alibaba.fastjson.JSON;
import com.btc.Expection.BizExpection;
import com.btc.Expection.EBizError;
import com.btc.dao.BTCAddressMapper;
import com.btc.dao.BTCDefaultUTXOMapper;
import com.btc.dao.BTCWithdrawMapper;
import com.btc.dao.BTCWithdrawUTXOMapper;
import com.btc.domain.BTCAddress;
import com.btc.domain.BTCDefaultUTXO;
import com.btc.domain.BTCWithdraw;
import com.btc.domain.BTCWithdrawUTXO;
import com.btc.domain.original.*;
import com.btc.enumeration.EAddressType;
import com.btc.enumeration.EDefaultUTXOStatus;
import com.btc.enumeration.EWithdrawStatus;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by tianlei on 2018/一月/22.
 */
@Service("iBTCService")
public class BTCService implements IBTCService {

    @Autowired
    BTCBlockDataService blockDataService;

    @Autowired
    BTCDefaultUTXOMapper defaultUTXOMapper;

    @Autowired
    BTCWithdrawUTXOMapper withdrawUTXOMapper;

    @Autowired
    BTCAddressMapper addressMapper;

    @Autowired
    BTCWithdrawMapper withdrawMapper;

    // 1260212 第一次充值地址
    // 1260264
//  Long currentBlockHeight = Long.valueOf(1260212);
//  Long.valueOf(1260264);
//  1260418
    private static Long currentBlockHeight = Long.valueOf(1260418);

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

    @Override
    public List<BTCDefaultUTXO> selectUTXOList(String status) {

        return this.defaultUTXOMapper.selectList(status, 0, 100);

    }

    private OkHttpClient okHttpClient() {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        return client;

    }


    @Transactional(rollbackFor = RuntimeException.class)
//    @Scheduled(cron = "*/10 * * * * ?")
    public void exploreBlockChain() {

        //用于存储 UTXO
        List<BTCDefaultUTXO> defaultBtcUTXOList = new ArrayList<>();

        //存储提现地址的 utxo
        List<BTCWithdrawUTXO> withdrawBtcUTXOList = new ArrayList<>();

        //用于存储已花费
        List<BTCDefaultUTXO> defaultBtcSpendedTXOList = new ArrayList<>();

        //存储提现地址的 utxo
        List<BTCWithdrawUTXO> withdrawBtcSpendedUTXOList = new ArrayList<>();

        OkHttpClient okHttpClient = this.okHttpClient();
        // 查询的分页
        Integer pageNum = 0;

        while (true) {

            BTCTXs btctXs = this.blockDataService.getBlockTxs(currentBlockHeight, pageNum);

            if (btctXs == null) {
                throw new BizExpection(EBizError.BLOCK_GET_ERROR);
            }

            // 说明该区块已经遍历完了
            if (btctXs.getTxs().size() <= 0) {
                break;
            }

            //遍历交易
            for (BTCOriginalTx originalTx : btctXs.getTxs()) {

                //todo 暂不处理coinbase
                if (originalTx.getCoinBase() != null && originalTx.getCoinBase()) {
                    continue;
                }
                //处理提现，如果 txid 在 提现表中存在。那么那条提现申请应该已经成功
                //todo

                //遍历输入
                for (BTCVinUTXO vinUTXO : originalTx.getVin()) {

                    String outAddress = vinUTXO.getAddr();
                    if (outAddress == null || outAddress.length() == 0) {
                        continue;
                    }

                    BTCAddress address = this.addressMapper.selectByPrimaryKey(outAddress);
                    if (address == null) {
                        continue;
                    }

                    String addressType = address.getType();
                    if (addressType.equals(EAddressType.DEFAULT.getCode())) {

                        BTCDefaultUTXO spendBtcutxo = new BTCDefaultUTXO();
                        spendBtcutxo.setTxid(vinUTXO.getTxid());
                        spendBtcutxo.setVout(vinUTXO.getVout());
                        defaultBtcSpendedTXOList.add((BTCDefaultUTXO) spendBtcutxo);

                    } else if (addressType.equals(EAddressType.WITHDRAW.getCode())) {

                        BTCWithdrawUTXO btcutxo = new BTCWithdrawUTXO();
                        btcutxo.setTxid(vinUTXO.getTxid());
                        btcutxo.setVout(vinUTXO.getVout());
                        withdrawBtcSpendedUTXOList.add(btcutxo);

                    }

                }


                //遍历输出
                for (BTCVoutUTXO voutUTXO : originalTx.getVout()) {

                    List<String> addressList = voutUTXO.getScriptPubKey().getAddresses();
                    if (addressList == null || addressList.size() == 0) {
                        continue;
                    }

                    String outToAddress = addressList.get(0);
                    BTCAddress address = this.addressMapper.selectByPrimaryKey(outToAddress);

                    if (address == null) {
                        continue;
                    }

                    String addressType = address.getType();
                    if (addressType.equals(EAddressType.DEFAULT.getCode())) {
//普通utxo收集
                        BTCDefaultUTXO btcutxo = (BTCDefaultUTXO) this.convertOut(true, voutUTXO, originalTx.getTxid(), originalTx.getBlockheight(), EDefaultUTXOStatus.UN_COLLECTION.getCode());
                        defaultBtcUTXOList.add(btcutxo);

                    } else if (addressType.equals(EAddressType.WITHDRAW.getCode())) {
//提现utxo收集

                        BTCWithdrawUTXO btcutxo = (BTCWithdrawUTXO) this.convertOut(false, voutUTXO, originalTx.getTxid(), originalTx.getBlockheight(), EDefaultUTXOStatus.UN_COLLECTION.getCode());
                        withdrawBtcUTXOList.add(btcutxo);

                    }

                }


            }

            pageNum += 1;

        }

        //变更 输入状态,为已归集
        for (BTCDefaultUTXO utxo : defaultBtcSpendedTXOList) {

            BTCDefaultUTXO updateBtcDefaultUTXO = new BTCDefaultUTXO();
            updateBtcDefaultUTXO.setTxid(utxo.getTxid());
            updateBtcDefaultUTXO.setVout(utxo.getVout());
            updateBtcDefaultUTXO.setStatus(EDefaultUTXOStatus.COLLECTION_ED.getCode());
            this.defaultUTXOMapper.updateByPrimaryKeySelective(updateBtcDefaultUTXO);

        }


        for (BTCWithdrawUTXO utxo : withdrawBtcSpendedUTXOList) {

            BTCWithdrawUTXO updateBtcWithdrawUTXO = new BTCWithdrawUTXO();
            updateBtcWithdrawUTXO.setTxid(utxo.getTxid());
            updateBtcWithdrawUTXO.setVout(utxo.getVout());
            updateBtcWithdrawUTXO.setStatus(EWithdrawUTXOStatus.USE_ED.getCode());
            this.withdrawUTXOMapper.updateByPrimaryKeySelective(updateBtcWithdrawUTXO);

        }


        //存储 utxo
        if (defaultBtcUTXOList.size() > 0) {
            System.out.println("找到了相关utxo");
            this.defaultUTXOMapper.insertBatch(defaultBtcUTXOList);
        }

        if (withdrawBtcUTXOList.size() > 0) {
            this.withdrawUTXOMapper.insertBatch(withdrawBtcUTXOList);
        }

        //改变遍历的区块高度
        currentBlockHeight += 1;

    }

    @Override
    //@Scheduled(cron = "*/30 * * * * ?")
    synchronized public String collection() {

        BigDecimal min = BigDecimal.ZERO;
        String collectionAddress = "mnTjb2QnJrTtssXan9WFwheERn3VpEs9YU";
        String statusCode = EDefaultUTXOStatus.UN_COLLECTION.getCode();
        BigDecimal count = this.defaultUTXOMapper.selectCoinCount(statusCode);

        if (count.compareTo(min) <= 0) {
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
            Integer feePerByte = this.blockDataService.getFee();
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
                updateDefaultUTXO.setCollectionTime(new Date());
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

    @Override
    public synchronized void handleWithdraw() {

        //1.加载一定量的提现请求
        List<BTCWithdraw> withdrawList = this.withdrawMapper.selectList(EWithdrawStatus.WILL_HANDLE.getCode(), 0, 10);

        BigDecimal totalCount = BigDecimal.ZERO;
        for (BTCWithdraw withdraw : withdrawList) {

            totalCount.add(withdraw.getCount());

        }

        //2.1批量处理这些提现请求，寻找合适的 utxo
        BigDecimal haveCount = this.withdrawUTXOMapper.selectCoinCount(EWithdrawUTXOStatus.UN_USE.getCode());
        if (haveCount.compareTo(totalCount) <= 0) {
            // 相等或小于都应该是提现不成功，应为要有矿工费
            throw new BizExpection(EBizError.WITHDRAW_COUNT_LESS);
        }
        //2.2找出表中合适的utxo

        //3.变更提现请求状态，和utxo状态

        //4.进行广播

        //5.怎样判断提现成功

    }

    @Override
    public Boolean withdraw(String toAddress, BigDecimal count) {

        Date now = new Date();
        BTCWithdraw btcWithdraw = new BTCWithdraw();
        btcWithdraw.setApplyDatetime(now);
        btcWithdraw.setUpdateDatetime(now);
        btcWithdraw.setToAddress(toAddress);
        btcWithdraw.setCount(count);
        int affectCount = this.withdrawMapper.insert(btcWithdraw);
        return affectCount == 1;

    }

    /***
     *  由于归集广播出去之后可能失败，所以对 归集中的UTXO,并且归集时间距今 "一定时间" 的UTXO,进行检查。拿交易 txid去查，如果为null. 那么就证明这个交易失败了。变更UTXO状态为，
     *  待归集，等待下次归集
     */
    @Scheduled(cron = "*/10 * * * * ?")
    public void handleUnColeectionSuccessUTXO() {

        //暂时一次处理20条
        List<BTCDefaultUTXO> utxoList = this.defaultUTXOMapper.selectSuspiciousUTXOList(0, 20);

        List<BTCDefaultUTXO> shouldCancelList = new ArrayList<>();
        for (BTCDefaultUTXO utxo : utxoList) {

            String hash = utxo.getSpendTxid();
            BTCOriginalTx tx = this.blockDataService.queryTxHash(hash);
            if (tx != null) {
                // 交易还未确认，但交易存在，正在等待进行打包
                continue;
            }

            //交易可能已被取消
            //改变utxo转台
            BTCDefaultUTXO updateUTXO = new BTCDefaultUTXO();
            updateUTXO.setTxid(utxo.getTxid());
            updateUTXO.setVout(utxo.getVout());
            updateUTXO.setStatus(EDefaultUTXOStatus.UN_COLLECTION.getCode());
            //归集时间 由于已经归集过一次，不为NULL. 暂不改回NULL
            shouldCancelList.add(updateUTXO);

        }

        if (shouldCancelList.size() > 0) {
            this.handleTimeOutUTXO(shouldCancelList);
        }

    }

    @Transactional(rollbackFor = RuntimeException.class)
    public void handleTimeOutUTXO(List<BTCDefaultUTXO> utxoList) {

        utxoList.forEach(updateCondition -> {

            this.defaultUTXOMapper.updateByPrimaryKeySelective(updateCondition);

        });

    }

    /**
     * 100000000sta 转换为 1btc
     *
     * @param orgNum
     * @return
     */
    private BigDecimal convert(BigDecimal orgNum) {

        return orgNum.divide(BigDecimal.TEN.pow(8));

    }

    private String broadcastRawTxUrl() {

        return "https://testnet.blockexplorer.com/api/tx/send";
//        return "https://blockexplorer.com/api/tx/send";

    }

    private Object convertOut(boolean isDefaultUTXO, BTCVoutUTXO btcOut, String txid, Long blockHeight, String status) {

        BTCScriptPubKey scriptPubKey = btcOut.getScriptPubKey();

        if (isDefaultUTXO) {

            BTCDefaultUTXO btcutxo = new BTCDefaultUTXO();
            btcutxo.setAddress(scriptPubKey.getAddresses().get(0));
            btcutxo.setCount(btcOut.getValue());
            btcutxo.setScriptPubKey(scriptPubKey.getHex());
            btcutxo.setTxid(txid);
            btcutxo.setVout(btcOut.getN());
            btcutxo.setSyncTime(new Date());
            btcutxo.setBlockHeight(blockHeight);
            btcutxo.setStatus(status);
            return btcutxo;

        } else {

            BTCWithdrawUTXO btcutxo = new BTCWithdrawUTXO();
            btcutxo.setAddress(scriptPubKey.getAddresses().get(0));
            btcutxo.setCount(btcOut.getValue());
            btcutxo.setScriptPubKey(scriptPubKey.getHex());
            btcutxo.setTxid(txid);
            btcutxo.setVout(btcOut.getN());
            btcutxo.setSyncTime(new Date());
            btcutxo.setBlockHeight(blockHeight);
            btcutxo.setStatus(status);
            return btcutxo;

        }

    }


}
