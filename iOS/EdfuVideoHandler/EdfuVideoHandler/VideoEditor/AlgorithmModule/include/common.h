#pragma once
#include <string>

namespace vak {

enum Status { kSuccess = 0, kFailed };

// frame_num: number of frames that has been processed
// percent: procedure percent of current task
using Callback = std::function<void(int /*frame_num*/, float /*percent*/)>;

}  // namespace vak
