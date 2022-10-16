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

    private WebcamName[] webcams;
    private SwitchableCamera switchableCamera;

    private boolean oldLeftBumper;
    private boolean oldRightBumper;

    @Override
    public void init() {
        jeb = new Jeb(hardwareMap, telemetry);

        webcams[0] = hardwareMap.get(WebcamName.class, "cam_front");
        webcams[1] = hardwareMap.get(WebcamName.class, "cam_back");
        vulo = jeb.initVuforia(webcams);

        switchableCamera = (SwitchableCamera) vulo.getCamera();
        switchableCamera.setActiveCamera(webcams[0]);

        tfod = jeb.initTfod(vulo);

        // Use loadModelFromAsset() if the TF Model is built in as an asset by Android Studio
        // Use loadModelFromFile() if you have downloaded a custom team model to the Robot Controller's FLASH.
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, TFOD_LABELS);
        // tfod.loadModelFromFile(TFOD_MODEL_FILE, LABELS);

        if (tfod != null) {
            tfod.activate();

            // The TensorFlow software will scale the input images from the camera to a lower resolution.
            // This can result in lower detection accuracy at longer distances (> 55cm or 22").
            // If your target is at distance greater than 50 cm (20") you can increase the magnification value
            // to artificially zoom in to the center of image.  For best results, the "aspectRatio" argument
            // should be set to the value of the images used to create the TensorFlow Object Detection model
            // (typically 16/9).
            tfod.setZoom(1.0, 16.0/9.0);
        }
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
            switchableCamera.setActiveCamera(webcams[0]);
            telemetry.addLine("now using front cam");
        } else if (newRightBumper && !oldRightBumper) {
            switchableCamera.setActiveCamera(webcams[1]);
            telemetry.addLine("now using back cam");
        }
        oldLeftBumper = newLeftBumper;
        oldRightBumper = newRightBumper;

        if (switchableCamera.getActiveCamera().equals(webcams[0])) {
            telemetry.addData("activeCamera", "Webcam 1");
            telemetry.addData("Press RightBumper", "to switch to Webcam 2");
        } else {
            telemetry.addData("activeCamera", "Webcam 2");
            telemetry.addData("Press LeftBumper", "to switch to Webcam 1");
        }
    }
}
