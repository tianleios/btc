package com.btc;

public class Test {

    public synchronized  void test(){

        synchronized (this.getClass()) {

        }
    }
}
