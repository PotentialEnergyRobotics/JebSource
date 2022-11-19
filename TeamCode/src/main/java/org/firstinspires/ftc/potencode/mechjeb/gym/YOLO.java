package org.firstinspires.ftc.potencode.mechjeb.gym;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect2d;
import org.opencv.core.Point;
import org.opencv.core.Rect2d;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;
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

// Epic person: https://www.linkedin.com/pulse/opencv-java-yolo-object-detection-images-svetozar-radoj%C4%8Din

@Autonomous(name = "YOLO")
@Disabled
public class YOLO extends OpMode {
    private OpenCvInternalCamera phoneCam;
    private JebNetPipeline pipeline;


    @Override
    public void init() {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        phoneCam = OpenCvCameraFactory.getInstance().createInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, cameraMonitorViewId);
        pipeline = new JebNetPipeline();
        phoneCam.setPipeline(pipeline);

        phoneCam.setViewportRenderingPolicy(OpenCvCamera.ViewportRenderingPolicy.OPTIMIZE_VIEW);

        phoneCam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                phoneCam.startStreaming(640,480, OpenCvCameraRotation.SIDEWAYS_LEFT);
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

    public static class JebNetPipeline extends OpenCvPipeline
    {
        private Net jebNet;
        private List<String> outputLayers = new ArrayList<String>();

        private List<String> labels;
        private ArrayList<Scalar> colors;

        @Override
        public void init(Mat firstFrame)
        {
            jebNet = Dnn.readNetFromDarknet("/sdcard/FIRST/opencv/yolov3-tiny.cfg","/sdcard/FIRST/opencv/yolov3-tiny.weights");
            List<String> layerNames = jebNet.getLayerNames();
            for (Integer i : jebNet.getUnconnectedOutLayers().toList()) {
                outputLayers.add(layerNames.get(i - 1));
            }

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

            Random random = new Random();
            colors = new ArrayList<Scalar>();
            for (int i= 0; i < labels.size(); i++) {
                colors.add(new Scalar( new double[] {random.nextInt(255), random.nextInt(255), random.nextInt(255)}));
            }
        }

        @Override
        public Mat processFrame(Mat input)
        {
            HashMap<String, List> result = forwardImageOverNetwork(input, jebNet, outputLayers);

            ArrayList<Rect2d> boxes = (ArrayList<Rect2d>)result.get("boxes");
            ArrayList<Float> confidences = (ArrayList<Float>) result.get("confidences");
            ArrayList<Integer> class_ids = (ArrayList<Integer>)result.get("class_ids");

            MatOfInt indices =  getBBoxIndicesFromNonMaximumSuppression(boxes,
                    confidences);
            //-- Finally, go over indices in order to draw bounding boxes on the image:
            Mat output =  drawBoxesOnTheImage(input, indices, boxes, labels, class_ids, colors);

            return output;
        }

