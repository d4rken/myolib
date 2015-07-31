package eu.darken.myolib.processor.classifier;

import eu.darken.myolib.processor.BaseDataPacket;
import eu.darken.myolib.tools.ByteHelper;

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

    public WarmUpResultClassifierEvent(BaseDataPacket packet) {
        super(packet, Type.WARM_UP_RESULT);
        ByteHelper byteHelper = new ByteHelper(packet.getData());
        int typeValue = byteHelper.getUInt8();
        if (getType().getValue() != typeValue)
            throw new RuntimeException("Incompatible BaseDataPacket:" + typeValue);

        int warmUpResultValue = byteHelper.getUInt8();
        for (WarmUpResultClassifierEvent.WarmUpResult warmUpResult : WarmUpResultClassifierEvent.WarmUpResult.values()) {
            if (warmUpResult.getValue() == warmUpResultValue) {
                mWarmUpResult = warmUpResult;
                break;
            }
        }
    }

    public WarmUpResult getWarmUpResult() {
        return mWarmUpResult;
    }

    public void setWarmUpResult(WarmUpResult warmUpResult) {
        mWarmUpResult = warmUpResult;
    }
}
