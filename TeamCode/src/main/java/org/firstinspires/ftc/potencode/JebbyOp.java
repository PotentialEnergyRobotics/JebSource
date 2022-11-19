package org.firstinspires.ftc.potencode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.potencode.utils.ButtonState;
import org.firstinspires.ftc.potencode.utils.Consts;

@TeleOp(name="JebbyOp")
public class JebbyOp extends OpMode {
    private Jeb jeb;

    private ButtonState backButtonToggle;
    private ButtonState rightBumperToggle;

    private int targetArmAngle; // Arm A
    private int targetArmHeight; // Arm B

    private double armSpeedModifier;
    private double driveSpeedModifier;

    public static double driveX;
    public static double driveY;
    public static double driveTurn;

    @Override
    public void init() {
        rightBumperToggle = new ButtonState();
        backButtonToggle = new ButtonState();

        jeb = new Jeb(hardwareMap, telemetry);
        jeb.initiate();
    }

    @Override
    public void loop() {

        /// drive

        backButtonToggle.update(gamepad1.back);
        telemetry.addData("FOD", backButtonToggle.buttonState);

        driveSpeedModifier = Consts.DEFAULT_DRIVE_POWER + gamepad1.left_trigger * (1 - Consts.DEFAULT_DRIVE_POWER) - gamepad1.right_trigger * Consts.DEFAULT_DRIVE_POWER;
        driveSpeedModifier = Range.clip(driveSpeedModifier,  Consts.MIN_DRIVE_POWER, 1);
        telemetry.addData("Move speed modifier", driveSpeedModifier);

        driveX = gamepad1.left_stick_x * driveSpeedModifier;
        driveY = gamepad1.left_stick_y * driveSpeedModifier;
        driveTurn = gamepad1.right_stick_x * driveSpeedModifier;
        if (gamepad1.y) {
            if (Math.abs(driveX) > Math.abs(driveY)) {
                driveY = 0;
            } else if (Math.abs(driveY) > Math.abs(driveX)) {
                driveX = 0;
            }
        }
        if (gamepad1.x) {
            jeb.resetAngle();
        }
        if (backButtonToggle.buttonState) {
            jeb.FOD(driveX, driveY);
        }
        jeb.driveVelocity(driveX, driveY, driveTurn);
        telemetry.addData("x:", gamepad1.x);
        /// arm
        // todo
        if (gamepad2.dpad_down) {
//            targetArmAngle = Consts.ARM_LEVELS[0];
        } else if (gamepad2.dpad_left) {
//            targetArmAngle = Consts.ARM_LEVELS[1];
        } else if (gamepad2.dpad_right) {
//            targetArmAngle = Consts.ARM_LEVELS[2];
        } else if (gamepad2.dpad_up) {
//            targetArmAngle = Consts.ARM_LEVELS[3];
        } else if (gamepad2.left_stick_y == 0 && targetArmAngle == 0) { // if arm is not moving and arm just moved hold arm at position
            //targetArmAngle = jeb.armMotorA.getCurrentPosition();
        }
        targetArmAngle = jeb.armMotorA.getCurrentPosition();
        targetArmHeight = jeb.armMotorB.getCurrentPosition();
        armSpeedModifier = Consts.DEFAULT_ARM_POWER + gamepad2.left_trigger * (1 - Consts.DEFAULT_ARM_POWER) - gamepad2.right_trigger * Consts.DEFAULT_ARM_POWER;
        armSpeedModifier = Range.clip(armSpeedModifier, Consts.MIN_ARM_POWER, 1);
        telemetry.addData("Arm speed modifier", armSpeedModifier);
        telemetry.addData("Arm A position", targetArmAngle);

        if (gamepad2.left_stick_y != 0 && targetArmAngle <= Consts.MAX_ARM_A_POS && targetArmAngle >= Consts.MIN_ARM_A_POS) { // if arm is moving (todo add limit switch)
            //targetArmAngle = 0;
            jeb.holdArmA((int) (jeb.armMotorA.getCurrentPosition() - gamepad2.left_stick_y));
            telemetry.addData("Target Arm Height:", targetArmAngle);
            telemetry.addData("Arm Angle Status:", "Moving Arm");
        }
        else { // if arm is not moving
            // do not set a new position if it's already being held at the target
            if (targetArmAngle > Consts.MAX_ARM_A_POS) {
                telemetry.addData("Arm Angle Status:", "Going Down");
                jeb.holdArmA(Consts.MAX_ARM_A_POS);
            } else if (targetArmAngle < Consts.MIN_ARM_A_POS) {
                jeb.holdArmA(Consts.MIN_ARM_A_POS);
                telemetry.addData("Arm Angle Status:", "Going Up");
            } else if (targetArmAngle != jeb.armMotorA.getCurrentPosition()) {
                jeb.holdArmA(targetArmAngle);
                telemetry.addData("Arm Angle Status:", "Holding Position");
            }
        }
        if (gamepad2.right_stick_y != 0 && targetArmHeight <= Consts.MAX_ARM_B_POS && targetArmAngle >= Consts.MIN_ARM_B_POS) { // if arm is moving (todo add limit switch)
            //targetArmAngle = 0;
            jeb.setArmPowerB(-gamepad2.right_stick_y * armSpeedModifier);
            telemetry.addData("Target Arm Height:", targetArmHeight);
            telemetry.addData("Arm Height Status:", "Moving Arm");
        }
        else { // if arm is not moving
            // do not set a new position if it's already being held at the target
            if (targetArmHeight > Consts.MAX_ARM_B_POS) {
                telemetry.addData("Arm Height Status:", "Going Down");
                jeb.holdArmB(Consts.MAX_ARM_B_POS);
            } else if (targetArmHeight < Consts.MIN_ARM_B_POS) {
                jeb.holdArmB(Consts.MIN_ARM_B_POS);
                telemetry.addData("Arm Height Status:", "Going Up");
            } else if (targetArmHeight != jeb.armMotorB.getCurrentPosition()) {
                jeb.holdArmB(targetArmHeight);
                telemetry.addData("Arm Height Status:", "Holding Position");
            }
        }
        /// claw

        rightBumperToggle.update(gamepad2.right_bumper);
        telemetry.addData("Claw closed:", rightBumperToggle.buttonState);
        //jeb.clawServo.setPosition(rightBumperToggle.buttonState ? Consts.CLAW_MIN_POS : Consts.CLAW_MAX_POS);
    }



}
