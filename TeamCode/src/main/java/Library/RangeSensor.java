package Library;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchImpl;
import static android.os.SystemClock.sleep;

public class RangeSensor {

    private static final int RANGE_REG_START = 0x04; //Register to start reading
    private static final int RANGE_READ_LENGTH = 2; //Number of byte to read
    private I2cDevice RANGE_1;
    private I2cDeviceSynchImpl RANGE_1_Reader;


    public RangeSensor(HardwareMap hardwareMap) {    // constructor to create object
        RANGE_1       = hardwareMap.i2cDevice.get("rangeSensor_1");
        RANGE_1_Reader= new I2cDeviceSynchImpl(RANGE_1, I2cAddr.create8bit(0x28), false);
        RANGE_1_Reader.engage();
    }

    public int getDistance_1_cm(int max_distance) {
        return getSensorDistance(max_distance, RANGE_1_Reader);
    }

    private int getSensorDistance(int max_distance, I2cDeviceSynchImpl reader) {
        byte[] range_Cache;

        range_Cache = reader.read(RANGE_REG_START, RANGE_READ_LENGTH);
        int dist = range_Cache[0] & 0xFF;
        while(dist > max_distance) {
            range_Cache = reader.read(RANGE_REG_START, RANGE_READ_LENGTH);
            dist = range_Cache[0] & 0xFF;
            sleep(10);
        }
        return dist;
    }

}
