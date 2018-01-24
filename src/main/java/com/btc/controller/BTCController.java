package com.btc.controller;

//import com.tianlei.common.Response;

import com.btc.service.IBTCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
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

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ResponseBody
    public String test() {

        return "{\"name\" : \"tianlei\"}";
//        return testService.test();
    }

}
