package org.firstinspires.ftc.potencode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.checkerframework.checker.signedness.qual.Constant;
import org.firstinspires.ftc.robotcore.external.Const;

@TeleOp(name="JebbyOp")
public class JebbyOp extends OpMode {
    private Jeb jeb;

    private ButtonState clawClosed = new ButtonState();
    private int targetArmPosition;

    private ButtonState FODOn = new ButtonState();
    
    @Override
    public void init() {
        jeb = new Jeb(hardwareMap, telemetry);
        jeb.initiate();
    }

    @Override
    public void loop() {

        /// drive

        FODOn.update(gamepad1.back);
        telemetry.addData("FOD:", FODOn);
        if (FODOn.buttonState) {
            jeb.drivePowerFOD(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x);
        } else {
            jeb.drivePower(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x);
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

        if (gamepad2.left_stick_y != 0) { // if arm is moving (todo add limit switch)
            targetArmPosition = 0;
            jeb.setArmPower(gamepad2.left_stick_x);
        }
        else { // if arm is not moving
            // do not set a new position if it's already being held at the target
            if (targetArmPosition != jeb.armMotorA.getCurrentPosition()) {
                jeb.holdArm(targetArmPosition);
            }
        }

        /// claw

        clawClosed.update(gamepad2.right_bumper);
        jeb.clawServo.setPosition(clawClosed.buttonState ? Constants.CLAW_MAX : Constants.CLAW_MIN);

    }



}
