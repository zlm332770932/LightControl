package com.e3.light.control.ble;

import android.bluetooth.le.ScanResult;

import java.util.List;

/**
 * Create by limin on 2020/8/4.
 **/
public interface LeScanCallback {

    int SCAN_FAILED_LOCATION_NOT_PERMISSION = 101;
    int SCAN_FAILED_LOCATION_NOT_OPEN = 102;
    int SCAN_FAILED_BLE_NOT_ENABLE = 103;
    int SCAN_FAILED_BLE_NOT_SUPPORT = 104;

    void onScanResult(ScanResult result);

    void onBatchScanResult(List<ScanResult> results);

    void onScanStart();

    void onScanStop();

    void onScanFailed(int code);

}
