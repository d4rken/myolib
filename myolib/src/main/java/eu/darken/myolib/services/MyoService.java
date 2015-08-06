/*
 * Android Myo library by darken
 * Matthias Urhahn (matthias.urhahn@rwth-aachen.de)
 * mHealth - Uniklinik RWTH-Aachen.
 */
package eu.darken.myolib.services;

import java.util.UUID;

/**
 * Describes a certain Myo service via it's UUID
 */
public class MyoService {
    public static final String MYO_SERVICE_BASE_UUID = "d506%04X-a904-deb9-4748-2c7f4a124842";

    private final UUID mServiceId;

    public MyoService(UUID serviceId) {
        mServiceId = serviceId;
    }

    public UUID getServiceUUID() {
        return mServiceId;
    }
}
