package com.e3.light.control.model.preset;

import com.e3.light.control.model.Preset;

/**
 * Create by limin on 2020/7/24.
 *
 *  彩光  --  灯光预设
 *
 *  彩光和取色器都可以使用该类，赋值不同的type
 *
 **/
public class RGBPreset extends Preset {
    public int s;   //饱和度
    public int hue; //色相
}
