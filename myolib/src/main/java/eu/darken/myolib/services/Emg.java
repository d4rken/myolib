/*
 * Android Myo library by darken
 * Matthias Urhahn (matthias.urhahn@rwth-aachen.de)
 * mHealth - Uniklinik RWTH-Aachen.
 */
package eu.darken.myolib.services;

import java.util.UUID;

/**
 * Describes accessible EMG services/characteristics/descriptors
 */
public class Emg {
    protected static final UUID SERVICE_UUID = UUID.fromString(String.format(MyoService.MYO_SERVICE_BASE_UUID, 0x0005));

    protected static final UUID EMGDATA0_CHARACTERISTIC_UUID = UUID.fromString(String.format(MyoService.MYO_SERVICE_BASE_UUID, 0x0105));
    protected static final UUID EMGDATA1_CHARACTERISTIC_UUID = UUID.fromString(String.format(MyoService.MYO_SERVICE_BASE_UUID, 0x0205));
    protected static final UUID EMGDATA2_CHARACTERISTIC_UUID = UUID.fromString(String.format(MyoService.MYO_SERVICE_BASE_UUID, 0x0305));
    protected static final UUID EMGDATA3_CHARACTERISTIC_UUID = UUID.fromString(String.format(MyoService.MYO_SERVICE_BASE_UUID, 0x0405));

    public static final MyoService SERVICE = new MyoService(SERVICE_UUID);

    /**
     * Notify-only
     */
    public static final MyoCharacteristic EMGDATA0 = new MyoCharacteristic(SERVICE, EMGDATA0_CHARACTERISTIC_UUID, "Emg0 Data");
    public static final MyoDescriptor EMGDATA0_DESCRIPTOR = new MyoDescriptor(EMGDATA0, MyoCharacteristic.CLIENT_CHARACTERISTIC_CONFIG);
    /**
     * Notify-only
     */
    public static final MyoCharacteristic EMGDATA1 = new MyoCharacteristic(SERVICE, EMGDATA1_CHARACTERISTIC_UUID, "Emg1 Data");
    public static final MyoDescriptor EMGDATA1_DESCRIPTOR = new MyoDescriptor(EMGDATA1, MyoCharacteristic.CLIENT_CHARACTERISTIC_CONFIG);
    /**
     * Notify-only
     */
    public static final MyoCharacteristic EMGDATA2 = new MyoCharacteristic(SERVICE, EMGDATA2_CHARACTERISTIC_UUID, "Emg2 Data");
    public static final MyoDescriptor EMGDATA2_DESCRIPTOR = new MyoDescriptor(EMGDATA2, MyoCharacteristic.CLIENT_CHARACTERISTIC_CONFIG);
    /**
     * Notify-only
     */
    public static final MyoCharacteristic EMGDATA3 = new MyoCharacteristic(SERVICE, EMGDATA3_CHARACTERISTIC_UUID, "Emg3 Data");
    public static final MyoDescriptor EMGDATA3_DESCRIPTOR = new MyoDescriptor(EMGDATA3, MyoCharacteristic.CLIENT_CHARACTERISTIC_CONFIG);

    public static UUID getServiceUUID() {
        return SERVICE.getServiceUUID();
    }

}
