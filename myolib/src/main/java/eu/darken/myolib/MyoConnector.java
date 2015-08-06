/*
 * Android Myo library by darken
 * Matthias Urhahn (matthias.urhahn@rwth-aachen.de)
 * mHealth - Uniklinik RWTH-Aachen.
 */
package eu.darken.myolib;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import eu.darken.myolib.services.Control;
import eu.darken.myolib.tools.ByteHelper;
import eu.darken.myolib.tools.Logy;

/**
 * Wrapper for {@link BluetoothAdapter} that finds Myo devices
 */
public class MyoConnector implements BluetoothAdapter.LeScanCallback {
    private static final String TAG = "MyoLib:RawMyoConnector";
    private final Context mContext;
    private final BluetoothAdapter mBluetoothAdapter;
    private final Map<String, Myo> mDeviceMap = new HashMap<>();
    private final Map<String, Myo> mScanMap = new HashMap<>();
    private Runnable mScanRunnable;

    public MyoConnector(Context context) {
        mContext = context.getApplicationContext();
        BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * Scans for Myo devices
     * Results will only be returned after the scan finished.
     *
     * @param timeout  How long the scan lasts in milliseconds.
     * @param callback optional callback with results, also available via {@link #getMyos()}
     * @return true if a scan was started, false if a scan was already running.
     */
    public boolean scan(final long timeout, @Nullable final ScannerCallback callback) {
        if (mScanRunnable != null)
            return false;
        mScanMap.clear();
        mScanRunnable = new Runnable() {
            public void run() {
                mBluetoothAdapter.startLeScan(MyoConnector.this);
                try {
                    Thread.sleep(timeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Logy.d(TAG, "Scan stopped (timeout:" + timeout + ")");
                mBluetoothAdapter.stopLeScan(MyoConnector.this);
                mScanRunnable = null;
                if (callback != null)
                    callback.onScanFinished(new ArrayList<>(mDeviceMap.values()));
            }
        };
        new Thread(mScanRunnable).start();
        return true;
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        List<AdRecord> adRecords = AdRecord.parseScanRecord(scanRecord);
        UUID uuid = null;
        for (AdRecord adRecord : adRecords) {
            // TYPE_UUID128_INC
            if (adRecord.getType() == 0x6) {
                uuid = new ByteHelper(adRecord.getData()).getUUID();
                break;
            }
        }
        if (Control.getServiceUUID().equals(uuid)) {
            if (!mScanMap.containsKey(device.getAddress())) {
                Myo myo = mDeviceMap.get(device.getAddress());
                if (myo == null) {
                    myo = new Myo(getContext(), device);
                    mDeviceMap.put(device.getAddress(), myo);
                }
                mScanMap.put(device.getAddress(), myo);
            }
        }
    }

    public interface ScannerCallback {
        void onScanFinished(List<Myo> myos);
    }

    public ArrayList<Myo> getMyos() {
        return new ArrayList<>(mDeviceMap.values());
    }
}
