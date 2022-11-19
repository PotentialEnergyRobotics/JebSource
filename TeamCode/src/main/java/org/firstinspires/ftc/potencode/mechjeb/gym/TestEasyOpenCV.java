/*
 * Copyright (c) 2019 OpenFTC Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.firstinspires.ftc.potencode.mechjeb.gym;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect2d;
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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/*
 * This sample shows use of EOCV's pipeline recording API to start/stop recording
 * when the viewport is tapped. The statistics box will turn red to indicate that
 * recording is active.
 */
@TeleOp(name = "Test EZ CV")
@Disabled
public class TestEasyOpenCV extends LinearOpMode
{
    OpenCvCamera camera;

    @Override
    public void runOpMode()
    {
        /**
         * NOTE: Many comments have been omitted from this sample for the
         * sake of conciseness. If you're just starting out with EasyOpenCV,
         * you should take a look at {@link InternalCamera1Example} or its
         * webcam counterpart, {@link WebcamExample} first.
         */

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "cam"), cameraMonitorViewId);

        camera.setPipeline(new SamplePipeline());
//        camera.setViewportRenderingPolicy(OpenCvCamera.ViewportRenderingPolicy.OPTIMIZE_VIEW);
//        camera.setViewportRenderer(OpenCvCamera.ViewportRenderer.GPU_ACCELERATED);
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                camera.startStreaming(1280, 720, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode)
            {
                /*
                 * This will be called if the camera could not be opened
                 */
            }
        });

        telemetry.addLine("Waiting for start");
        telemetry.update();

        /*
         * Wait for the user to press start on the Driver Station
         */
        waitForStart();

        while (opModeIsActive())
        {
            /*
             * Send some stats to the telemetry
             */
            telemetry.addData("Frame Count", camera.getFrameCount());
            telemetry.addData("FPS", String.format("%.2f", camera.getFps()));
            telemetry.addData("Total frame time ms", camera.getTotalFrameTimeMs());
            telemetry.addData("Pipeline time ms", camera.getPipelineTimeMs());
            telemetry.addData("Overhead time ms", camera.getOverheadTimeMs());
            telemetry.addData("Theoretical max FPS", camera.getCurrentPipelineMaxFps());
            telemetry.update();

            /*
             * For the purposes of this sample, throttle ourselves to 10Hz loop to avoid burning
             * excess CPU cycles for no reason. (By default, telemetry is only sent to the DS at 4Hz
             * anyway). Of course in a real OpMode you will likely not want to do this.
             */
            sleep(100);
        }
    }


    class SamplePipeline extends OpenCvPipeline
    {
        private Net jebNet;
        private List<String> outputLayers = new ArrayList<String>();

        private List<String> labels;
        private ArrayList<Scalar> colors;

        // TODO: Consts!!!!
        final int IN_WIDTH = 416;
        final int IN_HEIGHT = 416;
        final float WH_RATIO = (float)IN_WIDTH / IN_HEIGHT;
        final double IN_SCALE_FACTOR = 0.007843;
        final double MEAN_VAL = 127.5;
        final double THRESHOLD = 0.2;

        @Override
        public void init(Mat firstFrame) {
            // load jeb net
            jebNet = Dnn.readNetFromDarknet("/sdcard/FIRST/opencv/yolov7.yaml","/sdcard/FIRST/opencv/yolov7.weights");
            List<String> layerNames = jebNet.getLayerNames();
            for (Integer i : jebNet.getUnconnectedOutLayers().toList()) {
                outputLayers.add(layerNames.get(i - 1));
            }

            // load labels
            Scanner scan = null;
            try {
                scan = new Scanner(new FileReader("/sdcard/FIRST/opencv/labels.txt"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            labels = new ArrayList<String>();
            while(scan.hasNextLine()) {
                labels.add(scan.nextLine());
            }

            // create random colors
            Random random = new Random();
            colors = new ArrayList<Scalar>();
            for (int i= 0; i < labels.size(); i++) {
                colors.add(new Scalar( new double[] {random.nextInt(255), random.nextInt(255), random.nextInt(255)}));
            }
        }

        @Override
        public Mat processFrame(Mat input)
        {
            Mat output = forwardImageOverNetwork(input, jebNet, outputLayers);

            return output;
        }

        private Mat forwardImageOverNetwork(Mat img, Net net, List<String> outputLayers) {
            // --We need to prepare some data structure  in order to store the data returned by the network  (ie, after Net.forward() call))
            // So, Initialize our lists of detected bounding boxes, confidences, and  class IDs, respectively
            // This is what this method will return:
            HashMap<String, List> result = new HashMap<String, List>();
            result.put("boxes", new ArrayList<Rect2d>());
            result.put("confidences", new ArrayList<Float>());
            result.put("class_ids", new ArrayList<Integer>());

            // Get a new frame
            Imgproc.cvtColor(img, img, Imgproc.COLOR_RGBA2RGB);
            // Forward image through network.
            Mat blob = Dnn.blobFromImage(img, IN_SCALE_FACTOR,
                    new Size(IN_WIDTH, IN_HEIGHT),
                    new Scalar(MEAN_VAL, MEAN_VAL, MEAN_VAL), /*swapRB*/false, /*crop*/false);
            net.setInput(blob);
            Mat detections = net.forward();

            int cols = img.cols();
            int rows = img.rows();

            detections = detections.reshape(1, (int)detections.total());
            for (int i = 0; i < detections.rows(); ++i) {
                double confidence = detections.get(i, 2)[0];
                if (confidence > THRESHOLD) {
                    int classId = (int)detections.get(i, 1)[0];
                    int left   = (int)(detections.get(i, 3)[0] * cols);
                    int top    = (int)(detections.get(i, 4)[0] * rows);
                    int right  = (int)(detections.get(i, 5)[0] * cols);
                    int bottom = (int)(detections.get(i, 6)[0] * rows);
                    // Draw rectangle around detected object.
                    Imgproc.rectangle(img, new Point(left, top), new Point(right, bottom),
                            new Scalar(0, 255, 0));
                    String label = labels.get(classId) + ": " + confidence;
                    int[] baseLine = new int[1];
                    Size labelSize = Imgproc.getTextSize(label, Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, 1, baseLine);
                    // Draw background for label.
                    Imgproc.rectangle(img, new Point(left, top - labelSize.height),
                            new Point(left + labelSize.width, top + baseLine[0]),
                            new Scalar(255, 255, 255), Imgproc.FILLED);
                    // Write class name and confidence.
                    Imgproc.putText(img, label, new Point(left, top),
                            Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 0, 0));
                }
            }

            return img;
        }
    }
}