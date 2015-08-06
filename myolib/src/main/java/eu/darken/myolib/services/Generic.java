/*
 * Android Myo library by darken
 * Matthias Urhahn (matthias.urhahn@rwth-aachen.de)
 * mHealth - Uniklinik RWTH-Aachen.
 */
package eu.darken.myolib.services;

import java.util.UUID;

/**
 * https://developer.bluetooth.org/gatt/services/Pages/ServiceViewer.aspx?u=org.bluetooth.service.generic_access.xml
 */
public class Generic {
    private static final UUID SERVICE_ID = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb");

    public static UUID getServiceUUID() {
        return SERVICE.getServiceUUID();
    }

    public static final MyoService SERVICE = new MyoService(SERVICE_ID);

    /**
     * https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.gap.device_name.xml
     */
    private static final UUID DEVICE_NAME_CHARACTERISTIC = UUID.fromString("00002A00-0000-1000-8000-00805f9b34fb");
    /**
     * https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.gap.appearance.xml
     */
    private static final UUID APPEARANCE_CHARACTERISTIC = UUID.fromString("00002A01-0000-1000-8000-00805f9b34fb");
    /**
     * https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.gap.peripheral_preferred_connection_parameters.xml
     */
    private static final UUID PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS_CHARACTERISTIC = UUID.fromString("00002A04-0000-1000-8000-00805f9b34fb");

    /**
     * Myo device name.
     * Supports read/write.
     */
    public static final MyoCharacteristic DEVICE_NAME = new MyoCharacteristic(SERVICE, DEVICE_NAME_CHARACTERISTIC, "Device Name");
    public static final MyoCharacteristic APPEARANCE = new MyoCharacteristic(SERVICE, APPEARANCE_CHARACTERISTIC, "Appearance");
    public static final MyoCharacteristic PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS = new MyoCharacteristic(SERVICE, PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS_CHARACTERISTIC, "Peripheral Preferred Connection Parameters");
}
