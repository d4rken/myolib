package eu.darken.myolib.processor.imu;


public class MotionEvent {
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

    public MotionEvent(Type type) {
        mType = type;
    }

    public Type getType() {
        return mType;
    }
}
