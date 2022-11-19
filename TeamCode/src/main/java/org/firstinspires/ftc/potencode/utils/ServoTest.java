package org.firstinspires.ftc.potencode.utils;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.potencode.utils.ButtonState;


@TeleOp (name="ServoTest")
public class ServoTest extends OpMode {
    private CRServo servo1;
    private CRServo servo2;
    private ButtonState servoToggle;
    @Override
    public void init() {
        servo1 = hardwareMap.get(CRServo.class, "servo1");
        servo2 = hardwareMap.get(CRServo.class, "servo2");

        servoToggle = new ButtonState();
    }

    @Override
    public void loop() {
        servoToggle.update(gamepad1.x);
        if (gamepad1.right_bumper) {
            servo1.setPower(servoToggle.buttonState ? 1 : -1);
            servo2.setPower(servoToggle.buttonState ? -1 : 1);
        } else {
            servo1.setPower(0);
            servo2.setPower(0);
        }
    }
}
