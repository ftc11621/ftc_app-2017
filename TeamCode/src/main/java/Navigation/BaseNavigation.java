package Navigation;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import javax.microedition.khronos.opengles.GL;

import Library.Glypher;
import Library.JewelServo;
import Library.Mecanum;
import Library.REVColorDistance;
import Library.VuforiaNavigation;

public abstract class BaseNavigation extends LinearOpMode {

    private JewelServo JewelFlicker = null;
    private REVColorDistance Colordistance = null;
    private Mecanum mecanumDrive = null;
    private Glypher GlypherObject = null;
    private VuforiaNavigation vuforiaObject = null;

    boolean isRedAlliance, isLeftSide;
    int     flickDirection = 0;
    ElapsedTime basenavigation_elapsetime = new ElapsedTime();

    final double Initial_orientation = 90.0;   // initial robot orientatian respect to Jewels

    @Override
    public void runOpMode() {

        JewelFlicker = new JewelServo(hardwareMap);
        Colordistance = new REVColorDistance(hardwareMap);
        mecanumDrive = new Mecanum(hardwareMap);
        GlypherObject = new Glypher(hardwareMap);
        vuforiaObject = new VuforiaNavigation(true , false);  // true=extended Tracking of a target picture


        waitForStart();

        mecanumDrive.Start();

        navigate();

        while (opModeIsActive()) // after autonomous is done wait for manual stop or stop after the timer
        {
            idle();
        }
    }

    protected abstract void navigate();



    // =========================== Initial Robot placement ==================
    public void robotInitial(boolean isRed, boolean isLeft) {
        isRedAlliance = isRed;
        isLeftSide = isLeft;
        mecanumDrive.setCurrentAngle(Initial_orientation);
        mecanumDrive.set_angle_locked(Initial_orientation);     // to the right of Jewel
        mecanumDrive.set_Angle_tolerance(3.0);
    }

    public void vuforia_activate() {
        vuforiaObject.activate();
    }


    // ============================ testing purpose ===================================
    public void NavigationTest() {
        double timeoutsec = 5.0;
        double testpower = 0.4;
        double waittime = 2.0;

        mecanumDrive.set_Angle_tolerance(5.0);

        telemetry.addData("angle 0:", mecanumDrive.getRobotAngle());

        //mecanumDrive.set_angle_locked(0.0);
        mecanumDrive.spin_Motor_angle_locked_with_Timer(timeoutsec, testpower, 0.0);
        //mecanumDrive.run_Motor_angle_locked_with_Timer(0.0, 0.0, 5.0, 0.4);
        //mecanumDrive.stop_Motor_with_locked();
        telemetry.addData("angle 1:", mecanumDrive.getRobotAngle());

        // pause 2 seconds
        mecanumDrive.run_Motor_angle_locked_with_Timer(0.0, 0.0, waittime, 0.0);

        //mecanumDrive.set_angle_locked(90.0);
        mecanumDrive.spin_Motor_angle_locked_with_Timer(timeoutsec, testpower, 90.0);
        //mecanumDrive.run_Motor_angle_locked_with_Timer(0.0, 0.0, 5.0, 0.4);
        //mecanumDrive.stop_Motor_with_locked();
        telemetry.addData("angle 2:", mecanumDrive.getRobotAngle());

        // pause 2 seconds
        mecanumDrive.run_Motor_angle_locked_with_Timer(0.0, 0.0, waittime, 0.0);

        mecanumDrive.spin_Motor_angle_locked_with_Timer(timeoutsec, testpower, 180.0);

        // pause 2 seconds
        mecanumDrive.run_Motor_angle_locked_with_Timer(0.0, 0.0, waittime, 0.0);

        mecanumDrive.spin_Motor_angle_locked_with_Timer(timeoutsec, testpower, -90.0);

        // pause 2 seconds
        mecanumDrive.run_Motor_angle_locked_with_Timer(0.0, 0.0, waittime, 0.0);

        mecanumDrive.spin_Motor_angle_locked_with_Timer(timeoutsec, testpower,0.0);

        telemetry.update();
    }



    public void Robot_Forward(double time_sec, double power, double angle) {
        //mecanumDrive.set_angle_locked(Initial_orientation);
        mecanumDrive.set_angle_locked(Initial_orientation + angle); //Initial_orientation + 15.0);
        mecanumDrive.run_Motor_angle_locked_with_Timer(0.0, 1.0, time_sec, power);
    }

