/*
 * Android Myo library by darken
 * Matthias Urhahn (matthias.urhahn@rwth-aachen.de)
 * mHealth - Uniklinik RWTH-Aachen.
 */

package eu.darken.myolib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Based on http://stackoverflow.com/a/24043510/1251958
 * Convenience wrapper for one data entry of an BLE advertising message
 */
public class AdRecord {
    private final int mLength;
    private final int mType;
    private final byte[] mData;

    public AdRecord(int length, int type, byte[] data) {
        mLength = length;
        mType = type;
        mData = data;
    }

    public int getLength() {
        return mLength;
    }

    public int getType() {
        return mType;
    }

    public byte[] getData() {
        return mData;
    }

    @Override
    public String toString() {
        return "AdRecord: length:" + mLength + ", type:" + mType + ", data:" + Arrays.toString(mData);
    }

    public static List<AdRecord> parseScanRecord(byte[] scanRecord) {
        List<AdRecord> records = new ArrayList<>();

        int index = 0;
        while (index < scanRecord.length) {
            int length = scanRecord[index++];
            //Done once we run out of records
            if (length == 0) break;

            int type = scanRecord[index];
            //Done if our record isn't a valid type
            if (type == 0) break;

            byte[] data = Arrays.copyOfRange(scanRecord, index + 1, index + length);

            records.add(new AdRecord(length, type, data));
            //Advance
            index += length;
        }
        return records;
    }
}
