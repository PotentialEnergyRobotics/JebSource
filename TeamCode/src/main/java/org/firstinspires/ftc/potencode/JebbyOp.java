package org.firstinspires.ftc.potencode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.checkerframework.checker.signedness.qual.Constant;
import org.firstinspires.ftc.potencode.utils.ButtonState;
import org.firstinspires.ftc.potencode.utils.Consts;
import org.firstinspires.ftc.robotcore.external.Const;

@TeleOp(name="JebbyOp")
public class JebbyOp extends OpMode {
    private Jeb jeb;

    private ButtonState rightBumperToggle;
    private int targetArmPosition;

    private double armSpeedModifier;
    private double driveSpeedModifier;

    public static double XDrivePower;
    public static double YDrivePower;
    public static double TurnDrivePower;

    private int OCTarget;
    private boolean OCOn = false;
    private boolean dpadDown;

    private ButtonState FODOn = new ButtonState();
    
    @Override
    public void init() {
        rightBumperToggle = new ButtonState();

        jeb = new Jeb(hardwareMap, telemetry);
        jeb.initiate();
    }

    @Override
    public void loop() {

        /// drive speed
        if (gamepad1.right_trigger > 0) {
            driveSpeedModifier = 1;
        } else if (gamepad1.left_trigger > 0) {
            driveSpeedModifier = 0.2;
        } else {
            driveSpeedModifier = 0.5;
        }
        telemetry.addData("DriveSpeed", armSpeedModifier);

        /// drive

        FODOn.update(gamepad1.back);
        jeb.updateAngle();
        telemetry.addData("FOD:", FODOn.buttonState);
        telemetry.addData("Angle:", jeb.angle_d);
        XDrivePower = gamepad1.left_stick_x * driveSpeedModifier;
        YDrivePower = gamepad1.left_stick_y * driveSpeedModifier;
        TurnDrivePower = gamepad1.right_stick_x * driveSpeedModifier;

        /// Work in progress - Orientation Correction
        /*
        if (gamepad1.dpad_up || gamepad1.dpad_down || gamepad1.dpad_left || gamepad1.dpad_right) {
            OCOn = !OCOn;
            dpadDown = true;
            if (gamepad1.dpad_up) {
                OCTarget = 0;
            }
            if (gamepad1.dpad_down) {
                OCTarget = 180;
            }
            if (gamepad1.dpad_left) {
                OCTarget = -90;
            }
            if (gamepad1.dpad_right) {
                OCTarget = 90;
            }
        } else if (!gamepad1.dpad_up && !gamepad1.dpad_down && !gamepad1.dpad_left && !gamepad1.dpad_right && dpadDown) {
            dpadDown = false;
        } */

        if (FODOn.buttonState) {
            jeb.FOD(XDrivePower,YDrivePower);
        }
        jeb.driveVelocity(XDrivePower, YDrivePower, TurnDrivePower);
        /// arm
        /*
        // todo
        if (gamepad2.dpad_down) {
//            targetArmPosition = Consts.ARM_LEVELS[0];
        } else if (gamepad2.dpad_left) {
//            targetArmPosition = Consts.ARM_LEVELS[1];
        } else if (gamepad2.dpad_right) {
//            targetArmPosition = Consts.ARM_LEVELS[2];
        } else if (gamepad2.dpad_up) {
//            targetArmPosition = Consts.ARM_LEVELS[3];
        } else if (gamepad2.left_stick_y == 0 && targetArmPosition == 0) { // if arm is not moving and arm just moved hold arm at position
            targetArmPosition = (jeb.armMotorA.getCurrentPosition() + jeb.armMotorB.getCurrentPosition()) / 2;
        }
        if (gamepad2.right_trigger > 0) {
            armSpeedModifier = 1;
        } else if (gamepad2.left_trigger > 0) {
            armSpeedModifier = 0.2;
        } else {
            armSpeedModifier = 0.5;
        }
        telemetry.addData("ArmSpeed", armSpeedModifier);
        if (gamepad2.left_stick_y != 0) { // if arm is moving (todo add limit switch)
            targetArmPosition = 0;
            jeb.setArmPower(gamepad2.left_stick_y * armSpeedModifier);
        }
        else { // if arm is not moving
            // do not set a new position if it's already being held at the target
            if (targetArmPosition != jeb.armMotorA.getCurrentPosition()) {
                jeb.holdArm(targetArmPosition);
            }
        }

        /// claw

        rightBumperToggle.update(gamepad2.right_bumper);
        telemetry.addData("ClawOpened",rightBumperToggle.buttonState);
        jeb.clawServo.setPosition(rightBumperToggle.buttonState ? Consts.CLAW_MAX_POS : Consts.CLAW_MIN_POS);
        */
    }



}
