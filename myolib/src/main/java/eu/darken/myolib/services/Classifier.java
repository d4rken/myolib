/*
 * Android Myo library by darken
 * Matthias Urhahn (matthias.urhahn@rwth-aachen.de)
 * mHealth - Uniklinik RWTH-Aachen.
 */

package eu.darken.myolib.services;

import java.util.UUID;

/**
 * Describes accessible Classifier services/characteristics/descriptors
 */
public class Classifier {
    protected static final UUID SERVICE_ID = UUID.fromString(String.format(MyoService.MYO_SERVICE_BASE_UUID, 0x0003));

    protected static final UUID CLASSIFIEREVENT_CHARACTERISTIC = UUID.fromString(String.format(MyoService.MYO_SERVICE_BASE_UUID, 0x0103));

    public static final MyoService SERVICE = new MyoService(SERVICE_ID);

    public static UUID getServiceUUID() {
        return SERVICE.getServiceUUID();
    }

    /**
     * Indicate-only
     */
    public static final MyoCharacteristic CLASSIFIEREVENT = new MyoCharacteristic(SERVICE, CLASSIFIEREVENT_CHARACTERISTIC, "Classifier Event");

    public static final MyoDescriptor CLASSIFIEREVENT_DESCRIPTOR = new MyoDescriptor(CLASSIFIEREVENT, MyoDescriptor.CLIENT_CHARACTERISTIC_CONFIG);
}
