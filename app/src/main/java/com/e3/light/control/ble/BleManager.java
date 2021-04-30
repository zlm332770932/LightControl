package com.e3.light.control.ble;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.e3.light.control.util.LocationUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by limin on 2020/7/27.
 **/
public class BleManager {

    private Context mContext;
    private BluetoothAdapter mBluetoothAdapter;


    private static class SingletonHolder {
        private static final BleManager INSTANCE = new BleManager();
    }

    private BleManager(){}

    public static BleManager getInstance(){
        return SingletonHolder.INSTANCE;
    }

    public void init(Context context){
        this.mContext = context;
        this.mBluetoothAdapter = LeBluetoothUtils.getBluetoothAdapter();
    }

    public void startScan(Activity activity, LeScanSettings leScanSettings, LeScanCallback leScanCallback){
        // 是否支持蓝牙
        if (!LeBluetoothUtils.isSupportBle()){
            leScanCallback.onScanFailed(LeScanCallback.SCAN_FAILED_BLE_NOT_SUPPORT);
            return;
        }

        // 0. 判断location权限是否授权
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            leScanCallback.onScanFailed(LeScanCallback.SCAN_FAILED_LOCATION_NOT_PERMISSION);
            return;
        }

        // 1. 判断location权限是否打开，如果没有打开提示
        if (!LocationUtils.isOPen(activity)){
            leScanCallback.onScanFailed(LeScanCallback.SCAN_FAILED_LOCATION_NOT_OPEN);
            return;
        }

        // 2. 判断蓝牙是否打开，如果没有打开蓝牙，提示打开蓝牙
        if (!LeBluetoothUtils.isBleEnable()){
            leScanCallback.onScanFailed(LeScanCallback.SCAN_FAILED_BLE_NOT_ENABLE);
            return;
        }

        // 3. 关闭之前的扫描
        LeScanner.getInstance().stopScan();

        // 4. 开始扫描
        LeScanner.getInstance().startScan(leScanSettings,leScanCallback);
    }

    public void stopScan(){
        LeScanner.getInstance().stopScan();
    }

}
