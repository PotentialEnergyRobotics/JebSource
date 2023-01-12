package org.firstinspires.ftc.potencode;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp(name = "Jeb Test")
public class JebTests extends OpMode {
    private Jeb jeb;

    @Override
    public void init() {
        jeb = new Jeb(hardwareMap, telemetry);
        jeb.awake();
    }

    @Override
    public void loop() {
        telemetry.addData("color proximity", jeb.intakeColor.getDistance(DistanceUnit.MM));

    }
}
