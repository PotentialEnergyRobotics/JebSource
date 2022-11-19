package org.firstinspires.ftc.potencode.utils;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.potencode.utils.ButtonState;


@TeleOp (name="MotorTest")
public class MotorTest extends OpMode {
    private DcMotorEx motor1;
    private DcMotorEx motor2;
    @Override
    public void init() {
        motor1 = hardwareMap.get(DcMotorEx.class, "motor1");
        motor2 = hardwareMap.get(DcMotorEx.class, "motor2");
    }

    @Override
    public void loop() {
        motor1.setPower(gamepad1.left_stick_x);
        motor2.setPower(gamepad1.right_stick_x);
    }
}
