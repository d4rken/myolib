/*
 * Android Myo library by darken
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

    public byte getByte() {
        return mByteBuffer.get();
    }

    public int getUInt8() {
        return mByteBuffer.get() & 0xFF;
    }

    public int getUInt16() {
        return mByteBuffer.getShort();
    }

    public int[] getUInt16Array(int length) {
        int[] result = new int[length];
        for (int i = 0; i < length; i++)
            result[i] = mByteBuffer.getShort();
        return result;
    }

    public UUID getUUID() {
        long low = mByteBuffer.getLong();
        long high = mByteBuffer.getLong();
        return new UUID(high, low);
    }

    public boolean hasRemaining() {
        return mByteBuffer.hasRemaining();
    }

    public int getRemaining() {
        return mByteBuffer.remaining();
    }
}
