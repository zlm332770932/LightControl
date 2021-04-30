package com.e3.light.control.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Create by limin on 2020/8/4.
 **/
public class LeScanner {
    private static final String TAG = "LeScanner";
    public static final int SCAN_TIMEOUT = 10 * 1000;
    public static final int SCAN_REPORT_DELAY = 0;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mScanner;
    private LeScanCallback leScanCallback;
    private Handler mScanHandler = new Handler();
    private int mScanTimeout = SCAN_TIMEOUT;
    private int mScanReportDelay = SCAN_REPORT_DELAY;
    private boolean isScanning = false;


    private Runnable scanTimeout = () ->{
        stopScan();
    };


    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (leScanCallback != null)
                leScanCallback.onScanResult(result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            if (leScanCallback != null)
                leScanCallback.onBatchScanResult(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            if (leScanCallback != null)
                leScanCallback.onScanFailed(errorCode);
        }
    };


    private static class SingletonHolder {
        private static final LeScanner INSTANCE = new LeScanner();
    }

    private LeScanner(){
        mBluetoothAdapter = LeBluetoothUtils.getBluetoothAdapter();
        mScanner = mBluetoothAdapter.getBluetoothLeScanner();
    }

    public static LeScanner getInstance(){
        return SingletonHolder.INSTANCE;
    }

    public void startScan(LeScanSettings leScanSettings, LeScanCallback callback){
        leScanCallback = callback;
        mScanTimeout = leScanSettings.getScanPeriod();
        if (mScanTimeout == 0)
            mScanTimeout = SCAN_TIMEOUT;
        mScanReportDelay = leScanSettings.getReportDelayMillis();
        List<ScanFilter> list =new ArrayList<>();
        for (String name : leScanSettings.getFilterDeviceNames()){
            ScanFilter filter = new ScanFilter.Builder().setDeviceName(name).build();
            list.add(filter);
        }

        for (String mac : leScanSettings.getFilterDeviceAddresses()){
            ScanFilter filter = new ScanFilter.Builder().setDeviceAddress(mac).build();
            list.add(filter);
        }

        for (UUID uuid : leScanSettings.getuFilerServiceUUIDs()){
            ScanFilter filter = new ScanFilter.Builder().setServiceUuid(new ParcelUuid(uuid)).build();
            list.add(filter);
        }

        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(mScanReportDelay)
                .build();
        mScanner.startScan(list, scanSettings, mScanCallback);
        isScanning = true;
        mScanHandler.postDelayed(scanTimeout, mScanTimeout);
        if (leScanCallback != null)
            leScanCallback.onScanStart();

        Log.e(TAG," startScan ");
    }

    public void stopScan(){
        if (isScanning) {
            mScanHandler.removeCallbacks(scanTimeout);
            mScanner.stopScan(mScanCallback);
            isScanning = false;
            if (leScanCallback != null)
                leScanCallback.onScanStop();
            Log.e(TAG," stopScan ");
        }
    }
}
