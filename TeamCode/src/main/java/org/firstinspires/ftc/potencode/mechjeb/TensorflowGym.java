package org.firstinspires.ftc.potencode.mechjeb;

import android.widget.Button;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.potencode.Jeb;
import org.firstinspires.ftc.potencode.utils.ButtonState;
import org.firstinspires.ftc.robotcore.external.hardware.camera.SwitchableCamera;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

@TeleOp(name="Tensorflow Gym")
public class TensorflowGym extends OpMode {
    public static final String TFOD_MODEL_ASSET = "mechjeb.tflite";
    public static final String[] TFOD_LABELS = new String[] { "drax", "spring", "ryan" };

    private Jeb jeb;

    private VuforiaLocalizer vulo;
    private TFObjectDetector tfod;

    private WebcamName cam_front;
    private WebcamName cam_back;
    private SwitchableCamera switchableCamera;

    private boolean oldLeftBumper;
    private boolean oldRightBumper;

    @Override
    public void init() {
        jeb = new Jeb(hardwareMap, telemetry);

        cam_front = hardwareMap.get(WebcamName.class, "cam_front");
        cam_back = hardwareMap.get(WebcamName.class, "cam_back");
        vulo = jeb.initVuforia(cam_front, cam_back);
        switchableCamera = (SwitchableCamera) vulo.getCamera();
        switchableCamera.setActiveCamera(cam_front);

        tfod = jeb.initTfod(vulo);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, TFOD_LABELS);

        telemetry.addData("Status", "starting tfod...");

        tfod.activate();
        tfod.setZoom(1.0, 16.0/9.0);

        telemetry.addData("Status", "prepared to annihilate");
    }

    @Override
    public void loop() {
        if (tfod != null) {
            doCameraSwitching();
            List<Recognition> recognitions = tfod.getRecognitions();
            telemetry.addData("# Objects Detected", recognitions.size());
            // step through the list of recognitions and display image size and position
            // Note: "Image number" refers to the randomized image orientation/number
            for (Recognition recognition : recognitions) {
                double col = (recognition.getLeft() + recognition.getRight()) / 2 ;
                double row = (recognition.getTop()  + recognition.getBottom()) / 2 ;
                double width  = Math.abs(recognition.getRight() - recognition.getLeft()) ;
                double height = Math.abs(recognition.getTop()  - recognition.getBottom()) ;

                telemetry.addData(""," ");
                telemetry.addData("Image", "%s (%.0f %% Conf.)", recognition.getLabel(), recognition.getConfidence() * 100 );
                telemetry.addData("- Position (Row/Col)","%.0f / %.0f", row, col);
                telemetry.addData("- Size (Width/Height)","%.0f / %.0f", width, height);
            }
            telemetry.update();
        }
    }

    private void doCameraSwitching() {
        // If the left bumper is pressed, use Webcam 1.
        // If the right bumper is pressed, use Webcam 2.
        boolean newLeftBumper = gamepad1.left_bumper;
        boolean newRightBumper = gamepad1.right_bumper;
        if (newLeftBumper && !oldLeftBumper) {
            switchableCamera.setActiveCamera(cam_front);
            telemetry.addLine("now using front cam");
        } else if (newRightBumper && !oldRightBumper) {
            switchableCamera.setActiveCamera(cam_back);
            telemetry.addLine("now using back cam");
        }
        oldLeftBumper = newLeftBumper;
        oldRightBumper = newRightBumper;

        if (switchableCamera.getActiveCamera().equals(cam_front)) {
            telemetry.addData("activeCamera", "Webcam front");
            telemetry.addData("Press RightBumper", "to switch to Webcam back");
        } else {
            telemetry.addData("activeCamera", "Webcam back");
            telemetry.addData("Press LeftBumper", "to switch to Webcam front");
        }
    }
}
