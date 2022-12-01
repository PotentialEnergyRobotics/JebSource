package org.firstinspires.ftc.potencode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.potencode.utils.ButtonState;
import org.firstinspires.ftc.robotcore.external.Const;

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

    private ElapsedTime bagRuntime;

    @Override
    public void init() {
        jeb = new Jeb(hardwareMap, telemetry);
        jeb.awake();

        jeb.resetDriveEncoders();
        jeb.leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        jeb.rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        jeb.frontMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        jeb.backMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        jeb.bagMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        jeb.slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        jeb.bagMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        jeb.slideMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        backButtonToggle = new ButtonState();
        rightBumperToggle = new ButtonState();
        leftBumperToggle = new ButtonState();

        jeb.bagMotor.setPower(Consts.DEFAULT_ARM_POWER);
        jeb.slideMotor.setPower(Consts.DEFAULT_ARM_POWER);
    }

    @Override
    public void init_loop() {
        if (!jeb.limitBag.isPressed() && !jeb.limitSlide.isPressed()) {
            jeb.zeros();
            bagRuntime.reset();
        }
        else {
            jeb.bagMotor.setPower(-Consts.DEFAULT_ARM_POWER);
        }
    }

    @Override
    public void loop() {
        // drive
        backButtonToggle.update(gamepad1.back);
        telemetry.addData("FOD", backButtonToggle.buttonState);

        driveSpeedModifier = Consts.DEFAULT_DRIVE_POWER + gamepad1.left_trigger * (1 - Consts.DEFAULT_DRIVE_POWER) - gamepad1.right_trigger * Consts.DEFAULT_DRIVE_POWER;
        driveSpeedModifier = Range.clip(driveSpeedModifier,  Consts.MIN_DRIVE_POWER, 1);
        telemetry.addData("Move speed modifier", driveSpeedModifier);

        driveX = Math.pow(-gamepad1.left_stick_x * driveSpeedModifier, 3);
        driveY = Math.pow(-gamepad1.left_stick_y * driveSpeedModifier, 3);
        driveTurn = Math.pow(gamepad1.right_stick_x * driveSpeedModifier, 3);

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

        telemetry.addData("gear pos", jeb.bagMotor.getCurrentPosition());
        telemetry.addData("slide pos", jeb.slideMotor.getCurrentPosition());
        telemetry.addData("limit", jeb.limitBag.isPressed());

        // todo the gear should only move if the slide is down!!

//        if ((jeb.armMotorA.getCurrentPosition() < Consts.MAX_ARM_A_POS || gamepad2.left_stick_y < 0) && (!jeb.limit.isPressed() || gamepad2.left_stick_y < 0))
//            jeb.armMotorA.setPower(-gamepad2.left_stick_y);
//        else {
//            jeb.armMotorA.setPower(0);
//        }

        if (jeb.limitBag.isPressed()) {
            jeb.bagMotor.setPower(0);
        }
        if (!jeb.limitBag.isPressed() || gamepad2.left_stick_y < 0) {
            jeb.bagMotor.setPower(-gamepad2.left_stick_y * Consts.TICKS_PER_POWER);
        }

//        if ((jeb.armMotorB.getCurrentPosition() < Consts.MAX_ARM_B_POS || gamepad2.right_stick_y > 0) &&
//                (jeb.armMotorB.getCurrentPosition() > Consts.MIN_ARM_B_POS || gamepad2.right_stick_y < 0)) {
//            jeb.armMotorB.setPower(gamepad2.right_stick_y);
//        }
//        else {
//            jeb.armMotorB.setPower(0);
//        }
        if (jeb.limitSlide.isPressed() || jeb.slideMotor.getCurrentPosition() >= Consts.MAX_ARM_BAG_POS) {
            jeb.slideMotor.setPower(0);
        }
        if (!jeb.limitSlide.isPressed() || gamepad2.left_stick_y < 0 || (jeb.slideMotor.getCurrentPosition() >= Consts.MAX_ARM_BAG_POS && gamepad2.left_stick_y > 0)) {
            jeb.slideMotor.setPower(-gamepad2.right_stick_y * Consts.TICKS_PER_POWER);
        }

        if (gamepad2.dpad_down) jeb.slideMotor.setTargetPosition(Consts.LOW_ARM_POS);
        else if (gamepad2.dpad_left) jeb.slideMotor.setTargetPosition(Consts.LOW_ARM_POS);
        else if (gamepad2.dpad_right) jeb.slideMotor.setTargetPosition(Consts.MID_ARM_POS);
//        else if (gamepad2.dpad_up) jeb.slideMotor.setTargetPosition(Consts.HIGH_ARM_POS);

        // claw
        rightBumperToggle.update(gamepad2.right_bumper);
        telemetry.addData("claw in", rightBumperToggle.buttonState);
        leftBumperToggle.update(gamepad2.left_bumper);
        telemetry.addData("claw on", leftBumperToggle.buttonState);

        if (leftBumperToggle.buttonState) {
            jeb.clawServoA.setPower(rightBumperToggle.buttonState ? -Consts.DEFAULT_ARM_POWER : Consts.DEFAULT_ARM_POWER);
            jeb.clawServoB.setPower(rightBumperToggle.buttonState ? Consts.DEFAULT_ARM_POWER : -Consts.DEFAULT_ARM_POWER);
        }
        else {
            jeb.clawServoA.setPower(0);
            jeb.clawServoB.setPower(0);
        }

    }



}
