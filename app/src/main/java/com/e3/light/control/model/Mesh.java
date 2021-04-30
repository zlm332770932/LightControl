package com.e3.light.control.model;

import android.content.Context;
import android.util.Log;

import com.e3.light.control.FileSystem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Create by limin on 2020/7/24.
 **/
public class Mesh implements Serializable,Cloneable {
    private static final String TAG = "Mesh";
    private static final long serialVersionUID = 1L;
    public static final String STORAGE_NAME = "light_control_mesh";

    public String mesh;
    public String psd;
    public int lightId = 0x0001;
    public int groupId = 0x8001;
    public int presetId = 0x0001;


    public List<Group> groups = new ArrayList<>();
    public List<Light> lights = new ArrayList<>();
    public List<Preset> presets = new ArrayList<>();

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

    public boolean insertLight(Light light){
        Light local = getLightByMac(light.mac);
        if (local == null){
            lights.add(light);
            return true;
        }
        Log.w(TAG,"insertLight -- the device has exist >> " + light.mac);
        return false;
    }

    public List<Group> getGroups(){
        return groups;
    }

    public Group getGroupById(int id){
        for (Group group : groups){
            if (group.id == id)
                return group;
        }
        return null;
    }

    public boolean removeGroupById(int id){
        if (groups == null || groups.size() == 0) return false;
        Iterator<Group> iterator = groups.iterator();
        while (iterator.hasNext()) {
            Group group = iterator.next();
            if (group.id == id) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public boolean insertGroup(Group group){
        Group local = getGroupById(group.id);
        if (local == null){
            groups.add(group);
            return true;
        }
        return false;
    }

    public List<Preset> getPresets(){
        return presets;
    }

    public List<Preset> getPresetByType(int type){
        List<Preset> locals = new ArrayList<>();
        for (Preset preset : presets){
            if (preset.type == type)
                locals.add(preset);
        }
        return locals;
    }

    public Preset getPresetById(int id){
        for (Preset preset : presets){
            if (preset.id == id)
                return preset;
        }
        return null;
    }

    public boolean insertPreset(Preset preset){
        Preset local = getPresetById(preset.id);
        if (local == null){
            presets.add(preset);
            return true;
        }
        return false;
    }

    public boolean removePresetById(int id){
        if (presets == null || presets.size() == 0) return false;
        Iterator<Preset> iterator = presets.iterator();
        while (iterator.hasNext()) {
            Preset preset = iterator.next();
            if (preset.id == id) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public int getLightId(){
        return lightId++;
    }

    public int getGroupId(){
        return groupId++;
    }

    public int getPresetId(){
        return presetId++;
    }

    public void saveOrUpdate(Context context) {
        FileSystem.writeAsObject(context, STORAGE_NAME, this);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
