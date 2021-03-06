package Competition;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import Navigation.BaseNavigation;

@Autonomous(name = "Blue Alliance Right Side", group = "Competition")
//@Disabled
public class Blue_Right_Side extends BaseNavigation
{
    @Override

    protected void navigate() {

        robotInitial(false,false);

        flickJewel();

        // Getting off the balance board and get somewhere in front of crytobox.
        // Adjust the power, time, and offset.
        // The right offset is relative to the right side of the robot. If the robot needs to go
        //     more to the right, increase the value.
        get_off_Balancing_Stone(0.2, 1.0, 0.1); //


        Spin_locked_angle(-45.0, 10.0);      // facing crytobox 2-steps
        Spin_locked_angle(0.0, 10.0);        // facing crytobox

        // Ultrasonic Range sensor
        double X_distance = 60.0 - 8.5;  // to the left wall minus sensor offset
        Move_to_Distance_inch(X_distance,0,5.0);

        Glyph_Deposit();

    }

}
