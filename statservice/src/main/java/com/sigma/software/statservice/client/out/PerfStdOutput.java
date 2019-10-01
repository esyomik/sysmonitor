package com.sigma.software.statservice.client.out;

import java.util.Arrays;


class PerfStdOutput implements PerfOutput {

    @Override
    public void write(String key, byte[] data) {
        System.out.println(key);
        System.out.println(Arrays.toString(data));
    }

    @Override
    public void write(String key, String data) {
        System.out.println(key);
        System.out.println(data);
    }
}
