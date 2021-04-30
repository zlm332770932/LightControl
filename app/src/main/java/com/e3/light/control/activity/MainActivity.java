package com.e3.light.control.activity;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.ScanResult;
import android.os.Bundle;

import com.e3.light.control.BaseActivity;
import com.e3.light.control.R;
import com.e3.light.control.ble.BleManager;
import com.e3.light.control.ble.LeBluetoothUtils;
import com.e3.light.control.ble.LeConnectCallback;
import com.e3.light.control.ble.LeConnector;
import com.e3.light.control.ble.LeNotifyCallback;
import com.e3.light.control.ble.LeScanCallback;
import com.e3.light.control.ble.LeScanSettings;
import com.e3.light.control.util.ArrayUtils;
import com.e3.light.control.util.LocationUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private List<ScanResult> results = new ArrayList<>();

    private String meshname1 = "E3S33Aaiwv50";
    private String meshname2 = "E3Control Mesh";
    private String password1 = "mnbv1";
    private String password2 = "123";

    private LeConnector leConnector;

    private LeConnectCallback leConnectCallback = new LeConnectCallback() {
        @Override
        public void onConnected(BluetoothDevice device) {

        }

        @Override
        public void onDisconnected(BluetoothDevice device) {

        }

        @Override
        public void onServicesDiscovered(List<BluetoothGattService> services) {

        }
    };

    private LeNotifyCallback leNotifyCallback = new LeNotifyCallback() {
        @Override
        public void onNotify(UUID charUUID, byte[] data) {

        }

        @Override
        public void onNotifyEnable(UUID charUUID) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.start_scan).setOnClickListener(v -> startScan());
        findViewById(R.id.stop_scan).setOnClickListener(v -> stopScan());
        findViewById(R.id.connect).setOnClickListener(v -> connect());
    }

    private void stopScan(){
        BleManager.getInstance().stopScan();
    }

    private void startScan(){
        LeScanSettings leScanSettings = new LeScanSettings.Builder().setDeviceName(meshname1).setDeviceName(meshname2).build();
        BleManager.getInstance().startScan(this, leScanSettings, new LeScanCallback() {
            @Override
            public void onScanResult(ScanResult result) {
                Log.d(TAG, "onScanResult -> " + result.toString());
                Log.d(TAG, "onScanResult -> " + ArrayUtils.bytesToHexString(result.getScanRecord().getBytes(), ":"));
                results.add(result);
            }

            @Override
            public void onBatchScanResult(List<ScanResult> results) {
                Log.d(TAG, "onBatchScanResult -> ");
            }

            @Override
            public void onScanStart() {
                Log.d(TAG, "onScanStart -> ");
            }

            @Override
            public void onScanStop() {
                Log.d(TAG, "onScanStop -> ");
            }

            @Override
            public void onScanFailed(int code) {
                Log.d(TAG, "onScanFailed -> " + code);
                if (code == LeScanCallback.SCAN_FAILED_LOCATION_NOT_PERMISSION){
                    requestLocationPermission();
                }else if (code == LeScanCallback.SCAN_FAILED_BLE_NOT_ENABLE){
                    LeBluetoothUtils.enableBluetooth(MainActivity.this);
                }else if (code == LeScanCallback.SCAN_FAILED_LOCATION_NOT_OPEN){
                    LocationUtils.openLocation(MainActivity.this);
                }else if (code == LeScanCallback.SCAN_FAILED_BLE_NOT_SUPPORT){
                    Toast.makeText(MainActivity.this,"不支持蓝牙",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void connect(){
        if (results.size() <= 0){
            Toast.makeText(this, "没有扫描到可连接的设备", Toast.LENGTH_SHORT).show();
            return;
        }

        if (leConnector == null) {
            leConnector = new LeConnector(this);
            leConnector.setConnectCallback(leConnectCallback);
            leConnector.setNotifyCallback(leNotifyCallback);
        }

        ScanResult result = results.get(0);
        leConnector.connect(result.getDevice(), result.getScanRecord().getBytes(), result.getRssi());
    }

    private void requestLocationPermission(){
        EasyPermissions.requestPermissions(this, getString(R.string.rationale_location),RC_LOCATION_PERM, Manifest.permission.ACCESS_FINE_LOCATION);
    }
}
