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

    private int targetArmPosition;

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
        if (gamepad1.x) {
            if (Math.abs(driveX) > Math.abs(driveY)) {
                driveY = 0;
            } else if (Math.abs(driveY) > Math.abs(driveX)) {
                driveX = 0;
            }
        }
        if (backButtonToggle.buttonState) {
            jeb.FOD(driveX, driveY);
        }
        jeb.driveVelocity(driveX, driveY, driveTurn);

        /// arm

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
            targetArmPosition = jeb.armMotor.getCurrentPosition();
        }

        armSpeedModifier = Consts.DEFAULT_ARM_POWER + gamepad2.left_trigger * (1 - Consts.DEFAULT_ARM_POWER) - gamepad2.right_trigger * Consts.DEFAULT_ARM_POWER;
        armSpeedModifier = Range.clip(armSpeedModifier, Consts.MIN_ARM_POWER, 1);
        telemetry.addData("Arm speed modifier", armSpeedModifier);

        if (gamepad2.left_stick_y != 0) { // if arm is moving (todo add limit switch)
            targetArmPosition = 0;
            jeb.setArmPower(-gamepad2.left_stick_y * armSpeedModifier);
        }
        else { // if arm is not moving
            // do not set a new position if it's already being held at the target
            if (targetArmPosition != jeb.armMotor.getCurrentPosition()) {
                jeb.holdArm(targetArmPosition);
            }
        }

        /// claw

        rightBumperToggle.update(gamepad2.right_bumper);
        telemetry.addData("Claw closed:", rightBumperToggle.buttonState);
        jeb.clawServo.setPosition(rightBumperToggle.buttonState ? Consts.CLAW_MIN_POS : Consts.CLAW_MAX_POS);
    }



}
