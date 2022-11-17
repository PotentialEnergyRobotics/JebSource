package org.firstinspires.ftc.potencode.mechjeb;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.potencode.Jeb;
import org.firstinspires.ftc.robotcore.external.hardware.camera.SwitchableCamera;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

@TeleOp(name="Tensorflow Gym")
public class TensorflowGym extends OpMode {
    public static final String TFOD_MODEL_ASSET = "mechjeb.tflite";
    private static final String TFOD_MODEL_FILE  = "/sdcard/FIRST/tflitemodels/mechjeb.tflite";
    public static final String[] TFOD_LABELS = new String[] { "drax", "spring", "ryan" };

    private Jeb jeb;

    private VuforiaLocalizer vulo;
    private TFObjectDetector tfod;

    private WebcamName cam;

    @Override
    public void init() {
        jeb = new Jeb(hardwareMap, telemetry);

        cam = hardwareMap.get(WebcamName.class, "cam");
        vulo = jeb.initVuforia(cam);

        tfod = jeb.initTfod(vulo);
//        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, TFOD_LABELS);
        tfod.loadModelFromFile(TFOD_MODEL_FILE, TFOD_LABELS);

        telemetry.addData("Status", "starting tfod...");

        tfod.activate();
//        tfod.setZoom(1.0, 16.0/9.0);

        telemetry.addData("Status", "prepared to annihilate");
    }

    @Override
    public void loop() {
        if (tfod != null) {
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
}
