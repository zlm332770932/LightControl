package com.e3.light.control.ble;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

import androidx.annotation.RequiresPermission;

/**
 * Create by limin on 2020/8/4.
 **/
public class LeBluetoothUtils {

    @RequiresPermission(Manifest.permission.BLUETOOTH)
    public static BluetoothAdapter getBluetoothAdapter(){
        return BluetoothAdapter.getDefaultAdapter();
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH)
    public static boolean isSupportBle(){
        return getBluetoothAdapter() != null;
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH)
    public static boolean isBleEnable(){
        return getBluetoothAdapter().isEnabled();
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH)
    public static void enableBluetooth(Activity activity) {
        if (!isSupportBle()) {
            //不支持Bluetooth
            return;
        }
        if (isBleEnable()) {
            // Bluetooth已经打开
            return;
        }
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent, BleConstant.BLE_REQ_ENABLE);
    }


}
