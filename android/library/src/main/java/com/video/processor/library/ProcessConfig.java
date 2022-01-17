package com.video.processor.library;

/**
 * date:2022/1/13
 **/
public class ProcessConfig {
    public String inputVideoPath;
    public String outputPath;
    public String taskId;
    public int outputWidth;
    public int outputHeight;
    public String idx;
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
