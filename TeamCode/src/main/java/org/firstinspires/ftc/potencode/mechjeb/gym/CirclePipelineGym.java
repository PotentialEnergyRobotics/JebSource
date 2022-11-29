package org.firstinspires.ftc.potencode.mechjeb.gym;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class CirclePipelineGym extends OpenCvPipeline {
    Mat grayMat = new Mat();
    Mat grayBlurMat = new Mat();

    Mat foundCircles = new Mat();

    @Override
    public Mat processFrame(Mat input)
    {
        Imgproc.cvtColor(input, grayMat, Imgproc.COLOR_RGB2GRAY);
        Imgproc.blur(grayMat, grayBlurMat, new Size(3, 3));

        Imgproc.HoughCircles(grayBlurMat, foundCircles, Imgproc.HOUGH_GRADIENT, 1.0, 20.0,
                20.0, 50.0, 1, 40);

        for (int i = 0; i < foundCircles.cols(); i++) {
            // telemetry.addData("[Found Circle]", "%s", foundCircles.get(0, i));
            double[] vCircle = foundCircles.get(0, i);

            Point pt = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
            int radius = (int)Math.round(vCircle[2]);
//            telemetry.addData("Found circle at", pt);
//            telemetry.addData("Radius", radius);

            Imgproc.circle(grayBlurMat, pt, radius, new Scalar(255, 0, 0), 2);
        }


//        telemetry.update();

        return grayBlurMat;
    }
}
