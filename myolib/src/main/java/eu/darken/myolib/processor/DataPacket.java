package eu.darken.myolib.processor;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by darken on 31.07.2015.
 */
public abstract class DataPacket {

    private final long mTimeStamp;
    private final String mDeviceAddress;

    protected DataPacket(DataPacket packet) {
        mDeviceAddress = packet.getDeviceAddress();
        mTimeStamp = packet.getTimeStamp();
    }

    protected DataPacket(String deviceAddress, long timeStamp) {
        mDeviceAddress = deviceAddress;
        mTimeStamp = timeStamp;
    }

    /**
     * The address of the Myo device the data came from.
     *
     * @return The hex bluetooth address of a Myo.
     */
    public String getDeviceAddress() {
        return mDeviceAddress;
    }

    /**
     * A timestamp set when this class was created, which is shortly after {@link android.bluetooth.BluetoothGattCallback#onCharacteristicChanged(BluetoothGatt, BluetoothGattCharacteristic)} is triggered.
     * {@link System#currentTimeMillis()} is used.
     *
     * @return timestamp in milliseconds
     */
    public long getTimeStamp() {
        return mTimeStamp;
    }
}
