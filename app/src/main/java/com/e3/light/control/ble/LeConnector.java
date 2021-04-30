package com.e3.light.control.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Create by limin on 2020/8/4.
 **/
public class LeConnector extends BluetoothGattCallback {
    private static final String TAG = "LeConnector";

    private static final int CONN_STATE_IDLE = 1;
    private static final int CONN_STATE_CONNECTING = 2;
    private static final int CONN_STATE_CONNECTED = 4;
    private static final int CONN_STATE_DISCONNECTING = 8;
    private static final int CONN_STATE_CLOSED = 16;

    private static final int CONN_TIMEOUT = 10*1000;

    private static final int MTU_SIZE_MIN = 23;
    private static final int MTU_SIZE_MAX = 517;

    protected Context mContext;
    protected BluetoothGatt gatt;
    protected BluetoothDevice device;
    protected int rssi;
    protected byte[] scanRecord;

    protected int meshAddress;
    protected List<BluetoothGattService> mServices;

    private AtomicInteger mConnState = new AtomicInteger(CONN_STATE_IDLE);
    protected LeConnectCallback connectCallback;
    protected LeNotifyCallback notifyCallback;

    protected final Handler mDelayHandler = new Handler(Looper.getMainLooper());

    private Runnable connectTimeout = () -> disconnect();

    public LeConnector(Context context){
        this.mContext = context;
    }

    public void connect(BluetoothDevice device, byte[] scanRecord, int rssi){
        if (this.mConnState.get() == CONN_STATE_IDLE) {
            this.device = device;
            this.scanRecord = scanRecord;
            this.rssi = rssi;
            this.mDelayHandler.removeCallbacks(connectTimeout);
            this.connect(mContext);
            this.mDelayHandler.postDelayed(connectTimeout, CONN_TIMEOUT);
        }else{
            Log.d(TAG, "connecting -> " + this.getDeviceAddress());
        }
    }

    private void connect(Context context){
        Log.d(TAG, "connect -> " + this.getDeviceName() + " -- " + this.getDeviceAddress());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.gatt = this.device.connectGatt(context, false, this, BluetoothDevice.TRANSPORT_LE);
        } else {
            this.gatt = this.device.connectGatt(context, false, this);
        }
        if (this.gatt == null) {
            this.disconnect();
            if (connectCallback != null)
                connectCallback.onDisconnected(this.device);
        }
    }

    public boolean disconnect(){
        Log.w(TAG, "disconnect -> " + this.getDeviceName() + " -- " + this.getDeviceAddress() + " -- " + mConnState.get());
        this.clear();
        this.mDelayHandler.removeCallbacksAndMessages(null);
        int connState = this.mConnState.get();
        //已经断开连接，不需要再发送断开命令
        if (connState != CONN_STATE_CONNECTING && connState != CONN_STATE_CONNECTED && connState != CONN_STATE_DISCONNECTING)
            return false;
        if (this.gatt != null) {
            if (connState == CONN_STATE_CONNECTED) {
                this.mConnState.set(CONN_STATE_DISCONNECTING);
                this.gatt.disconnect();
                return true;
            } else if (connState == CONN_STATE_CONNECTING) {
                this.gatt.disconnect();
                this.gatt.close();
                this.mConnState.set(CONN_STATE_IDLE);
                return false;
            } else {
                return true;
            }
        } else {
            this.mConnState.set(CONN_STATE_IDLE);
            return false;
        }
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        Log.w(TAG,"onConnectionStateChange -> " + this.getDeviceAddress() + " status :" + status + " state : " + newState);

        if (newState == BluetoothGatt.STATE_CONNECTED && status == BluetoothGatt.GATT_SUCCESS) {
            this.mConnState.set(CONN_STATE_CONNECTED);
            if (this.gatt == null || !this.gatt.discoverServices()) {
                Log.w(TAG, "onConnectionStateChange ->  remote service discovery has been stopped status = " + newState);
                this.disconnect();
            } else {
                mDelayHandler.removeCallbacks(connectTimeout);
                if (connectCallback != null)
                    connectCallback.onConnected(getDevice());
            }
        }else{
            Log.w(TAG, "onConnectionStateChange ->  Close");
            if (this.gatt != null) {
                this.gatt.close();
            }
            this.clear();
            this.mConnState.set(CONN_STATE_IDLE);
            this.mDelayHandler.removeCallbacksAndMessages(null);
            if (connectCallback != null)
                connectCallback.onDisconnected(getDevice());
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            List<BluetoothGattService> services = gatt.getServices();
            this.mServices = services;
            if (connectCallback != null)
                connectCallback.onServicesDiscovered(services);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                this.gatt.requestMtu(MTU_SIZE_MAX);
            }
        } else {
            Log.w(TAG, "onServicesDiscovered -> Service discovery failed");
            this.disconnect();
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        if (notifyCallback != null)
            notifyCallback.onNotify(characteristic.getUuid(), characteristic.getValue());
    }

    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
        super.onMtuChanged(gatt, mtu, status);
    }



    // ----------------------------------------------------------------------------------
    public void clear(){
        refreshCache();
        this.mDelayHandler.removeCallbacksAndMessages(null);
    }

    public boolean refreshCache() {
        if (Build.VERSION.SDK_INT >= 27) return false;
        if (gatt == null) {
            Log.d(TAG, "refresh error: gatt null");
            return false;
        } else {
            Log.d(TAG, "Device#refreshCache#prepare");
        }
        try {
            BluetoothGatt localBluetoothGatt = gatt;
            Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
            if (localMethod != null) {
                boolean bool = ((Boolean) localMethod.invoke(localBluetoothGatt, new Object[0])).booleanValue();
                /*if (bool) {
                    mDelayHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            gatt.discoverServices();
                        }
                    }, 0);
                }*/
                return bool;
            }
        } catch (Exception localException) {
            Log.e(TAG, "An exception occurs while refreshing device");
        }
        return false;
    }


    // ----------------------------------------------------------------------------------

    public BluetoothDevice getDevice(){
        return this.device;
    }

    public String getDeviceName(){
        return this.getDevice() == null ? "null" : this.getDevice().getName();
    }

    public String getDeviceAddress(){
        return this.getDevice() == null ? "null" : this.getDevice().getAddress();
    }

    public LeConnectCallback getConnectCallback() {
        return connectCallback;
    }

    public void setConnectCallback(LeConnectCallback connectCallback) {
        this.connectCallback = connectCallback;
    }

    public LeNotifyCallback getNotifyCallback() {
        return notifyCallback;
    }

    public void setNotifyCallback(LeNotifyCallback notifyCallback) {
        this.notifyCallback = notifyCallback;
    }
}
