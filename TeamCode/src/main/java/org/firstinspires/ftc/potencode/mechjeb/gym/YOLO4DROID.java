package org.firstinspires.ftc.potencode.mechjeb.gym;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;
import org.openftc.easyopencv.OpenCvPipeline;
import org.openftc.easyopencv.OpenCvWebcam;

@Autonomous(name = "YOLO 4 DROID")
@Disabled
public class YOLO4DROID extends OpMode {
    private OpenCvWebcam cam;
    private JebNetPipeline pipeline;

    @Override
    public void init() {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        cam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "cam"), cameraMonitorViewId);
        pipeline = new JebNetPipeline();
        cam.setPipeline(pipeline);

        cam.setViewportRenderingPolicy(OpenCvCamera.ViewportRenderingPolicy.OPTIMIZE_VIEW);

        cam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                cam.startStreaming(640,480, OpenCvCameraRotation.SIDEWAYS_LEFT);
            }

            @Override
            public void onError(int errorCode)
            {
                telemetry.addData("Error", errorCode);
            }
        });
    }

    @Override
    public void loop() {

    }


    public static class JebNetPipeline extends OpenCvPipeline {
        public Net jebNet;

        // replace with file
        private static final String[] classNames = {"background",
                "aeroplane", "bicycle", "bird", "boat",
                "bottle", "bus", "car", "cat", "chair",
                "cow", "diningtable", "dog", "horse",
                "motorbike", "person", "pottedplant",
                "sheep", "sofa", "train", "tvmonitor"};

        public void init() {
            jebNet = Dnn.readNetFromDarknet("/sdcard/FIRST/opencv/yolov3-tiny.cfg","/sdcard/FIRST/opencv/yolov3-tiny.weights");
        }

        @Override
        public Mat processFrame(Mat inputFrame) {
            Mat frame = inputFrame; // do transformations here
            Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGBA2RGB);

            // TODO: Make constants https://docs.opencv.org/4.x/d0/d6c/tutorial_dnn_android.html
            Mat blob = Dnn.blobFromImage(frame, 0.007843,
                    new Size(640, 640),
                    new Scalar(127.5, 127.5, 127.5), false, false);
            jebNet.setInput(blob);
            Mat detections = jebNet.forward();

            int cols = frame.cols();
            int rows = frame.rows();
            detections = detections.reshape(1, (int)detections.total() / 7);

            return null;
        }
    }
}