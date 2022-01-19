package com.video.processor.library;

/**
 * date:2022/1/13
 **/
public class ProcessConfig {
    /**
     * path of input .mp4 video file
     */
    public String inputVideoPath;
    /**
     * directory to store output files, we should create directory self
     */
    public String outputPath;
    /**
     * a unique task id
     * path of output mp4 is: ${output_path}/${uuid}.mp4
     * path of output zip is: ${output_path}/${uuid}.zip
     * uuid is composed by ${user_id}_${timestamp}
     * user_id: a unique id of user
     * timestamp: a program generated timestamp
     */
    public String taskId;
    /**
     * width of output video
     */
    public int outputWidth;
    /**
     * height of output video
     */
    public int outputHeight;
    public String idx;
    /**
     * 2~8，默认传2
     */
    public int layoutType;
    public String source;

    @Override
    public String toString() {
        return "ProcessConfig{" +
                ", inputVideoPath='" + inputVideoPath + '\'' +
                ", outputPath='" + outputPath + '\'' +
                ", taskId='" + taskId + '\'' +
                ", outputWidth=" + outputWidth +
                ", outputHeight=" + outputHeight +
                ", idx='" + idx + '\'' +
                ", layoutType='" + layoutType + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}
