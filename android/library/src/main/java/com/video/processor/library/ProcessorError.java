package com.video.processor.library;

/**
 * author:baidengpan
 * date:2022/1/19
 **/
public interface ProcessorError {
    interface Code {
        int SUCCESS = 0;
        //参数错误
        int INPUT_PATH_ERROR = 1;
        int OUTPUT_PATH_ERROR = 2;
        int TASK_ID_ERROR = 3;
        int IDX_ERROR = 4;
        int SOURCE_ERROR = 5;
        int OUTPUT_WIDTH_ERROR = 6;
        int OUTPUT_HEIGHT_ERROR = 7;

        //jni 执行异常
        int INIT_ERROR = 10;
        int OPEN_ERROR = 11;
        int PROCESS_ERROR = 12;
    }

    interface Message {
        String success = "success";

        //参数错误
        String INPUT_PATH_ERROR = "input file not exist or has no permission to read";
        String OUTPUT_PATH_ERROR = "output file not exist and fail to make dir, or has no permission to write";
        String TASK_ID_ERROR = "task id is null";
        String IDX_ERROR = "idx is null";
        String SOURCE_ERROR = "source is null";
        String OUTPUT_WIDTH_ERROR = "output width should be even number and greater than 0";
        String OUTPUT_HEIGHT_ERROR = "output height should be even number and greater than 0";

        //jni 执行异常
        String INIT_ERROR = "jni init() error";
        String OPEN_ERROR = "jni open() error";
        String PROCESS_ERROR = "jni process() error";
    }
}
