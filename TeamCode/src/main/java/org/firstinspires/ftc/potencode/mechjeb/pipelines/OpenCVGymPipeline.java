package org.firstinspires.ftc.potencode.mechjeb.pipelines;

import org.opencv.core.Mat;
import org.openftc.easyopencv.OpenCvPipeline;

public class OpenCVGymPipeline extends OpenCvPipeline {

    @Override
    public Mat processFrame(Mat input) {
        return input;
    }
}
