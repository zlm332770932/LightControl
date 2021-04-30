package com.e3.light.control.model;

import java.io.Serializable;

/**
 * Create by limin on 2020/7/24.
 **/
public class Light extends Target implements Serializable {
    public String name;
    public String mac;
    public int status;// on:1, off:0, offline:-1
    public int power;  //电量
    public int type;
}
