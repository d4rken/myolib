/*
 * darken's Myo lib
 * Matthias Urhahn (matthias.urhahn@rwth-aachen.de)
 * mHealth - Uniklinik RWTH-Aachen.
 */
package eu.darken.myolib.processor;

import eu.darken.myolib.services.Imu;

/**
 * Processor that converts {@link BaseDataPacket} object into {@link ImuData} objects.
 */
public class ImuProcessor extends BaseProcessor {
    private static final String TAG = "MyoLib:ImuProcessor";

    public ImuProcessor() {
        super();
        getSubscriptions().add(Imu.IMUDATA.getCharacteristicUUID());
    }

    @Override
    protected void doProcess(BaseDataPacket packet) {
        ImuData imuData = ImuData.from(packet);

        for (DataListener listener : getDataListeners()) {
            ImuDataListener imuListener = (ImuDataListener) listener;
            imuListener.onNewImuData(imuData);
        }
    }


    public interface ImuDataListener extends DataListener {
        void onNewImuData(ImuData imuData);
    }

}