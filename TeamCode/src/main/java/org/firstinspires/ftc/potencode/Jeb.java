package org.firstinspires.ftc.potencode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;

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

    public DcMotorEx bag;
    public DcMotorEx slide;
    public CRServo clawA;
    public CRServo clawB;

    public TouchSensor limit;

    private double angle_r;
    private double angle_d;
    private double angle_0 = 0;
    private double current_angle_r;

    private double drive_direction;

    public Jeb(HardwareMap hardwareMap, Telemetry telemetry) {
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
    }

    public void awake() {
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

        parameters.mode = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled = false;

        IMU = hardwareMap.get(BNO055IMU.class, "IMU");
        IMU.initialize(parameters);

        frontMotor = hardwareMap.get(DcMotorEx.class, "front");
        rightMotor = hardwareMap.get(DcMotorEx.class, "right");
        leftMotor = hardwareMap.get(DcMotorEx.class, "left");
        backMotor = hardwareMap.get(DcMotorEx.class, "back");

        bag = hardwareMap.get(DcMotorEx.class, "bag");
        slide = hardwareMap.get(DcMotorEx.class, "slide");
        clawA = hardwareMap.get(CRServo.class, "claw A");
        clawB = hardwareMap.get(CRServo.class, "claw B");

        limit = hardwareMap.get(TouchSensor.class, "limit bag");
        limit = hardwareMap.get(TouchSensor.class, "limit slide");

        frontMotor.setDirection(DcMotorEx.Direction.FORWARD);
        backMotor.setDirection(DcMotorEx.Direction.REVERSE);
        leftMotor.setDirection(DcMotorEx.Direction.REVERSE);
        rightMotor.setDirection(DcMotorEx.Direction.FORWARD);
    }

    public void zeros() {

    }

    public void updateAngle() {
        angle_r = IMU.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZXY, AngleUnit.RADIANS).firstAngle;
        angle_d = IMU.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZXY, AngleUnit.DEGREES).firstAngle;
        current_angle_r = angle_r - angle_0;
        telemetry.addData("current radians:", current_angle_r);
    }

    public void resetAngle() {
        angle_0 = angle_r;
        telemetry.addData("angle 0:", angle_0);
    }

    public void rotateDegrees(double degrees) { // degrees not radians!!
        // todo
        return;
    }

    private void trySwitchRunPosition(int vel) {
        // copy-🍝 pain
        if (leftMotor.getMode() != DcMotor.RunMode.RUN_TO_POSITION) { // one should mean all
            leftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            frontMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            backMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }

        leftMotor.setVelocity(vel);
        rightMotor.setVelocity(vel);
        frontMotor.setVelocity(vel);
        backMotor.setVelocity(vel);
    }

    public void resetDriveEncoders() {
        // pasta anyone?
        if (leftMotor.getMode() != DcMotor.RunMode.STOP_AND_RESET_ENCODER) {
            leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            frontMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            backMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }
        //leftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        //rightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        //frontMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        //backMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void driveCentimeters(double distanceX, double distanceY, int velocity) { // cm, cm, cm/s
        int xTicks = (int)(distanceX * Consts.TICKS_PER_CM);
        int yTicks = (int)(distanceY * Consts.TICKS_PER_CM);
        telemetry.addData("driving x ticks", xTicks);
        telemetry.addData("driving y ticks", yTicks);
        leftMotor.setTargetPosition(yTicks);
        rightMotor.setTargetPosition(yTicks);
        frontMotor.setTargetPosition(xTicks);
        backMotor.setTargetPosition(xTicks);
        trySwitchRunPosition(velocity); // todo actually use cm/s rather than arbitrary
        resetDriveEncoders();
    }

    public void driveFOD(double powerX, double powerY, double turnPower) {
        updateAngle();
        double powerXFOD = powerX * Math.cos(current_angle_r) - powerY * Math.sin(current_angle_r);
        double powerYFOD = powerX * Math.sin(current_angle_r) + powerY * Math.cos(current_angle_r);
        frontMotor.setVelocity((powerXFOD + turnPower) * Consts.TICKS_PER_POWER);
        leftMotor.setVelocity((powerYFOD - turnPower) * Consts.TICKS_PER_POWER);
        backMotor.setVelocity((powerXFOD - turnPower) * Consts.TICKS_PER_POWER);
        rightMotor.setVelocity((powerYFOD + turnPower) * Consts.TICKS_PER_POWER);
    }

    public void drivePower(double powerX, double powerY, double turnPower) {
        frontMotor.setPower(powerX + turnPower);
        leftMotor.setPower(powerY - turnPower);
        backMotor.setPower(powerX - turnPower);
        rightMotor.setPower(powerY + turnPower);
    }

    public void driveVelocity(double powerX, double powerY, double turnPower) {
        frontMotor.setVelocity((powerX + turnPower) * Consts.TICKS_PER_POWER);
        leftMotor.setVelocity((powerY - turnPower) * Consts.TICKS_PER_POWER);
        backMotor.setVelocity((powerX - turnPower) * Consts.TICKS_PER_POWER);
        rightMotor.setVelocity((powerY + turnPower) * Consts.TICKS_PER_POWER);
    }

    public void gyroDrive(double powerX, double powerY, double orientation) {
        drive_direction = angle_d - orientation;
        frontMotor.setVelocity((powerX + (drive_direction * Consts.POWER_PER_P)) * Consts.TICKS_PER_POWER);
        leftMotor.setVelocity((powerY - (drive_direction * Consts.POWER_PER_P)) * Consts.TICKS_PER_POWER);
        backMotor.setVelocity((powerX - (drive_direction * Consts.POWER_PER_P)) * Consts.TICKS_PER_POWER);
        rightMotor.setVelocity((powerY + (drive_direction * Consts.POWER_PER_P)) * Consts.TICKS_PER_POWER);
    }

    public VuforiaLocalizer initVuforia(CameraName ...webcams) {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
        parameters.vuforiaLicenseKey = Consts.VUFORIA_KEY;
        parameters.cameraName = ClassFactory.getInstance().getCameraManager().nameForSwitchableCamera(webcams); // camera switching

        telemetry.addData("jeb", "vulo init complete");
        return ClassFactory.getInstance().createVuforia(parameters);
    }

    public TFObjectDetector initTfod(VuforiaLocalizer vulo) {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = Consts.MIN_RESULT_CONFIDENCE;
        tfodParameters.isModelTensorFlow2 = true;
        tfodParameters.inputSize = Consts.TFOD_INPUT_SIZE;

        telemetry.addData("jeb", "tfod init complete");
        return ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vulo);
    }
}
