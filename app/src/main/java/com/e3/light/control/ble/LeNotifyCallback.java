package com.e3.light.control.ble;

import java.util.UUID;

/**
 * Create by limin on 2020/8/5.
 **/
public interface LeNotifyCallback {
    void onNotify(UUID charUUID, byte[] data);

    void onNotifyEnable(UUID charUUID);
}
