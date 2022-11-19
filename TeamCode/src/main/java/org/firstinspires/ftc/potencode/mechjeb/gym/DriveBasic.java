package org.firstinspires.ftc.potencode.mechjeb.gym;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.potencode.Jeb;

@Autonomous(name = "Drive Test")
@Disabled
public class DriveBasic extends OpMode {
    private Jeb jeb;

    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void init() {
        jeb = new Jeb(hardwareMap, telemetry);
        jeb.awake();
    }

    @Override
    public void start() {
        jeb.driveCentimeters(0,4,600);
    }

    @Override
    public void loop() {

    }
}
