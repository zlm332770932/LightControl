package com.e3.light.control.model;

import java.io.Serializable;

/**
 * Create by limin on 2020/7/24.
 **/
public class Preset implements Serializable {
    public static final int TYPE_WHITE = 1; //白光预设
    public static final int TYPE_RGB = 2;   //彩光预设
    public static final int TYPE_PICK = 3;  //拾色器预设

    public int id;
    public String name;
    public int type;
    public int dim;
}
