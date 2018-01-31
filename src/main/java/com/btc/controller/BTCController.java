package com.btc.controller;

//import com.tianlei.common.Response;

import com.btc.domain.BTCDefaultUTXO;
import com.btc.service.IBTCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tianlei on 2018/一月/22.
 */

@Controller
@RequestMapping("/btc")
public class BTCController {

    @Autowired
    private IBTCService btcService;

    /*
    1.地址生成
     */
    @GetMapping("/address")
    @ResponseBody
    public Map address() {

        String address = this.btcService.address(true);
        Map map = new HashMap(1);
        map.put("address", address);
        return map;

    }

    @GetMapping("/collection")
    @ResponseBody
    public Map collection() {

        String txid = this.btcService.collection();
        Map map = new HashMap(1);
        map.put("txid", txid);
        return map;

    }

    @GetMapping("utxos/{status}")
    @ResponseBody
    public List<BTCDefaultUTXO> utxoList(@PathVariable("status") String status) {

        return this.btcService.selectUTXOList(status);

    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ResponseBody
    public String test() {

        return "{\"name\" : \"tianlei\"}";
//        return testService.test();
    }

    @GetMapping(value = "withdraw")
    @ResponseBody
    public void withdraw() {

        this.btcService.handleWithdraw();

    }

}
