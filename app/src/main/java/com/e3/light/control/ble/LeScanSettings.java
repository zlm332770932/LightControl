package com.e3.light.control.ble;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Create by limin on 2020/8/4.
 **/
public class LeScanSettings {
    private List<String> filterDeviceNames = new ArrayList<>();
    private List<String> filterDeviceAddresses = new ArrayList<>();
    private List<UUID> filerServiceUUIDs = new ArrayList<>();
    private int scanPeriod = LeScanner.SCAN_TIMEOUT;
    private int reportDelayMillis = LeScanner.SCAN_REPORT_DELAY;

    public List<String> getFilterDeviceNames() {
        return filterDeviceNames;
    }

    public List<String> getFilterDeviceAddresses() {
        return filterDeviceAddresses;
    }

    public List<UUID> getuFilerServiceUUIDs() {
        return filerServiceUUIDs;
    }

    public int getScanPeriod() {
        return scanPeriod;
    }

    public int getReportDelayMillis() {
        return reportDelayMillis;
    }

    public static class Builder{
        private List<String> filterDeviceNames = new ArrayList<>();
        private List<String> filterDeviceAddresses = new ArrayList<>();
        private List<UUID> filerServiceUUIDs = new ArrayList<>();
        private int scanPeriod = LeScanner.SCAN_TIMEOUT;
        private int reportDelayMillis = LeScanner.SCAN_REPORT_DELAY;

        public Builder setDeviceName(List<String> names){
            this.filterDeviceNames.addAll(names);
            return this;
        }

        public Builder setDeviceName(String name){
            this.filterDeviceNames.add(name);
            return this;
        }

        public Builder setDeviceAddresses(List<String> macs){
            this.filterDeviceAddresses.addAll(macs);
            return this;
        }

        public Builder setDeviceAddress(String mac){
            this.filterDeviceAddresses.add(mac);
            return this;
        }

        public Builder setUUIDs(List<UUID> uuids){
            this.filerServiceUUIDs.addAll(uuids);
            return this;
        }

        public Builder setUUIDs(UUID uuid){
            this.filerServiceUUIDs.add(uuid);
            return this;
        }

        public Builder setScanPeriod(int period){
            this.scanPeriod = period;
            return this;
        }

        public Builder setReportDelayMillis(int delayMillis){
            this.reportDelayMillis = delayMillis;
            return this;
        }

        public LeScanSettings build(){
            LeScanSettings settings = new LeScanSettings();
            settings.filerServiceUUIDs = this.filerServiceUUIDs;
            settings.filterDeviceNames = this.filterDeviceNames;
            settings.filterDeviceAddresses = this.filterDeviceAddresses;
            settings.scanPeriod = this.scanPeriod;
            settings.reportDelayMillis = this.reportDelayMillis;

            return settings;
        }
    }

}
