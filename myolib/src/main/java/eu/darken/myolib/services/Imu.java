/*
 * Android Myo library by darken
 * Matthias Urhahn (matthias.urhahn@rwth-aachen.de)
 * mHealth - Uniklinik RWTH-Aachen.
 */
package eu.darken.myolib.services;

import java.util.UUID;

/**
 * Describes accessible IMU services/characteristics/descriptors
 */
public class Imu {
    protected static final UUID SERVICE_ID = UUID.fromString(String.format(MyoService.MYO_SERVICE_BASE_UUID, 0x0002));

    protected static final UUID IMUDATA_CHARACTERISTIC = UUID.fromString(String.format(MyoService.MYO_SERVICE_BASE_UUID, 0x0402));
    protected static final UUID MOTIONEVENT_CHARACTERISTIC = UUID.fromString(String.format(MyoService.MYO_SERVICE_BASE_UUID, 0x0502));

    public static final MyoService SERVICE = new MyoService(SERVICE_ID);

    public static UUID getServiceUUID() {
        return SERVICE.getServiceUUID();
    }

    /**
     * Notify-only
     */
    public static final MyoCharacteristic IMUDATA = new MyoCharacteristic(SERVICE, IMUDATA_CHARACTERISTIC, "Imu Data");
    public static final MyoDescriptor IMUDATA_DESCRIPTOR = new MyoDescriptor(IMUDATA, MyoCharacteristic.CLIENT_CHARACTERISTIC_CONFIG);
    /**
     * Indicate-only
     */
    public static final MyoCharacteristic MOTIONEVENT = new MyoCharacteristic(SERVICE, MOTIONEVENT_CHARACTERISTIC, "Motion Event");
    public static final MyoDescriptor MOTIONEVENT_DESCRIPTOR = new MyoDescriptor(MOTIONEVENT, MyoCharacteristic.CLIENT_CHARACTERISTIC_CONFIG);
}