    public void Robot_Reverse(double time_sec, double power, double setangle) {
        //mecanumDrive.set_angle_locked(Initial_orientation);
        //telemetry.addData("angle", mecanumDrive.IMU_getAngle());
        telemetry.addData("set angle", setangle);
        telemetry.addData("locked angle", Initial_orientation);
        mecanumDrive.set_angle_locked(Initial_orientation + setangle); //Initial_orientation + 15.0);
        telemetry.addData("new angle", mecanumDrive.get_locked_angle());

        mecanumDrive.run_Motor_angle_locked_with_Timer(0.0, -1.0, time_sec, power);
        //telemetry.addData("new angle", mecanumDrive.IMU_getAngle());
        telemetry.update();
    }

    public void Robot_Glyph_Deposit() {
        // kick glyph out
        GlypherObject.RunGlypherMotor(-1); // bring down glyph
        mecanumDrive.run_Motor_angle_locked_with_Timer(0, -1, 1.0, 0.0); // wait for glyph to go down

        mecanumDrive.run_Motor_angle_locked_with_Timer(0, -1, 0.5, 0.1); // move back a little
        GlypherObject.RunGlypherMotor(0);

        //Robot_Turn( 2, .2, 45);

        //Robot_Turn( 2, .2, 45);

        mecanumDrive.run_Motor_angle_locked_with_Timer(-1, 0, 1.5, 0.1); // hit glyph from the side

        //mecanumDrive.run_Motor_angle_locked_with_Timer(0, 1, 1.5, 0.1); // hit glyph from the side

    }

    public void Reset_locked_angle() {
        mecanumDrive.set_angle_locked(Initial_orientation);
    }



    // ====================================================================
    // Jewel Flicker method
    protected void flickJewel() {

        double turn_power = 0.2;
        double timeoutsec = 2.5;
        double turn_angle = 15.0;

        JewelFlicker.Initial();

        flickDirection = JewelFlicker.flickJewel(isRedAlliance); // 0=color not detected, 1=left,-1=right
        telemetry.addData("Direction :", flickDirection);
        telemetry.addData("Red value : ", JewelFlicker.readRed);
        telemetry.addData("Blue value: ", JewelFlicker.readBlue);
        telemetry.update();

        if ( Math.abs(flickDirection) > 0) {
            // Robot_Turn(timeoutsec, turn_power, flickDirection * turn_angle);
            mecanumDrive.spin_Motor_angle_locked_with_Timer(timeoutsec, turn_power, flickDirection * turn_angle + Initial_orientation);
        }
        JewelFlicker.Initial();

    }



    // ===========================Get of the Balancing Stone =========================================
    protected void get_off_Balancing_Stone() {

        double powerset = 0.2;
        double timeoutset = 1.5;

        telemetry.addData("Jewel direction: ", flickDirection );

        if (isRedAlliance) {  // move forward
            mecanumDrive.run_Motor_angle_locked_with_Timer(flickDirection * Math.sin(Math.toRadians(15.0)), Math.cos(Math.toRadians(15.0)), timeoutset, powerset);

        } else {                // go backward
            mecanumDrive.run_Motor_angle_locked_with_Timer(-1.0*flickDirection * Math.sin(Math.toRadians(15.0)), -Math.cos(Math.toRadians(15.0)), timeoutset, powerset);
        }

        telemetry.update();
    }



    // =========================== Move to X-Y location =====================

