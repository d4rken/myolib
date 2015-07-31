/*
 * darken's Myo lib
 * Matthias Urhahn (matthias.urhahn@rwth-aachen.de)
 * mHealth - Uniklinik RWTH-Aachen.
 */

package eu.darken.myolib.processor;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import java.util.UUID;

import eu.darken.myolib.services.MyoCharacteristic;
import eu.darken.myolib.services.MyoService;

/**
 * The base class for sensor data received from a Myo.
 * If you create a custom processor by implementing {@link Processor},
 * this is what you will get as data.
 */
public class BaseDataPacket {
    private final String mDeviceAddress;
    private final UUID mServiceUUID;
    private final UUID mCharacteristicUUID;
    private final byte[] mData;

    private final long mTimeStamp;

    public BaseDataPacket(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        this(gatt.getDevice().getAddress(), characteristic.getService().getUuid(), characteristic.getUuid(), characteristic.getValue());
    }

    public BaseDataPacket(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, byte[] data) {
        mDeviceAddress = deviceAddress;
        mServiceUUID = serviceUUID;
        mCharacteristicUUID = characteristicUUID;
        mData = data;
        mTimeStamp = System.currentTimeMillis();
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
     * The service that this data originated from.
     *
     * @return A UUID corresponding to a {@link MyoService}
     */
    public UUID getServiceUUID() {
        return mServiceUUID;
    }

    /**
     * The characteristic that this data originated from.
     *
     * @return A UUID corresponding to a {@link MyoCharacteristic}
     */
    public UUID getCharacteristicUUID() {
        return mCharacteristicUUID;
    }

    /**
     * The raw data that was delivered.
     *
     * @return A byte array that can be NULL.
     */
    public byte[] getData() {
        return mData;
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
