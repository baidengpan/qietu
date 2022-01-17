package com.video.processor.library;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import java.io.File;

/**
 * date:2022/1/13
 **/
public class ProcessTask implements Runnable {
    private ProcessConfig config;
    private ProcessCallback callback;
    private Context context;

    @Keep
    public void progress(int frame, float progress) {
        callback.progress(frame, progress);
    }

    public ProcessTask(@NonNull Context context, @NonNull ProcessConfig config, @NonNull ProcessCallback callback) {
        this.context = context;
        this.config = config;
        this.callback = callback;
        File outputDir = new File(config.outputPath);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
    }

    @Override
    public void run() {
        JniInterface jniInterface = new JniInterface(context);
        long handle = jniInterface.init();
        if (handle == 0) {
            callback.finish(false);
        }
        int status = jniInterface.open(
                config.inputVideoPath,
                config.outputPath,
                config.taskId,
                config.outputWidth,
                config.outputHeight,
                config.idx,
                config.layoutType,
                config.source);
        if (status != 0) {
            callback.finish(false);
        }
        if (handle != 0) {
            jniInterface.process(this);
            jniInterface.close();
            callback.finish(true);
        }
    }
}
