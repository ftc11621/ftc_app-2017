package TestCode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Func;

import java.util.Locale;

import Library.IMU;


@TeleOp(name = "IMU test", group = "TestCode")
public class Teleop_IMU extends LinearOpMode
{
    private IMU IMU_Object = null;

    public void runOpMode() throws InterruptedException
    {
        IMU_Object = new IMU(hardwareMap);

        // Set up our telemetry dashboard
        composeTelemetry();

        waitForStart();

        IMU_Object.start();

        while(opModeIsActive())
        {
            telemetry.update();
            idle();

        }
    }


    void composeTelemetry() {
        telemetry.addAction(new Runnable() { @Override public void run()
        {
            IMU_Object.measure();
            IMU_Object.getGravity();
        }
        });

        telemetry.addLine()
                .addData("Yaw: ", new Func<String>() {
                    @Override
                    public String value() {
                        return String.format(Locale.getDefault(), "%.1f", IMU_Object.yaw());
                    }
                });

        telemetry.addLine()
                .addData("Angular speed Yaw: ", new Func<String>() {
                    @Override
                    public String value() {
                        return String.format(Locale.getDefault(), "%.1f", IMU_Object.angular_velocity);
                    }
                });

        telemetry.addLine()
                .addData("Roll: ", new Func<String>() {
                    @Override
                    public String value() {
                        return String.format(Locale.getDefault(), "%.1f", IMU_Object.roll());
                    }
                });
        telemetry.addLine()
                .addData("Pitch: ", new Func<String>() {
                    @Override
                    public String value() {
                        return String.format(Locale.getDefault(), "%.1f", IMU_Object.pitch());
                    }
                });


        /*
        telemetry.addLine()
                .addData("Pitch: ", new Func<String>() {
                    @Override
                    public String value() {
                        return String.format(Locale.getDefault(), "%.1f", IMU_Object.gravity_x);
                    }
                });
        telemetry.addLine()
                .addData("Pitch: ", new Func<String>() {
                    @Override
                    public String value() {
                        return String.format(Locale.getDefault(), "%.1f", IMU_Object.gravity_y);
                    }
                });
        telemetry.addLine()
                .addData("Pitch: ", new Func<String>() {
                    @Override
                    public String value() {
                        return String.format(Locale.getDefault(), "%.1f", IMU_Object.gravity_z);
                    }
                });
*/
    }
}
