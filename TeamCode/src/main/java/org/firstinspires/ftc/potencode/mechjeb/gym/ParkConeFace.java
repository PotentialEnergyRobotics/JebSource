package org.firstinspires.ftc.potencode.mechjeb.gym;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.potencode.Jeb;
import org.firstinspires.ftc.potencode.mechjeb.assisted.Motion;

public class ParkConeFace extends OpMode {
    private Jeb jeb;

    private int stage = 0;
    private Motion[] motions = new Motion[2];

    @Override
    public void init() {
        jeb = new Jeb(hardwareMap, telemetry);
        jeb.awake();
    }

    @Override
    public void loop() {
        motions[stage].run();

        if (motions[stage].isEnd()) {
            motions[stage].cleanup();
            if (stage < motions.length - 1) motions[++stage].init();
            else stop();
        }
    }
}
