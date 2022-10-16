package org.firstinspires.ftc.potencode.mechjeb.assisted;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.potencode.Jeb;
import org.firstinspires.ftc.potencode.utils.Consts;

@Autonomous(name = "Drive 2ft")
public class DriveBasic extends OpMode {
    private Jeb jeb;

    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void init() {
        jeb = new Jeb(hardwareMap, telemetry);
        jeb.initiate();
    }

    @Override
    public void start() {
        runtime.reset();
        jeb.drivePower(0, -Consts.DEFAULT_DRIVE_POWER, 0);

    }

    @Override
    public void loop() {
        telemetry.addData("runtime", runtime.toString());
        if (runtime.seconds() > 2) {
            jeb.drivePower(0, 0, 0);
        }
    }
}
