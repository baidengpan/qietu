package com.video.processor.library;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * date:2022/1/13
 **/
public class JniInterface {
    private static final String MODEL_FILE = "model/recorder_int8.mnn";
    private Context context;
    private String modelPath;
    private long handle;

    static {
        System.loadLibrary("videoprocessor");
    }

    public JniInterface(Context context) {
        this.context = context;
        copyModelToFileIfNeeded();
    }


    private void copyModelToFileIfNeeded() {
        File file = new File(context.getFilesDir(), MODEL_FILE);
        modelPath = file.getAbsolutePath();
        if (file.exists()) {
            return;
        } else {
            File parent = file.getParentFile();
            if (!parent.exists()) {
                if (!parent.mkdirs()) {
                    return;
                }
            }
        }
        try {
            InputStream is = context.getAssets().open("model/recorder_int8.mnn");
            ModelUtils.copyToFile(is, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long init() {
        handle = nInit();
        return handle;
    }

    public int open(String inputVideoPath,
                    String outputPath,
                    String uuid,
                    int outputWidth,
                    int outputHeight,
                    String idx,
                    int layoutType,
                    String source) {
        return nOpen(handle, modelPath, inputVideoPath, outputPath, uuid, outputWidth, outputHeight, idx, layoutType, source);
    }

    public int process(Object progressCallback) {
        return nProcess(handle, progressCallback);
    }

    public void close() {
        nClose(handle);
    }

    /**
     * @return instance handle
     */
    private native long nInit();

    /**
     * @param handle         instance handle
     * @param modelPath      path of model file
     * @param inputVideoPath path of input .mp4 video file
     * @param outputPath     directory to store output files, we should create directory self
     * @param uuid           a unique task id
     *                       path of output mp4 is: ${output_path}/${uuid}.mp4
     *                       path of output zip is: ${output_path}/${uuid}.zip
     *                       uuid is composed by ${user_id}_${timestamp}
     *                       user_id: a unique id of user
     *                       timestamp: a program generated timestamp
     * @param outputWidth    width of output video
     * @param outputHeight   height of output video
     * @param idx
     * @param layoutType     2~8，默认传2
     * @param source
     * @return 0:success, 1:failed
     */
    private native int nOpen(long handle,
                             String modelPath,
                             String inputVideoPath,
                             String outputPath,
                             String uuid,
                             int outputWidth,
                             int outputHeight,
                             String idx,
                             int layoutType,
                             String source
    );

    /**
     * progress 是一个同步方法，直到任务执行完才会返回
     *
     * @param handle           instance handle
     * @param progressCallback progress callback
     * @return 0:success, 1:failed
     */
    private native int nProcess(long handle, Object progressCallback);

    private native void nClose(long handle);
}
