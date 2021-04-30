package com.e3.light.control.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;

import java.util.List;

/**
 * Create by limin on 2020/8/5.
 **/
public interface LeConnectCallback {

    void onConnected(BluetoothDevice device);

    void onDisconnected(BluetoothDevice device);

    void onServicesDiscovered(List<BluetoothGattService> services);

}
