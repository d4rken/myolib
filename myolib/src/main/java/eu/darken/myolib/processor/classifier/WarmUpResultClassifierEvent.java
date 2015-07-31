package eu.darken.myolib.processor.classifier;

public class WarmUpResultClassifierEvent extends ClassifierEvent {
    /**
     * Possible warm-up results for Myo.
     */
    public enum WarmUpResult {
        UNKNOWN((byte) 0x00), SUCCESS((byte) 0x01), FAILED_TIMEOUT((byte) 0x02);
        private final byte mValue;

        WarmUpResult(byte value) {
            mValue = value;
        }

        public byte getValue() {
            return mValue;
        }
    }

    private WarmUpResult mWarmUpResult;

    public WarmUpResultClassifierEvent() {
        super(Type.WARM_UP_RESULT);
    }

    public WarmUpResult getWarmUpResult() {
        return mWarmUpResult;
    }

    public void setWarmUpResult(WarmUpResult warmUpResult) {
        mWarmUpResult = warmUpResult;
    }
}
