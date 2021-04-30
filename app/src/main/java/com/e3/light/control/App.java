package com.e3.light.control;

import android.app.Application;
import android.util.Log;

import com.e3.light.control.ble.BleManager;
import com.e3.light.control.model.Mesh;
import com.e3.light.control.util.MeshUtils;

/**
 * Create by limin on 2020/7/24.
 **/
public class App extends Application {
    private static final String TAG = "App";
    private static App mApp;
    private Mesh mMesh;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        Log.d(TAG," onCreate");
        
        initMesh();


        BleManager.getInstance().init(this);
    }

    private void initMesh() {
        Object object = FileSystem.readAsObject(this, Mesh.STORAGE_NAME);
        if (object == null) {
            mMesh = new Mesh();
            mMesh.psd = MeshUtils.generatePsd();
            mMesh.mesh = MeshUtils.generateMesh(mMesh.psd);
            Log.e(TAG, "Mesh : " + mMesh.mesh + "  Psd : " + mMesh.psd);
            mMesh.presetId = 0x0001;
            mMesh.lightId = 0x0001;
            mMesh.groupId = 0x8001;

            mMesh.saveOrUpdate(this);
        }else{
            mMesh = (Mesh) object;
            Log.e(TAG, "MeshInfo read Mesh : " + mMesh.mesh + "  Psd : " + mMesh.psd);
        }
    }
}
