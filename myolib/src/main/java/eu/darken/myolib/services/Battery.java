/*
 * Android Myo library by darken
 * Matthias Urhahn (matthias.urhahn@rwth-aachen.de)
 * mHealth - Uniklinik RWTH-Aachen.
 */
package eu.darken.myolib.services;

import java.util.UUID;

/**
 * Access to battery information
 */
public class Battery {
    private static final UUID SERVICE_ID = UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb");

    public static final MyoService SERVICE = new MyoService(SERVICE_ID);

    public static UUID getServiceUUID() {
        return SERVICE.getServiceUUID();
    }

    private static final UUID BATTERY_LEVEL_CHARACTERISTIC = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");
    /**
     * Current Myo battery level.
     * Supports read/notify
     */
    public static final MyoCharacteristic BATTERYLEVEL = new MyoCharacteristic(SERVICE, BATTERY_LEVEL_CHARACTERISTIC, "Battery Level");

}
