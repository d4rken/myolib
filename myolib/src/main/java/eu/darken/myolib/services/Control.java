/*
 * Android Myo library by darken
 * Matthias Urhahn (matthias.urhahn@rwth-aachen.de)
 * mHealth - Uniklinik RWTH-Aachen.
 */

package eu.darken.myolib.services;

import java.util.UUID;

/**
 * Data necessary to control the Myo devices i.e., send commands
 */
public class Control {
    protected static final UUID SERVICE_ID = UUID.fromString(String.format(MyoService.MYO_SERVICE_BASE_UUID, 0x0001));

    protected static final UUID MYOINFO_CHARACTERISTIC = UUID.fromString(String.format(MyoService.MYO_SERVICE_BASE_UUID, 0x0101));
    protected static final UUID FIRMWARE_VERSION_CHARACTERISTIC = UUID.fromString(String.format(MyoService.MYO_SERVICE_BASE_UUID, 0x0201));
    protected static final UUID COMMAND_CHARACTERISTIC = UUID.fromString(String.format(MyoService.MYO_SERVICE_BASE_UUID, 0x0401));

    public static final MyoService SERVICE = new MyoService(SERVICE_ID);
    /**
     * Read-only
     */
    public static final MyoCharacteristic MYOINFO = new MyoCharacteristic(SERVICE, MYOINFO_CHARACTERISTIC, "Myo Info");
    /**
     * Read-only
     */
    public static final MyoCharacteristic FIRMWARE_VERSION = new MyoCharacteristic(SERVICE, FIRMWARE_VERSION_CHARACTERISTIC, "Myo Firmware Version");
    /**
     * Write-only
     */
    public static final MyoCharacteristic COMMAND = new MyoCharacteristic(SERVICE, COMMAND_CHARACTERISTIC, "Myo Command");

    public static UUID getServiceUUID() {
        return SERVICE.getServiceUUID();
    }
}
