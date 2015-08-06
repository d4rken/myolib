/*
 * Android Myo library by darken
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
public class BaseDataPacket extends DataPacket {
    private final UUID mServiceUUID;
    private final UUID mCharacteristicUUID;
    private final byte[] mData;

    public BaseDataPacket(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        this(gatt.getDevice().getAddress(), characteristic.getService().getUuid(), characteristic.getUuid(), characteristic.getValue());
    }

    public BaseDataPacket(String deviceAddress, UUID serviceUUID, UUID characteristicUUID, byte[] data) {
        super(deviceAddress, System.currentTimeMillis());
        mServiceUUID = serviceUUID;
        mCharacteristicUUID = characteristicUUID;
        mData = data;
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

}
