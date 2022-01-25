package com.video.processor.library;

/**
 * date:2022/1/13
 **/
public interface ProcessCallback {
    /**
     * @param frameFinished 已处理完的帧数
     * @param progress 处理进度
     */
    void progress(int frameFinished, float progress);

    /**
     * @param code see {@link com.video.processor.library.ProcessorError.Code}
     * @param message see {@link com.video.processor.library.ProcessorError.Message}
     */
    void finish(int code, String message);
}
