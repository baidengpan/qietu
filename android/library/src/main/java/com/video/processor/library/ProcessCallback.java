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
     * @param success 处理结果
     */
    void finish(boolean success);
}
