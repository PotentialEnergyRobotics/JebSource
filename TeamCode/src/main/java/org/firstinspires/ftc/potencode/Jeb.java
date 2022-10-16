package org.firstinspires.ftc.potencode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.potencode.utils.Consts;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

public class Jeb {
    private HardwareMap hardwareMap;
    private Telemetry telemetry;

    ///

    public BNO055IMU IMU;

    public DcMotorEx frontMotor;
    public DcMotorEx rightMotor;
    public DcMotorEx leftMotor;
    public DcMotorEx backMotor;

    public DcMotorEx armMotorA;
    public DcMotorEx armMotorB;

    public Servo clawServo;

    ///

    private float angle;

    private boolean clawOpen;

    public Jeb(HardwareMap hardwareMap, Telemetry telemetry) {
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
    }

    public void initiate() {
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

        parameters.mode = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled = false;

        IMU = hardwareMap.get(BNO055IMU.class, "IMU");
        IMU.initialize(parameters);

        ///

        frontMotor = hardwareMap.get(DcMotorEx.class, "front");
        rightMotor = hardwareMap.get(DcMotorEx.class, "right");
        leftMotor = hardwareMap.get(DcMotorEx.class, "left");
        backMotor = hardwareMap.get(DcMotorEx.class, "back");

        armMotorA = hardwareMap.get(DcMotorEx.class, "armA"); // motor 0
        armMotorB = hardwareMap.get(DcMotorEx.class, "armB"); // motor 1

        clawServo = hardwareMap.get(Servo.class, "claw"); // servo 0

        frontMotor.setDirection(DcMotorEx.Direction.FORWARD);
        backMotor.setDirection(DcMotorEx.Direction.REVERSE);
        leftMotor.setDirection(DcMotorEx.Direction.FORWARD);
        rightMotor.setDirection(DcMotorEx.Direction.REVERSE);
    }

    public void updateAngle() {
        angle = IMU.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZXY, AngleUnit.DEGREES).thirdAngle;
    }

    public void rotateDegrees(double degrees) { // degrees not radians!!
        // todo
        return;
    }

    public void holdArm(int ticks) { // todo convert to degrees!!
        armMotorA.setTargetPosition(ticks);
        armMotorB.setTargetPosition(ticks);
        armMotorA.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        armMotorB.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        armMotorA.setVelocity(Consts.ARM_TPS);
        armMotorB.setVelocity(Consts.ARM_TPS);
    }

    public void setArmPower(double power) {
        armMotorA.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        armMotorB.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        armMotorA.setPower(power);
        armMotorB.setPower(power);
    }

    public void driveCentimeters(double distanceX, double distanceY, int velocity) { // cm, cm, m/s
        // todo
        return;
    }

    public void drivePowerFOD(double powerX, double powerY, double turnPower) {
        updateAngle();
        frontMotor.setPower(powerX * Math.sin(angle) + powerY * Math.cos(angle) + turnPower);
        leftMotor.setPower(powerX * Math.cos(angle) + powerY * Math.sin(angle) + turnPower);
        backMotor.setPower(powerX * Math.sin(angle) + powerY * Math.cos(angle) - turnPower);
        rightMotor.setPower(powerX * Math.cos(angle) + powerY * Math.sin(angle) - turnPower);
    }
    public void drivePower(double powerX, double powerY, double turnPower) {
        frontMotor.setPower(powerX + turnPower);
        leftMotor.setPower(powerY + turnPower);
        backMotor.setPower(powerX - turnPower);
        rightMotor.setPower(powerY - turnPower);
    }

    public VuforiaLocalizer initVuforia(CameraName ...webcams) {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = Consts.VUFORIA_KEY;

        // Indicate that we wish to be able to switch cameras.
        parameters.cameraName = ClassFactory.getInstance().getCameraManager().nameForSwitchableCamera(webcams);

        //  Instantiate the Vuforia engine
        return ClassFactory.getInstance().createVuforia(parameters);
    }

    public TFObjectDetector initTfod(VuforiaLocalizer vulo) {
        // same as vulo but for tfod
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.75f;
        tfodParameters.isModelTensorFlow2 = true;
        tfodParameters.inputSize = 600;
        return ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vulo);
    }
}
