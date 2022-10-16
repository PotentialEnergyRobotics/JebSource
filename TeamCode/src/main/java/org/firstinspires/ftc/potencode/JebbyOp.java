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
    private double moveSpeedModifier;

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

        moveSpeedModifier = Range.clip(0, 1, Consts.DEFAULT_ARM_SPEED + gamepad2.left_trigger / 2) +
                Range.clip(0, 1, Consts.DEFAULT_ARM_SPEED - gamepad2.right_trigger / 2);
        telemetry.addData("Move speed modifier", moveSpeedModifier);

        if (backButtonToggle.buttonState) {
            jeb.drivePowerFOD(gamepad1.left_stick_x * moveSpeedModifier, gamepad1.left_stick_y * moveSpeedModifier, gamepad1.right_stick_x * moveSpeedModifier);
        } else {
            jeb.drivePower(gamepad1.left_stick_x * moveSpeedModifier, gamepad1.left_stick_y * moveSpeedModifier, gamepad1.right_stick_x * moveSpeedModifier);
        }

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
            targetArmPosition = (jeb.armMotorA.getCurrentPosition() + jeb.armMotorB.getCurrentPosition()) / 2;
        }

        armSpeedModifier = Range.clip(0, 1, Consts.DEFAULT_ARM_SPEED + gamepad2.left_trigger / 2) +
            Range.clip(0, 1, Consts.DEFAULT_ARM_SPEED - gamepad2.right_trigger / 2);
        telemetry.addData("Arm speed modifier", moveSpeedModifier);

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
        jeb.clawServo.setPosition(rightBumperToggle.buttonState ? Consts.CLAW_MAX : Consts.CLAW_MIN);
    }



}
