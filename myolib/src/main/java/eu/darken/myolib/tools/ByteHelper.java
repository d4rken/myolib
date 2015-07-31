/*
 * darken's Myo lib
 * Matthias Urhahn (matthias.urhahn@rwth-aachen.de)
 * mHealth - Uniklinik RWTH-Aachen.
 */
package eu.darken.myolib.tools;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

/**
 * Helper class to convert byte[] data received via bluetooth.
 */
public class ByteHelper {
    private final ByteBuffer mByteBuffer;

    public ByteHelper(byte[] data) {
        mByteBuffer = ByteBuffer.wrap(data);
        mByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    public short getUInt16() {
        return mByteBuffer.getShort();
    }

    public short[] getUInt16Array(int length) {
        short[] result = new short[length];
        for (int i = 0; i < length; i++)
            result[i] = mByteBuffer.getShort();
        return result;
    }

    public UUID getUUID() {
        long low = mByteBuffer.getLong();
        long high = mByteBuffer.getLong();
        return new UUID(high, low);
    }
}
