package Library;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.Disabled;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.util.Hardware;
import  com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

//import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

import java.util.Locale;

/**
 * {@link IMU} gives a short demo on how to use the BNO055 Inertial Motion Unit (IMU) from AdaFruit.
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 * @see <a href="http://www.adafruit.com/products/2472">Adafruit IMU</a>
 */

public class IMU    {

    // The IMU sensor object
    private BNO055IMU imu;

    // State used for updating telemetry
    private Orientation angles;
    //private double yaw_initial, roll_initial, pitch_initial;
    private Acceleration gravity;
    public double gravity_x, gravity_y, gravity_z;
    private double yaw_value;
    public float angular_velocity;
    private ElapsedTime elapsed_time = new ElapsedTime();
    private double last_measured_time = -100.0;

    private BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

    public IMU (HardwareMap hardwareMap) {
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);
    }

    public void start ()  {  // Start the logging of measured acceleration
        imu.startAccelerationIntegration(new Position(), new Velocity(), 10);
        measure();           // get initial angles as the reference
        elapsed_time.reset();
    }


    public boolean measure () {   // measure angles
        if ((elapsed_time.milliseconds() - last_measured_time) > 10.0) {
            angular_velocity = imu.getAngularVelocity().zRotationRate;
            angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
            yaw_value = AngleUnit.DEGREES.normalize(angles.angleUnit.DEGREES.fromUnit(angles.angleUnit, angles.firstAngle));
            last_measured_time = elapsed_time.milliseconds();
            return true;
        } else {
            return false;
        }
    }
    public void getGravity() {
        gravity  = imu.getGravity();
        gravity_x = gravity.xAccel*gravity.xAccel;
        gravity_y = gravity.yAccel*gravity.yAccel;
        gravity_z = gravity.zAccel*gravity.zAccel;
    }
    public double yaw() {   // yaw, positive counterclockwise -180 to 180
        return yaw_value;
        //return AngleUnit.DEGREES.normalize(angles.angleUnit.DEGREES.fromUnit(angles.angleUnit, angles.firstAngle));
    }
    public double roll() {   // positive when fall back
        return AngleUnit.DEGREES.normalize(angles.angleUnit.DEGREES.fromUnit(angles.angleUnit, angles.secondAngle));
    }
    public double pitch() {   // positive tilt to the right
        return AngleUnit.DEGREES.normalize(angles.angleUnit.DEGREES.fromUnit(angles.angleUnit, angles.thirdAngle));
    }
}