    protected void vuforia_robotMove_XY_inch(double Xloc, double Yloc) {

        double maxpower = 0.1;

        basenavigation_elapsetime.reset();

        double distanceX, distanceY, total_distance;
        double cryto_offset_x = 0.0;
        double cryto_offset_y = 0.0;

        while (basenavigation_elapsetime.seconds() < 5.0 && opModeIsActive()) {
            if (vuforiaObject.isTarget_visible()) {
                // telemetry.addData("Vuforia", "Visible");

                if (vuforiaObject.updateRobotLocation()) {
                    telemetry.addData("Location Update:", "Yes");
                } else {
                    telemetry.addData("Location Update:", "No");
                }

                if (isRedAlliance) {
                    if (isLeftSide) {
                        cryto_offset_x = vuforiaObject.crytobox_offset_inch;
                    } else {
                        cryto_offset_y = -vuforiaObject.crytobox_offset_inch;
                    }
                } else {
                    if (isLeftSide) {
                        cryto_offset_y = vuforiaObject.crytobox_offset_inch;
                    } else {
                        cryto_offset_x = vuforiaObject.crytobox_offset_inch;
                    }
                }

                Xloc += cryto_offset_x;
                Yloc += cryto_offset_y;

                distanceX = Xloc - vuforiaObject.getXcoordinate_mm() / 25.4;
                distanceY = vuforiaObject.getYcoordinate_mm() / 25.4 - Yloc;
                total_distance = Math.hypot(distanceX, distanceY);

                mecanumDrive.set_max_power(Math.min(total_distance / 5.0, maxpower));

                mecanumDrive.run_Motor_angle_locked(distanceX / total_distance, distanceY / total_distance);
                telemetry.addData("X distance to correct cryto column: ", distanceX);
                telemetry.addData("Y distance to correct cryto column: ", distanceY);
                telemetry.addData("Crytobox column offset: ", vuforiaObject.crytobox_offset_inch);
            } else {
                telemetry.addData("Vuforia", "NOT Visible");
                mecanumDrive.stop_Motor_with_locked();
                // do non vuforia autonomous
            }

            telemetry.update();

            idle();
        }
    }

        // =========================== Move to X-Y location =====================

    protected void vuforia_Move_XY_inch_point_picture(double Xloc, double Yloc, double timeout) {

        double maxpower = 0.1;
        double PID_kp = 0.02;
        double PID_ki = 0.00001;

        basenavigation_elapsetime.reset();

        double distanceX, distanceY, total_distance;
        double cryto_offset_x = 0.0;
        double cryto_offset_y = 0.0;
        double ki_sum_power = 0.0;

        while (basenavigation_elapsetime.seconds() < timeout && opModeIsActive()) {
            if(vuforiaObject.isTarget_visible()) {

                // point toward the picture
                mecanumDrive.set_angle_locked(mecanumDrive.get_locked_angle()+vuforiaObject.getAngleTowardPicture());

                if (vuforiaObject.updateRobotLocation()) {
                    telemetry.addData("Location Update:", "Yes");
                } else {
                    telemetry.addData("Location Update:", "No");
                }

                if (isRedAlliance) {
                    if(isLeftSide) {
                        cryto_offset_x = vuforiaObject.crytobox_offset_inch;
                    } else {
                        cryto_offset_y = -vuforiaObject.crytobox_offset_inch;
                    }
                } else {
                    if(isLeftSide) {
                        cryto_offset_y = vuforiaObject.crytobox_offset_inch;
                    } else {
                        cryto_offset_x = vuforiaObject.crytobox_offset_inch;
                    }
                }

                Xloc += cryto_offset_x;
                Yloc += cryto_offset_y;

                distanceX = Xloc - vuforiaObject.getXcoordinate_mm()/25.4;
                distanceY = vuforiaObject.getYcoordinate_mm()/25.4 - Yloc;
                total_distance = Math.hypot(distanceX,distanceY);

                ki_sum_power += total_distance * PID_ki;
                double total_power = total_distance * PID_kp + ki_sum_power;

                mecanumDrive.set_max_power(Math.min(total_power, maxpower));

                // mecanumDrive.run_Motor_angle_locked(distanceX / total_distance,distanceY / total_distance);

                mecanumDrive.run_Motor_angle_locked_relative_to_driver(distanceX / total_distance,distanceY / total_distance);

                telemetry.addData("X distance to correct cryto column: ", distanceX);
                telemetry.addData("Y distance to correct cryto column: ", distanceY);
                telemetry.addData("Crytobox column offset: ", vuforiaObject.crytobox_offset_inch);
            } else {
                telemetry.addData("Vuforia", "NOT Visible");
                mecanumDrive.stop_Motor_with_locked();
                // do non vuforia autonomous
            }

            telemetry.update();

            idle();
        }
    }

    protected boolean vuforia_find_picture () {
        basenavigation_elapsetime.reset();

        while (opModeIsActive() && !vuforiaObject.isTarget_visible()) {
            mecanumDrive.spin_Motor_angle_locked_with_Timer(1.0, 0.05, mecanumDrive.get_locked_angle() + 20.0);
            idle();
        }

        if (vuforiaObject.isTarget_visible()) {
            telemetry.addData("Picture:" , "Found");
        } else {
            telemetry.addData("Picture:", "NOT found");
        }
        telemetry.update();

        return vuforiaObject.isTarget_visible();
    }

}