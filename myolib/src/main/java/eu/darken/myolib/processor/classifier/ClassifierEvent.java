package eu.darken.myolib.processor.classifier;

import eu.darken.myolib.processor.DataPacket;

/**
 * https://github.com/thalmiclabs/myo-bluetooth/blob/master/myohw.h#L345
 */
public class ClassifierEvent extends DataPacket {
    /**
     * Types of classifier events.
     */
    public enum Type {
        ARM_SYNCED((byte) 0x01), ARM_UNSYNCED((byte) 0x02), POSE((byte) 0x03), UNLOCKED((byte) 0x04), LOCKED((byte) 0x05), SYNC_FAILED((byte) 0x06), WARM_UP_RESULT((byte) 0x07);
        private final byte mValue;

        Type(byte value) {
            mValue = value;
        }

        public byte getValue() {
            return mValue;
        }
    }

    private final Type mType;

    protected ClassifierEvent(DataPacket packet, Type type) {
        super(packet.getDeviceAddress(), packet.getTimeStamp());
        mType = type;
    }

    public Type getType() {
        return mType;
    }

}
