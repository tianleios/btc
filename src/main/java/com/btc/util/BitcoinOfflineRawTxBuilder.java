package com.btc.util;

import com.azazar.bitcoin.jsonrpcclient.BitcoinException;
import com.azazar.bitcoin.jsonrpcclient.BitcoinJSONRPCClient;

import java.math.BigDecimal;
import java.util.*;

public class BitcoinOfflineRawTxBuilder {


    public BitcoinOfflineRawTxBuilder() {
    }

    private Long size;
    private String txid;

    private LinkedHashSet<OfflineTxInput> inputs = new LinkedHashSet();
    private List<OfflineTxOutput> outputs = new ArrayList();

    //离线签名使用 测试网络
    private BitcoinJSONRPCClient bitcoinJSONRPCClient = new BitcoinJSONRPCClient(true);

    // in
    public BitcoinOfflineRawTxBuilder in(OfflineTxInput offlineTxInput) {

        inputs.add(offlineTxInput);
        return this;
    }

    public BitcoinOfflineRawTxBuilder in(List<OfflineTxInput> offlineTxInputs) {

        inputs.addAll(offlineTxInputs);
        return this;
    }

    //out
    public BitcoinOfflineRawTxBuilder out(OfflineTxOutput offlineTxOutput) {
//        if (offlineTxOutput.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
//
//            return this;
//
//        }

        outputs.add(offlineTxOutput);
        return this;
    }

    public BitcoinOfflineRawTxBuilder out(List<OfflineTxOutput> offlineTxOutputs) {
//        if (offlineTxOutput.getAmount().compareTo(BigDecimal.ZERO) <= 0)
//            return this;

        outputs.addAll(offlineTxOutputs);
        return this;
    }

    /* 清楚原来的输出，怎家新的输出*/
    public void reloadOut(List<OfflineTxOutput> offlineTxOutputs) {
        outputs.clear();
        outputs.addAll(offlineTxOutputs);
    }

    public void reloadOut(OfflineTxOutput offlineTxOutput) {
        outputs.clear();
        outputs.add(offlineTxOutput);
    }

    //create 创建交易
    private String create(boolean isIncludeScriptPubKey) throws BitcoinException {

        //in
        List<Map> pInputs = this.getInPut(isIncludeScriptPubKey);

        //out
        Map<String, BigDecimal> pOutputs = this.getOutPut();

        return (String) this.bitcoinJSONRPCClient.query("createrawtransaction", pInputs, pOutputs);
    }


    /*
    交易离线签名
    * */
    public String offlineSign() throws BitcoinException {

        //获取签名
        String rawTransaction = this.create(false);

        //获取私钥集合
        Set<String> privateKeys = new HashSet<>();
        for (OfflineTxInput txInput : inputs) {
            privateKeys.add(txInput.getPrivateKey());

        }

        //进行签名
        List<Map> inputLists = this.getInPut(true);
        LinkedHashMap offlineObj = (LinkedHashMap) this.bitcoinJSONRPCClient.query("signrawtransaction", rawTransaction, inputLists, privateKeys);
//        if (offlineObj.get(""))
        if ((Boolean) offlineObj.get("complete")) {

            return (String) offlineObj.get("hex");

        } else {
            throw new BitcoinException("complete 为 false,签名失败");
        }

    }

    /*
    返回的是json字符串
    * */
    public void decodeRawTxToGetSizeAndTxid(String rawTx) throws BitcoinException {

        try {

            LinkedHashMap offlineObj = (LinkedHashMap) this.bitcoinJSONRPCClient.query("decoderawtransaction", rawTx);
            Long size = (Long) offlineObj.get("size");
            if (size > 0) {

                this.size = size;

            } else {
                throw new BitcoinException("");
            }

            this.txid = (String) offlineObj.get("txid");


        } catch (Exception e) {

            throw new BitcoinException("获取");

        }

    }

    public Long getSize() {
        return size;
    }

    public String getTxid() {
        return txid;
    }

    //    public Long decodeOfflineRawTxThenGetSize(String rawTx) throws BitcoinException {
//
//        try {
//
//            LinkedHashMap offlineObj = (LinkedHashMap) this.bitcoinJSONRPCClient.query("decoderawtransaction", rawTx);
//            Long size = (Long) offlineObj.get("size");
//            if (size > 0) {
//
//                return size;
//
//            } else {
//                throw new BitcoinException("");
//            }
//
//        } catch (Exception e) {
//
//            throw new BitcoinException("获取");
//
//        }
//
//
//    }

    public LinkedHashSet<OfflineTxInput> getInputs() {
        return inputs;
    }

    public List<OfflineTxOutput> getOutputs() {
        return outputs;
    }

    /*
         预估交易的大小，单位为bytes。用于交易手续费调整
       * */
    public static int calculateSize(int inCount, int outCount) {
//        if (this.inputs == null ||
//                this.outputs == null ||
//                this.inputs.size() <=0 ||
//                this.outputs.size() <= 0
//                ) {
//            return 0;
//        }

//      148 * number_of_inputs + 34 * number_of_outputs + 10
        return inCount * 148 + outCount * 34 + 10;
    }

