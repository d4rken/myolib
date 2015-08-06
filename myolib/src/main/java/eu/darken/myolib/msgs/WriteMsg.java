/*
 * Android Myo library by darken
 * Matthias Urhahn (matthias.urhahn@rwth-aachen.de)
 * mHealth - Uniklinik RWTH-Aachen.
 */

package eu.darken.myolib.msgs;

import java.util.Arrays;
import java.util.UUID;

import eu.darken.myolib.services.MyoCharacteristic;
import eu.darken.myolib.services.MyoDescriptor;

/**
 * A subclass of {@link MyoMsg} for writing data.
 */
public class WriteMsg extends MyoMsg {
    private final byte[] mData;

    public WriteMsg(MyoCharacteristic characteristic, byte[] data, Callback callback) {
        this(characteristic.getServiceUUID(), characteristic.getCharacteristicUUID(), null, data, callback);
    }

    public WriteMsg(MyoDescriptor descriptor, byte[] data, Callback callback) {
        this(descriptor.getServiceUUID(), descriptor.getCharacteristicUUID(), descriptor.getDescriptorUUID(), data, callback);
    }

    public WriteMsg(UUID serviceUUID, UUID characteristicUUID, UUID descriptorUUID, byte[] data, Callback callback) {
        super(serviceUUID, characteristicUUID, descriptorUUID, callback);
        mData = data;
    }

    public byte[] getData() {
        return mData;
    }

    @Override
    public String toString() {
        return "WriteMsg\n" + "Data: " + Arrays.toString(mData) + "\n" + super.toString();
    }
}
