/*
 * Android Myo library by darken
 * Matthias Urhahn (matthias.urhahn@rwth-aachen.de)
 * mHealth - Uniklinik RWTH-Aachen.
 */
package eu.darken.myolib.services;

import java.util.UUID;

/**
 * Holds the UUID for a specific descriptor
 */
public class MyoDescriptor extends MyoCharacteristic {
    private final UUID mDescriptor;
    private final MyoCharacteristic mCharacteristic;

    public MyoDescriptor(MyoCharacteristic characteristic, UUID descriptor) {
        super(characteristic.getService(), characteristic.getCharacteristicUUID(), characteristic.getName());
        mCharacteristic = characteristic;
        mDescriptor = descriptor;
    }

    public MyoCharacteristic getCharacteristic() {
        return mCharacteristic;
    }

    public UUID getDescriptorUUID() {
        return mDescriptor;
    }
}
