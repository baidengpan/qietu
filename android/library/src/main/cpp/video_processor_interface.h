#pragma once

#include <string>

#include "common.h"
namespace vak {

class __attribute__((visibility("default"))) VideoProcessorInterface {
public:
    static VideoProcessorInterface *getInstance();
    virtual ~VideoProcessorInterface() {}
    // model_path: path of model file
    // input_video_path: path of input .mp4 video file
    // output_path: directory to store output files
    // uuid: a unique task id
    //       path of output mp4 is: ${output_path}/${uuid}.mp4
    //       path of output zip is: ${output_path}/${uuid}.zip
    //       uuid is composed by ${user_id}_${timestamp}
    //       user_id: a unique id of user
    //       timestamp: a program generated timestamp
    // output_width: width of output video
    // output_height: height of output video
    // idx:
    // layout_type: [1-8]
    // source:
    virtual Status open(const std::string &model_path, const std::string &input_video_path,
                        const std::string &output_path, const std::string &uuid, int output_width,
                        int output_height, const std::string &idx, int layout_type,
                        const std::string &source) = 0;
    virtual Status process() = 0;
    // cb: a callback function that report the progress of current task
    virtual Status process(Callback cb) = 0;
    // close must be called after the call of process is finish
    virtual Status close() = 0;
};

}  // namespace vak
