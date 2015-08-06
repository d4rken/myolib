/*
 * Android Myo library by darken
 * Matthias Urhahn (matthias.urhahn@rwth-aachen.de)
 * mHealth - Uniklinik RWTH-Aachen.
 */

package eu.darken.myolib.services;

import java.util.UUID;

/**
 * Holds the UUID for a specific characteristic
 */
public class MyoCharacteristic {
    public static UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private final MyoService mService;
    private final UUID mCharacteristicUUID;
    private final String mName;

    public MyoCharacteristic(MyoService service, UUID characteristicUUID, String name) {
        mService = service;
        mCharacteristicUUID = characteristicUUID;
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public MyoService getService() {
        return mService;
    }

    public UUID getServiceUUID() {
        return mService.getServiceUUID();
    }

    public UUID getCharacteristicUUID() {
        return mCharacteristicUUID;
    }
}
