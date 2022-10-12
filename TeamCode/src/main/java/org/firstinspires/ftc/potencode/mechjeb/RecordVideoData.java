package org.firstinspires.ftc.potencode.mechjeb;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.opencv.core.Mat;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;
import org.openftc.easyopencv.PipelineRecordingParameters;

@TeleOp(name="Record Video Data")
public class RecordVideoData extends OpMode {
    private int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
    private WebcamName webcamName = hardwareMap.get(WebcamName.class, "camA");
    private OpenCvCamera camera = OpenCvCameraFactory.getInstance().createWebcam(webcamName, cameraMonitorViewId);

    @Override
    public void init() {
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                camera.setViewportRenderingPolicy(OpenCvCamera.ViewportRenderingPolicy.OPTIMIZE_VIEW);
                camera.setViewportRenderer(OpenCvCamera.ViewportRenderer.GPU_ACCELERATED);
                camera.startStreaming(1920, 1080, OpenCvCameraRotation.SIDEWAYS_LEFT);
                camera.setPipeline(new RecordVideoPipeline());
            }
            @Override
            public void onError(int errorCode)
            {
                telemetry.addData("-_-", errorCode);
            }
        });
    }

    @Override
    public void loop() {
        telemetry.addData("Frame Count", camera.getFrameCount());
        telemetry.addData("FPS", String.format("%.2f", camera.getFps()));
        telemetry.addData("Total frame time ms", camera.getTotalFrameTimeMs());
        telemetry.addData("Pipeline time ms", camera.getPipelineTimeMs());
        telemetry.addData("Overhead time ms", camera.getOverheadTimeMs());
        telemetry.addData("Theoretical max FPS", camera.getCurrentPipelineMaxFps());
    }


    class RecordVideoPipeline extends OpenCvPipeline
    {
        boolean toggleRecording = false;

        @Override
        public Mat processFrame(Mat input)
        {
            return input;
        }

        @Override
        public void onViewportTapped()
        {
            toggleRecording = !toggleRecording;

            if(toggleRecording)
            {
                camera.startRecordingPipeline(
                        new PipelineRecordingParameters.Builder()
                                .setBitrate(4, PipelineRecordingParameters.BitrateUnits.Mbps)
                                .setEncoder(PipelineRecordingParameters.Encoder.H264)
                                .setOutputFormat(PipelineRecordingParameters.OutputFormat.MPEG_4)
                                .setFrameRate(30)
                                .setPath("/sdcard/pipeline_rec.mp4")
                                .build());
            }
            else
            {
                camera.stopRecordingPipeline();
            }
        }
    }
}