    private List<Map> getInPut(boolean isIncludeScriptPubKey) {

        List<Map> pInputs = new ArrayList<Map>();
        for (final OfflineTxInput txInput : inputs) {
            pInputs.add(new LinkedHashMap() {
                {
                    put("txid", txInput.getTxid());
                    put("vout", txInput.getVout());
                    if (isIncludeScriptPubKey) {
                        put("scriptPubKey", txInput.getScriptPubKey());
                    }

                }
            });
        }
        return pInputs;
    }

    private Map<String, BigDecimal> getOutPut() {

        Map<String, BigDecimal> pOutputs = new LinkedHashMap();

        Double oldValue;
        for (OfflineTxOutput txOutput : outputs) {

            BigDecimal amount = txOutput.getAmount();

            //全部保留8位小数其余舍去
            amount = amount.setScale(8, BigDecimal.ROUND_DOWN);
            pOutputs.put(txOutput.getAddress(), amount);

        }
        return pOutputs;
    }


    public static void main(String[] args) {

        testnet();
        //以下进行测试
        // 代码运行机器要有，一个测试测试网路在跑 regTest
        // in
        OfflineTxInput offlineTxInput = new OfflineTxInput("1c1cab1f6348243e888c82db10946126adede2b5cb4652b5d615929108bc8816",
                1,
                "76a9144903332b1ee2b8d4055af4522bb87bb03f095d7988ac",
                "cUt9mB4HGXmE69qo5DmiP3CzuGezu3NsLfzMRQFfWXcdogaV62MK"
        );

        //out
        OfflineTxOutput offlineTxOutput = new OfflineTxOutput("myg4sxjQDQ7qG6a9E2h2aviwm59tiBLa72", BigDecimal.valueOf(9.9999));

        //
        BitcoinOfflineRawTxBuilder bitcoinOfflineRawTxBuilder = new BitcoinOfflineRawTxBuilder();
        bitcoinOfflineRawTxBuilder.in(offlineTxInput);
        bitcoinOfflineRawTxBuilder.out(offlineTxOutput);

        try {

            String offlineSignResult = bitcoinOfflineRawTxBuilder.offlineSign();

            if (offlineSignResult.equals("02000000011688bc08919215d6b55246cbb5e2edad26619410db828c883e2448631fab1c1c010000006b483045022100f92d882c99fcc52ff877bbe8b3478b0ce9db25611d5e619c74281369058c0e0d02206dca090baf17dc42ba4ecb4e61e99da7afcd389ee1cec19ab1cf811fc880c6770121032da7ff2c0845416d5930224cb98566e0a99666ec9d3dd446052ce19d1b92da8fffffffff01f0a29a3b000000001976a914c72b811b6052088497363810a700a2f257d03f0188ac00000000")) {
                System.out.print("离线签名成功");

            }
        } catch (Exception e) {

            e.printStackTrace();
            System.out.print("离线签名失败");

        }

    }

    private static void testnet() {

        //以下进行测试
        // 代码运行机器要有，一个测试测试网路在跑 regTest
        // in
        OfflineTxInput offlineTxInput = new OfflineTxInput("27e01a5e61e52e27c3481563d04c6c980c72559f02a0b887f5b176229bc39b13",
                0,
                "76a9144903332b1ee2b8d4055af4522bb87bb03f095d7988ac",
                "cUt9mB4HGXmE69qo5DmiP3CzuGezu3NsLfzMRQFfWXcdogaV62MK"
        );


        //out
        OfflineTxOutput offlineTxOutput1 = new OfflineTxOutput("n22qbkmhfip9Ks5ehxZqCT8CHR23FDw4ka", BigDecimal.valueOf(0.15));

        OfflineTxOutput offlineTxOutput2 = new OfflineTxOutput("myc1x6qKivfuxqcGovfMXhbmZWbBrx5TKz", BigDecimal.valueOf(0.25));

        //找零
        OfflineTxOutput offlineTxOutputling = new OfflineTxOutput("mnB1TPWm7vtaqycZGXBkjwtkVq6Vzb6Nci", BigDecimal.valueOf(0.39));

        //
        BitcoinOfflineRawTxBuilder bitcoinOfflineRawTxBuilder = new BitcoinOfflineRawTxBuilder();
        bitcoinOfflineRawTxBuilder.in(offlineTxInput);

        bitcoinOfflineRawTxBuilder.out(offlineTxOutput1);
        bitcoinOfflineRawTxBuilder.out(offlineTxOutput2);
        bitcoinOfflineRawTxBuilder.out(offlineTxOutputling);


        try {

            String offlineSignResult = bitcoinOfflineRawTxBuilder.offlineSign();

            System.out.print("离线签名成功");

//            Long size = bitcoinOfflineRawTxBuilder.decodeOfflineRawTxThenGetSize(offlineSignResult);


        } catch (Exception e) {

            e.printStackTrace();
            System.out.print("离线签名失败");


        }


    }


}

