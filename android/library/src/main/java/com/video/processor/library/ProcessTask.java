package com.video.processor.library;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import java.io.File;

/**
 * date:2022/1/13
 **/
public class ProcessTask implements Runnable {
    private final ProcessConfig config;
    private final ProcessCallback callback;
    private final Context context;

    @Keep
    public void progress(int frame, float progress) {
        callback.progress(frame, progress);
    }

    public ProcessTask(@NonNull Context context, @NonNull ProcessConfig config, @NonNull ProcessCallback callback) {
        this.context = context;
        this.config = config;
        this.callback = callback;
    }

    @Override
    public void run() {
        if (!checkInputFile()) {
            callback.finish(ProcessorError.Code.INPUT_PATH_ERROR, ProcessorError.Message.INPUT_PATH_ERROR);
            return;
        }
        if (!checkOutputFile()) {
            callback.finish(ProcessorError.Code.OUTPUT_PATH_ERROR, ProcessorError.Message.OUTPUT_PATH_ERROR);
            return;
        }
        if (!checkNonNull(config.taskId)) {
            callback.finish(ProcessorError.Code.TASK_ID_ERROR, ProcessorError.Message.TASK_ID_ERROR);
            return;
        }
        if (!checkNonNull(config.source)) {
            callback.finish(ProcessorError.Code.SOURCE_ERROR, ProcessorError.Message.SOURCE_ERROR);
            return;
        }
        if (!checkNonNull(config.idx)) {
            callback.finish(ProcessorError.Code.IDX_ERROR, ProcessorError.Message.IDX_ERROR);
            return;
        }
        if (!checkOutputSize(config.outputWidth)) {
            callback.finish(ProcessorError.Code.OUTPUT_WIDTH_ERROR, ProcessorError.Message.OUTPUT_WIDTH_ERROR);
            return;
        }
        if (!checkOutputSize(config.outputHeight)) {
            callback.finish(ProcessorError.Code.OUTPUT_HEIGHT_ERROR, ProcessorError.Message.OUTPUT_HEIGHT_ERROR);
            return;
        }
        JniInterface jniInterface = new JniInterface(context);
        long handle = jniInterface.init();
        if (handle == 0) {
            callback.finish(ProcessorError.Code.INIT_ERROR, ProcessorError.Message.INIT_ERROR);
            return;
        }
        if (jniInterface.open(
                config.inputVideoPath,
                config.outputPath,
                config.taskId,
                config.outputWidth,
                config.outputHeight,
                config.idx,
                config.layoutType,
                config.source) != 0) {
            callback.finish(ProcessorError.Code.OPEN_ERROR, ProcessorError.Message.OPEN_ERROR);
            return;
        }
        if (jniInterface.process(this) != 0) {
            callback.finish(ProcessorError.Code.PROCESS_ERROR, ProcessorError.Message.PROCESS_ERROR);
        } else {
            callback.finish(ProcessorError.Code.SUCCESS, ProcessorError.Message.success);
        }
        jniInterface.close();
    }

    private boolean checkInputFile() {
        File f = new File(config.inputVideoPath);
        return f.exists() && f.canRead();
    }

    private boolean checkOutputFile() {
        File outputDir = new File(config.outputPath);
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            return false;
        }
        return outputDir.canWrite();
    }

    private boolean checkNonNull(String params) {
        return params != null;
    }

    private boolean checkOutputSize(int size) {
        if (size == 0) {
            return false;
        }
        return (size & 1) == 0;
    }
}
