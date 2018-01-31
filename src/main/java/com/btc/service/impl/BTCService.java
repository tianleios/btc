package com.btc.service.impl;

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
import okhttp3.OkHttpClient;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.RegTestParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    // 1260264 第二次测试的区块
    // 1260418 最后一次测试的地址
//  Long currentBlockHeight = Long.valueOf(1260212);
//  Long.valueOf(1260264);
//  1260418
    private static Long currentBlockHeight = Long.valueOf(1260212);

    //    private static final String stopSearchWithdrawCode = "0";
    private static final String broadcastSuccessCode = "1";
    private static final String withdrawCountLess = "-1";


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
    @Scheduled(cron = "*/10 * * * * ?")
    synchronized public String collection() {

        BigDecimal min = BigDecimal.ZERO;
        String collectionAddress = "mnTjb2QnJrTtssXan9WFwheERn3VpEs9YU";
        String statusCode = EDefaultUTXOStatus.UN_COLLECTION.getCode();
        BigDecimal count = this.defaultUTXOMapper.selectCoinCount(statusCode);

        if (count.compareTo(min) <= 0) {
            //不符合归集条件
            return null;

        }

        //先取最多40条
        List<BTCDefaultUTXO> UTXOList = this.defaultUTXOMapper.selectList(statusCode, 0, 40);
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

            //构造签名交易，输入
            OfflineTxInput offlineTxInput = new OfflineTxInput(txid, vout, utxo.getScriptPubKey(), btcAddress.getPrivatekey());
            rawTxBuilder.in(offlineTxInput);

        }

        //如何估算手续费，先预先给一个size,然后拿这个size进行签名
        //对签名的数据进行解码，拿到真实大小，然后进行矿工费的修正

        int preSize = BitcoinOfflineRawTxBuilder.calculateSize(UTXOList.size(), 1);
        int feePerByte = this.blockDataService.getFee();

        //计算出手续费
        int preFee = preSize * feePerByte;

        //构造输出
        OfflineTxOutput offlineTxOutput = new OfflineTxOutput(collectionAddress, this.convert(totalCount.subtract(BigDecimal.valueOf(preFee))));
        rawTxBuilder.out(offlineTxOutput);


        try {


            {
              // 只要输入输出定好，大小就不变，和输出value大小无关
//            String signResult = rawTxBuilder.offlineSign();
//            rawTxBuilder.decodeRawTxToGetSizeAndTxid(signResult);
//            Long trueSize = rawTxBuilder.getSize();
//            BigDecimal trueFee = BigDecimal.valueOf(feePerByte*trueSize);
//            OfflineTxOutput reloadOfflineOutPut = new OfflineTxOutput(collectionAddress,this.convert(totalCount.s(trueFee)));
//            rawTxBuilder.reloadOut(reloadOfflineOutPut);
//            String lastSign = rawTxBuilder.offlineSign();
            }

            String signResult = rawTxBuilder.offlineSign();

            //2.进行广播
            String trueTxid = this.blockDataService.broadcastRawTx(signResult);
            if (trueTxid != null) {

                for (BTCDefaultUTXO utxo : UTXOList) {

                    //这里更新utxo的 status 和 spend_txid
                    BTCDefaultUTXO updateDefaultUTXO = new BTCDefaultUTXO();
                    updateDefaultUTXO.setTxid(utxo.getTxid());
                    updateDefaultUTXO.setVout(utxo.getVout());
                    updateDefaultUTXO.setStatus(EDefaultUTXOStatus.COLLECTION_ING.getCode());
                    updateDefaultUTXO.setSpendTxid(trueTxid);
                    updateDefaultUTXO.setCollectionTime(new Date());
                    //
                    this.defaultUTXOMapper.updateByPrimaryKeySelective(updateDefaultUTXO);

                }
                return trueTxid;

            } else {

                throw new BizExpection(EBizError.UTXO_COLLECTION_ERROR);

            }


        } catch (Exception e) {

            throw new BizExpection("-100", e.getMessage());

        }


    }


    @Override
    @Transactional
    public synchronized void handleWithdraw() {

        try {

            this.handleWithdrawWithExecption();

        } catch (Exception e) {

            if (e instanceof BizExpection) {

                BizExpection bizE = (BizExpection) e;

                if (bizE.getCode().equals(broadcastSuccessCode)) {

                    System.out.println("广播成功");

                } else if (bizE.getCode().equals(withdrawCountLess)) {

                    System.out.println("提现账户余额不足");

                }

            }

        }

    }

    private void handleWithdrawWithExecption() throws Exception {

        //1.加载一定量的提现请求
        List<BTCWithdraw> withdrawList = this.withdrawMapper.selectList(EWithdrawStatus.WILL_HANDLE.getCode(), 0, 20);

        //1.2 计算提现的总额
        BigDecimal shouldWithdrawCount = BigDecimal.ZERO;
        for (BTCWithdraw withdraw : withdrawList) {

            shouldWithdrawCount = shouldWithdrawCount.add(withdraw.getCount());

        }

        //2.1批量处理这些提现请求，寻找合适的 utxo
        BigDecimal haveCount = this.withdrawUTXOMapper.selectCoinCount(EWithdrawUTXOStatus.UN_USE.getCode());
        if (haveCount.compareTo(shouldWithdrawCount) <= 0) {
            // 相等或小于都应该是提现不成功，应为要有矿工费
            // 大于也不一定成功，因为矿工费不能太小，容易交易失败
            throw new BizExpection(withdrawCountLess, "提现账户余额不足");
        }

        //2.2找出表中合适的utxo， count 降序查出

        //存储用于提现的utxo
        List<BTCWithdrawUTXO> shouldUseWithdrawUTXO = new ArrayList<>();
        BigDecimal hasSearchCount = BigDecimal.ZERO;
        int pageNum = 0;
        while (true) {

            List<BTCWithdrawUTXO> canUseWithdrawUTXOList = this.withdrawUTXOMapper.selectListCanUseWithdraw(EWithdrawUTXOStatus.UN_USE.getCode(), pageNum, 1, "count", "DESC");

            if (canUseWithdrawUTXOList == null || canUseWithdrawUTXOList.size() == 0) {
                // 已经遍历了所有的utxo
                // 这里肯定所有的utxo都不满足提现请求，通知 "请对提现账户充值"
                throw new BizExpection(withdrawCountLess, "提现账户余额不足");

            }

            for (BTCWithdrawUTXO canUseWithdraw : canUseWithdrawUTXOList) {

                shouldUseWithdrawUTXO.add(canUseWithdraw);
                hasSearchCount = hasSearchCount.add(canUseWithdraw.getCount());

                if (hasSearchCount.compareTo(shouldWithdrawCount) > 0) {
                    //还需要验证手续费是否满足

                    int preSize = BitcoinOfflineRawTxBuilder.calculateSize(withdrawList.size(), shouldUseWithdrawUTXO.size() + 1);
                    Integer feePerByte = this.blockDataService.getFee();
                    BigDecimal preFeeCount = BigDecimal.valueOf(preSize).multiply(BigDecimal.valueOf(feePerByte));

                    if (hasSearchCount.compareTo(shouldWithdrawCount.add(preFeeCount)) >= 0) {
                        //说明找到到的提现 utxo 基本满足需求

                        BitcoinOfflineRawTxBuilder offlineRawTxBuilder = new BitcoinOfflineRawTxBuilder();

                        //1. 构造输入， 同时改变utxo状态
                        for (BTCWithdrawUTXO withdrawUTXO : shouldUseWithdrawUTXO) {

                            BTCAddress address = this.addressMapper.selectByPrimaryKey(withdrawUTXO.getAddress());

                            OfflineTxInput offlineTxInput = new OfflineTxInput(withdrawUTXO.getTxid(), withdrawUTXO.getVout(), withdrawUTXO.getScriptPubKey(), address.getPrivatekey());
                            offlineRawTxBuilder.in(offlineTxInput);


                        }


                        //2. 构造输出,包含找零
                        for (BTCWithdraw withdraw : withdrawList) {

                            OfflineTxOutput txOutput = new OfflineTxOutput(withdraw.getToAddress(), this.convert(withdraw.getCount()));
                            offlineRawTxBuilder.out(txOutput);

                        }

                        //计算需要的找零, 现在是随机找零到一个提现地址
                        BigDecimal backCount = hasSearchCount.subtract(shouldWithdrawCount).subtract(preFeeCount);
                        if (backCount.compareTo(BigDecimal.ZERO) > 0) {

                            String backAddress = canUseWithdrawUTXOList.get(0).getAddress();
                            OfflineTxOutput backOutput = new OfflineTxOutput(backAddress, this.convert(backCount));
                            offlineRawTxBuilder.out(backOutput);

                        }

                        //3. 进行交易签名
                        String offlineSign = offlineRawTxBuilder.offlineSign();
                        String trueTxid = this.blockDataService.broadcastRawTx(offlineSign);
                        if (trueTxid != null) {

                            for (BTCWithdrawUTXO withdrawUTXO : shouldUseWithdrawUTXO) {

                                BTCWithdrawUTXO update = new BTCWithdrawUTXO();
                                update.setTxid(withdrawUTXO.getTxid());
                                update.setVout(withdrawUTXO.getVout());
                                update.setStatus(EWithdrawUTXOStatus.USE_ING.getCode());
                                update.setWithdrawTxid(trueTxid);
                                this.withdrawUTXOMapper.updateByPrimaryKeySelective(update);

                            }

                            throw new BizExpection(broadcastSuccessCode, "广播成功");

                        } else {

                            throw new BizExpection(EBizError.UTXO_COLLECTION_ERROR);

                        }

                    } else {
                        //继续寻找
                        continue;

                    }

                }

            }

            pageNum += 1;

        }

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
    public void handleUnCollectionSuccessUTXO() {

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

    private Object convertOut(boolean isDefaultUTXO, BTCVoutUTXO btcOut, String txid, Long blockHeight, String
            status) {

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
