package org.firstinspires.ftc.potencode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.potencode.utils.ButtonState;

@TeleOp(name="JebbyOp")
public class JebbyOp extends OpMode {
    private Jeb jeb;

    private ButtonState backButtonToggle;
    private ButtonState rightBumperToggle;
    private ButtonState leftBumperToggle;

    private double driveSpeedModifier;

    public static double driveX;
    public static double driveY;
    public static double driveTurn;

    @Override
    public void init() {
        jeb = new Jeb(hardwareMap, telemetry);
        jeb.awake();

        jeb.resetDriveEncoders();
        jeb.leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        jeb.rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        jeb.frontMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        jeb.backMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        jeb.armMotorA.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        jeb.armMotorB.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        jeb.armMotorA.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        jeb.armMotorB.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        backButtonToggle = new ButtonState();
        rightBumperToggle = new ButtonState();
        leftBumperToggle = new ButtonState();
    }

    @Override
    public void loop() {
        // drive
        backButtonToggle.update(gamepad1.back);
        telemetry.addData("FOD", backButtonToggle.buttonState);

        driveSpeedModifier = Consts.DEFAULT_DRIVE_POWER + gamepad1.left_trigger * (1 - Consts.DEFAULT_DRIVE_POWER) - gamepad1.right_trigger * Consts.DEFAULT_DRIVE_POWER;
        driveSpeedModifier = Range.clip(driveSpeedModifier,  Consts.MIN_DRIVE_POWER, 1);
        telemetry.addData("Move speed modifier", driveSpeedModifier);

        driveX = -gamepad1.left_stick_x * driveSpeedModifier;
        driveY = -gamepad1.left_stick_y * driveSpeedModifier;
        driveTurn = gamepad1.right_stick_x * driveSpeedModifier;

        if (gamepad1.x) {
            jeb.resetAngle();
        }

        if (backButtonToggle.buttonState) {
            jeb.driveFOD(driveX, driveY, driveTurn);
        }
        else {
            jeb.driveVelocity(driveX, driveY, driveTurn);
        }

        // arm

        telemetry.addData("gear pos", jeb.armMotorA.getCurrentPosition());
        telemetry.addData("slide pos", jeb.armMotorB.getCurrentPosition());
        telemetry.addData("limit", jeb.limit.isPressed());

        // todo the gear should only move if the slide is down!!

//        if ((jeb.armMotorA.getCurrentPosition() < Consts.MAX_ARM_A_POS || gamepad2.left_stick_y < 0) && (!jeb.limit.isPressed() || gamepad2.left_stick_y < 0))
//            jeb.armMotorA.setPower(-gamepad2.left_stick_y);
//        else {
//            jeb.armMotorA.setPower(0);
//        }

        if (jeb.limit.isPressed()) {
            jeb.armMotorA.setPower(0);
        }
        if (!jeb.limit.isPressed() || gamepad2.left_stick_y < 0) {
            jeb.armMotorA.setPower(-gamepad2.left_stick_y * Consts.TICKS_PER_POWER);
        }

//        if ((jeb.armMotorB.getCurrentPosition() < Consts.MAX_ARM_B_POS || gamepad2.right_stick_y > 0) &&
//                (jeb.armMotorB.getCurrentPosition() > Consts.MIN_ARM_B_POS || gamepad2.right_stick_y < 0)) {
//            jeb.armMotorB.setPower(gamepad2.right_stick_y);
//        }
//        else {
//            jeb.armMotorB.setPower(0);
//        }
        jeb.armMotorB.setPower(-gamepad2.right_stick_y);

        // claw
        rightBumperToggle.update(gamepad2.right_bumper);
        telemetry.addData("claw in", rightBumperToggle.buttonState);
        leftBumperToggle.update(gamepad2.left_bumper);
        telemetry.addData("claw on", leftBumperToggle.buttonState);

        if (leftBumperToggle.buttonState) {
            jeb.clawA.setPower(rightBumperToggle.buttonState ? -Consts.DEFAULT_ARM_POWER : Consts.DEFAULT_ARM_POWER);
            jeb.clawB.setPower(rightBumperToggle.buttonState ? Consts.DEFAULT_ARM_POWER : -Consts.DEFAULT_ARM_POWER);
        }
        else {
            jeb.clawA.setPower(0);
            jeb.clawB.setPower(0);
        }

    }



}
