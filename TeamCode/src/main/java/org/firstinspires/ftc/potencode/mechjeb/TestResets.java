package org.firstinspires.ftc.potencode.mechjeb;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.potencode.Consts;
import org.firstinspires.ftc.potencode.Jeb;

@Autonomous(name = "Test Resets")
public class TestResets extends OpMode {
    private Jeb jeb;

    private boolean bagIsReset = false;

    private ElapsedTime runtime;

    @Override
    public void init() {
        jeb = new Jeb(hardwareMap, telemetry);
        jeb.awake();

        runtime = new ElapsedTime();

        // steps
        // put gear to encoder
        // stop and reset position
        // go to known upright position
        // reset slide to encoder
        // safety checks??

//        if (!jeb.limitBag.isPressed()) jeb.bagMotor.setPower(-Consts.DEFAULT_ARM_POWER);
    }

    @Override
    public void init_loop() {
        if (jeb.limitBag.isPressed() && !bagIsReset) {
            bagIsReset = true;
//            jeb.bagMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//            jeb.bagMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//            jeb.bagMotor.setPower(Consts.DEFAULT_ARM_POWER);
            runtime.reset();
        }

        if (runtime.milliseconds() > 6000 && bagIsReset) {
//            jeb.bagMotor.setPower(0);
        }

    }

    @Override
    public void loop() {
//        if (jeb.limitBag.isPressed()) jeb.bagMotor.setPower(0);
//        else if (!jeb.limitBag.isPressed() || gamepad2.left_stick_y < 0) jeb.bagMotor.setPower(gamepad2.left_stick_y);
    }
}
