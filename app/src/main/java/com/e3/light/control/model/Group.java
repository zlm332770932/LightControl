package com.e3.light.control.model;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Create by limin on 2020/7/24.
 **/
public class Group extends Target implements Serializable {
    private static final String TAG = "Group";
    public String name;
    public List<Light> lights = new ArrayList<>();

    public List<Light> getLights(){
        return lights;
    }

    public Light getLightByMac(String mac){
        for (Light light:lights){
            if (light.mac.equalsIgnoreCase(mac))
                return light;
        }
        return null;
    }

    public boolean removeLightByMac(String mac){
        if (lights == null || lights.size() == 0) return false;
        Iterator<Light> iterator = lights.iterator();
        while (iterator.hasNext()) {
            Light light = iterator.next();
            if (light.mac.equalsIgnoreCase(mac)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public boolean addLight(Light light){
        Light local = getLightByMac(light.mac);
        if (local == null){
            lights.add(light);
            return true;
        }
        Log.w(TAG,"addLight -- the device has exist >> " + light.mac);
        return false;
    }
}
