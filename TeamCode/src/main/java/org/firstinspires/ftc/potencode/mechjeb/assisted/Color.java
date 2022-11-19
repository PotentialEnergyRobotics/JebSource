package org.firstinspires.ftc.potencode.mechjeb.assisted;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.potencode.Jeb;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;
import org.openftc.easyopencv.OpenCvWebcam;

@Autonomous(name="ColorCV")
public class Color extends OpMode {
    private Jeb jeb;

    private OpenCvWebcam webcam;

    @Override
    public void init() {
        jeb = new Jeb(hardwareMap, telemetry);
        jeb.initiate();

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "cam"), cameraMonitorViewId);
        webcam.setPipeline(new ColorPipeline());

        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                webcam.startStreaming(320,240, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {}
        });
    }

    @Override
    public void loop() {

    }

    class ColorPipeline extends OpenCvPipeline {
        Mat coneRegion;
        Mat YCrCb = new Mat();
        Mat Cb = new Mat();

        int avg;

        @Override
        public Mat processFrame(Mat input) {
            Imgproc.cvtColor(input, YCrCb, Imgproc.COLOR_RGB2YCrCb);
            Core.extractChannel(YCrCb, Cb, 2);

            // this should be a const
            coneRegion = Cb.submat(new Rect(new Point(0 , 0), new Point(10, 10)));

            avg = (int) Core.mean(coneRegion).val[0];

//            if (avg == )

            return input;
        }
    }
}
