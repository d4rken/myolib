package eu.darken.myolib.processor.classifier;


import eu.darken.myolib.processor.BaseDataPacket;
import eu.darken.myolib.tools.ByteHelper;

public class SyncFailedClassifierEvent extends ClassifierEvent {
    /**
     * Possible outcomes when the user attempts a sync gesture.
     */
    public enum SyncResult {
        /**
         * Sync gesture was performed too hard.
         */
        FAILED_TOO_HARD((byte) 0x01);

        private final byte mValue;

        SyncResult(byte value) {
            mValue = value;
        }

        public byte getValue() {
            return mValue;
        }
    }

    private SyncResult mSyncResult;

    public SyncFailedClassifierEvent(BaseDataPacket packet) {
        super(packet, Type.SYNC_FAILED);
        ByteHelper byteHelper = new ByteHelper(packet.getData());
        int typeValue = byteHelper.getUInt8();
        if (getType().getValue() != typeValue)
            throw new RuntimeException("Incompatible BaseDataPacket:" + typeValue);

        int syncResultValue = byteHelper.getUInt8();
        for (SyncFailedClassifierEvent.SyncResult syncResult : SyncFailedClassifierEvent.SyncResult.values()) {
            if (syncResult.getValue() == syncResultValue) {
                mSyncResult = syncResult;
                break;
            }
        }
    }

    public SyncResult getSyncResult() {
        return mSyncResult;
    }

}
