package eu.darken.myolib.processor.imu;


import eu.darken.myolib.processor.DataPacket;

public class MotionEvent extends DataPacket {
    /**
     * Types of motion events.
     */
    public enum Type {
        TAP((byte) 0x00);
        private final byte mValue;

        Type(byte value) {
            mValue = value;
        }

        public byte getValue() {
            return mValue;
        }
    }

    private final Type mType;

    public MotionEvent(DataPacket packet, Type type) {
        super(packet);
        mType = type;
    }


    public Type getType() {
        return mType;
    }
}