        private HashMap<String, List> forwardImageOverNetwork(Mat img, Net net, List<String> outputLayers) {
            // --We need to prepare some data structure  in order to store the data returned by the network  (ie, after Net.forward() call))
            // So, Initialize our lists of detected bounding boxes, confidences, and  class IDs, respectively
            // This is what this method will return:
            HashMap<String, List> result = new HashMap<String, List>();
            result.put("boxes", new ArrayList<Rect2d>());
            result.put("confidences", new ArrayList<Float>());
            result.put("class_ids", new ArrayList<Integer>());

            // -- The input image to a neural network needs to be in a certain format called a blob.
            //  In this process, it scales the image pixel values to a target range of 0 to 1 using a scale factor of 1/255.
            // It also resizes the image to the given size of (416, 416) without cropping
            // Construct a blob from the input image and then perform a forward  pass of the YOLO object detector,
            // giving us our bounding boxes and  associated probabilities:

            // TODO: Define this in Consts
            Mat blob_from_image = Dnn.blobFromImage(img, 1 / 255.0, new Size(640, 640), // Here we supply the spatial size that the Convolutional Neural Network expects.
                    new Scalar(new double[]{0.0, 0.0, 0.0}), true, false);
            blob_from_image.convertTo(blob_from_image, CvType.CV_32F, 1.0/255, -0.5);
            blob_from_image = blob_from_image.reshape(1, new int[]{1, blob_from_image.cols(), blob_from_image.cols(), 3});// new int[]{1,3,640,640});
            net.setInput(blob_from_image);

            // -- the output from network's forward() method will contain a List of OpenCV Mat object, so lets prepare one
            List<Mat> outputs = new ArrayList<Mat>();

            // -- Finally, let pass forward throught network. The main work is done here:
            net.forward(outputs, outputLayers);

            // --Each output of the network outs (ie, each row of the Mat from 'outputs') is represented by a vector of the number
            // of classes + 5 elements.  The first 4 elements represent center_x, center_y, width and height.
            // The fifth element represents the confidence that the bounding box encloses the object.
            // The remaining elements are the confidence levels (ie object types) associated with each class.
            // The box is assigned to the category corresponding to the highest score of the box:

            for(Mat output : outputs) {
                //  loop over each of the detections. Each row is a candidate detection,
                System.out.println("Output.rows(): " + output.rows() + ", Output.cols(): " + output.cols());
                for (int i = 0; i < output.rows(); i++) {
                    Mat row = output.row(i);
                    List<Float> detect = new MatOfFloat(row).toList();
                    List<Float> score = detect.subList(5, output.cols());
                    int class_id = argmax(score); // index maximalnog elementa liste
                    float conf = score.get(class_id);
                    // TODO: This should also be a Consts!!!
                    if (conf >= 0.5) {
                        int center_x = (int) (detect.get(0) * img.cols());
                        int center_y = (int) (detect.get(1) * img.rows());
                        int width = (int) (detect.get(2) * img.cols());
                        int height = (int) (detect.get(3) * img.rows());
                        int x = (center_x - width / 2);
                        int y = (center_y - height / 2);
                        Rect2d box = new Rect2d(x, y, width, height);
                        result.get("boxes").add(box);
                        result.get("confidences").add(conf);
                        result.get("class_ids").add(class_id);
                    }
                }
            }
            return result;
        }

        /**
         Returns index of maximum element in the list
         */
        private  int argmax(List<Float> array) {
            float max = array.get(0);
            int re = 0;
            for (int i = 1; i < array.size(); i++) {
                if (array.get(i) > max) {
                    max = array.get(i);
                    re = i;
                }
            }
            return re;
        }

        private MatOfInt getBBoxIndicesFromNonMaximumSuppression(ArrayList<Rect2d> boxes, ArrayList<Float> confidences ) {
            MatOfRect2d mOfRect = new MatOfRect2d();
            mOfRect.fromList(boxes);
            MatOfFloat mfConfs = new MatOfFloat(Converters.vector_float_to_Mat(confidences));
            MatOfInt result = new MatOfInt();
            Dnn.NMSBoxes(mOfRect, mfConfs, (float)(0.6), (float)(0.5), result);
            return result;
        }

        private Mat drawBoxesOnTheImage(Mat img, MatOfInt indices, ArrayList<Rect2d> boxes, List<String> cocoLabels, ArrayList<Integer> class_ids, ArrayList<Scalar> colors) {
            //Scalar color = new Scalar( new double[]{255, 255, 0});
            List indices_list = indices.toList();
            for (int i = 0; i < boxes.size(); i++) {
                if (indices_list.contains(i)) {
                    Rect2d box = boxes.get(i);
                    Point x_y = new Point(box.x, box.y);
                    Point w_h = new Point(box.x + box.width, box.y + box.height);
                    Point text_point = new Point(box.x, box.y - 5);
                    Imgproc.rectangle(img, w_h, x_y, colors.get(class_ids.get(i)), 1);
                    String label = cocoLabels.get(class_ids.get(i));
                    Imgproc.putText(img, label, text_point, Imgproc.FONT_HERSHEY_SIMPLEX, 1, colors.get(class_ids.get(i)), 2);
                }
            }
            return img;
        }

    }
}
