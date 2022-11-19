package org.firstinspires.ftc.potencode.mechjeb.gym;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.potencode.Jeb;
import org.firstinspires.ftc.robotcore.external.hardware.camera.SwitchableCamera;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

@Autonomous(name = "Test Cone Face")
@Disabled
public class TestConeFace extends OpMode {
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

        telemetry.addData("Status", "initialized");
    }

    @Override
    public void loop() {
        List<Recognition> recognitions = tfod.getRecognitions();

    }
}
