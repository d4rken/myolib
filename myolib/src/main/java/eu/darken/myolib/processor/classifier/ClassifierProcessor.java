package eu.darken.myolib.processor.classifier;

import java.util.Arrays;

import eu.darken.myolib.processor.BaseDataPacket;
import eu.darken.myolib.processor.BaseProcessor;
import eu.darken.myolib.services.Classifier;
import eu.darken.myolib.tools.ByteHelper;
import eu.darken.myolib.tools.Logy;

/**
 * @see <a href="https://github.com/thalmiclabs/myo-bluetooth/blob/master/myohw.h#L345">Myo Bluetooth Protocol</a>
 */
public class ClassifierProcessor extends BaseProcessor {
    private static final String TAG = "MyoLib:ClassifierProcessor";

    public ClassifierProcessor() {
        super();
        getSubscriptions().add(Classifier.CLASSIFIEREVENT.getCharacteristicUUID());
    }

    @Override
    protected void doProcess(BaseDataPacket packet) {
        Logy.v(TAG, Arrays.toString(packet.getData()));

        ByteHelper byteHelper = new ByteHelper(packet.getData());
        int typeValue = byteHelper.getUInt8();
        ClassifierEvent event = null;

        if (typeValue == ClassifierEvent.Type.ARM_SYNCED.getValue()) {
            ArmSyncedClassifierEvent _event = new ArmSyncedClassifierEvent();
            int armValue = byteHelper.getUInt8();
            for (ArmSyncedClassifierEvent.Arm arm : ArmSyncedClassifierEvent.Arm.values()) {
                if (arm.getValue() == armValue) {
                    _event.setArm(arm);
                    break;
                }
            }
            int directionValue = byteHelper.getUInt8();
            for (ArmSyncedClassifierEvent.Direction direction : ArmSyncedClassifierEvent.Direction.values()) {
                if (direction.getValue() == directionValue) {
                    _event.setDirection(direction);
                    break;
                }
            }
            // https://github.com/logotype/myodaemon/blob/master/native-osx/libs/myo.framework/Versions/A/Headers/cxx/impl/Hub_impl.hpp#L144
            float myoRotationValue = 0;
            if (packet.getData().length > 3) {
                int warmUpStateValue = byteHelper.getUInt8();
                for (ArmSyncedClassifierEvent.WarmUpState warmUpState : ArmSyncedClassifierEvent.WarmUpState.values()) {
                    if (warmUpState.getValue() == warmUpStateValue) {
                        _event.setWarmUpState(warmUpState);
                        break;
                    }
                }
                myoRotationValue = byteHelper.getUInt16() / 16384.0f;
            }
            event = _event;
            Logy.v(TAG, "typeValue:" + typeValue + " Arm:" + _event.getArm() + " Direction:" + _event.getDirection() + " WarmUpState:" + _event.getWarmUpState() + " MyoRotation:" + myoRotationValue);
        } else if (typeValue == ClassifierEvent.Type.ARM_UNSYNCED.getValue()) {
            event = new ClassifierEvent(ClassifierEvent.Type.ARM_UNSYNCED);
        } else if (typeValue == ClassifierEvent.Type.POSE.getValue()) {
            PoseClassifierEvent _event = new PoseClassifierEvent();
            int poseValue = byteHelper.getUInt16();
            for (PoseClassifierEvent.Pose pose : PoseClassifierEvent.Pose.values()) {
                if (pose.getValue() == poseValue) {
                    _event.setPose(pose);
                    break;
                }
            }
            event = _event;
            Logy.v(TAG, "typeValue:" + typeValue + " Pose:" + _event.getPose());
        } else if (typeValue == ClassifierEvent.Type.UNLOCKED.getValue()) {
            event = new ClassifierEvent(ClassifierEvent.Type.UNLOCKED);
        } else if (typeValue == ClassifierEvent.Type.LOCKED.getValue()) {
            event = new ClassifierEvent(ClassifierEvent.Type.LOCKED);
        } else if (typeValue == ClassifierEvent.Type.SYNC_FAILED.getValue()) {
            SyncFailedClassifierEvent _event = new SyncFailedClassifierEvent();
            int syncResultValue = byteHelper.getUInt8();
            for (SyncFailedClassifierEvent.SyncResult syncResult : SyncFailedClassifierEvent.SyncResult.values()) {
                if (syncResult.getValue() == syncResultValue) {
                    _event.setSyncResult(syncResult);
                    break;
                }
            }
            event = _event;
            Logy.v(TAG, "typeValue:" + typeValue + " syncResult:" + _event.getSyncResult());
        } else if (typeValue == ClassifierEvent.Type.WARM_UP_RESULT.getValue()) {
            WarmUpResultClassifierEvent _event = new WarmUpResultClassifierEvent();
            int warmUpResultValue = byteHelper.getUInt8();
            for (WarmUpResultClassifierEvent.WarmUpResult warmUpResult : WarmUpResultClassifierEvent.WarmUpResult.values()) {
                if (warmUpResult.getValue() == warmUpResultValue) {
                    _event.setWarmUpResult(warmUpResult);
                    break;
                }
            }
            event = _event;
            Logy.v(TAG, "typeValue:" + typeValue + " warmUpResult:" + _event.getWarmUpResult());
        }
        if (event == null) {
            Logy.e(TAG, "Unknown classifier event type!");
            return;
        }
        for (DataListener listener : getDataListeners()) {
            ClassifierEventListener motionEventListener = (ClassifierEventListener) listener;
            motionEventListener.onClassifierEvent(event);
        }
    }

    public interface ClassifierEventListener extends DataListener {

        void onClassifierEvent(ClassifierEvent classifierEvent);

    }

    public void addListener(ClassifierEventListener listener) {
        super.addDataListener(listener);
    }
}
