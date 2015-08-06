/*
 * Android Myo library by darken
 * Matthias Urhahn (matthias.urhahn@rwth-aachen.de)
 * mHealth - Uniklinik RWTH-Aachen.
 */

package eu.darken.myolib.services;

import java.util.UUID;

/**
 * https://developer.bluetooth.org/gatt/services/Pages/ServiceViewer.aspx?u=org.bluetooth.service.device_information.xml
 */
public class Device {
    private static final UUID SERVICE_ID = UUID.fromString("0000180A-0000-1000-8000-00805f9b34fb");

    public static UUID getServiceUUID() {
        return SERVICE.getServiceUUID();
    }

    /**
     * https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.manufacturer_name_string.xml
     */
    private static final UUID MANUFACTURER_NAME_CHARACTERISTIC = UUID.fromString("00002A29-0000-1000-8000-00805f9b34fb");

    public static final MyoService SERVICE = new MyoService(SERVICE_ID);

    public static final MyoCharacteristic MANUFACTURER_NAME = new MyoCharacteristic(SERVICE, MANUFACTURER_NAME_CHARACTERISTIC, "Manufacturer Name");

}
