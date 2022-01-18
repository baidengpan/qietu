//
//  ViewController.m
//  EdfuVideoHandler
//

#import "ViewController.h"
#import <AVFoundation/AVFoundation.h>

#import "video_processor_interface.h"
#import "common.h"

@interface ViewController ()<UITextViewDelegate>

@property (nonatomic, strong) UIButton *processButton;
@property (nonatomic, strong) UITextView *textView;

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    CGRect viewBounds = [UIScreen mainScreen].bounds;
    CGFloat btnX = 30.f;
    CGFloat btnY = 30.f;
    CGFloat btnW = viewBounds.size.width - btnX * 2.f;
    CGFloat btnH = 50.f;
    self.processButton = [UIButton buttonWithType:UIButtonTypeCustom];
    self.processButton.frame = CGRectMake(btnX, btnY, btnW, btnH);
    [self.processButton setTitle:@"开始处理" forState:UIControlStateNormal];
    self.processButton.backgroundColor = UIColor.grayColor;
    [self.view addSubview:self.processButton];
    [self.processButton addTarget:self action:@selector(process) forControlEvents:UIControlEventTouchUpInside];
    
    CGFloat textVX = 0.f;
    CGFloat textVY = btnY + btnH + 30.f;
    CGFloat textVW = viewBounds.size.width;
    CGFloat textVH = viewBounds.size.height - textVY;
    self.textView = [[UITextView alloc] init];
    self.textView.frame = CGRectMake(textVX, textVY, textVW, textVH);
    self.textView.backgroundColor = UIColor.grayColor;
    self.textView.delegate = self;
    self.textView.font = [UIFont systemFontOfSize:15.f];
    self.textView.textColor = UIColor.blackColor;
    [self.view addSubview:self.textView];
}

#pragma mark -UITextViewDelegate
- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text
{
    if ([text isEqualToString:@"\n"]) {
        [textView resignFirstResponder];
        return NO;
    }
    return YES;
}

#pragma mark -Click
- (void)process
{
    NSMutableString *processStrTip = [NSMutableString string];
    // 1、初始化模型文件路径[open方法model_path参数]
    std::string modelPath = [[[NSBundle mainBundle] pathForResource:@"rec_int8" ofType:@"mnn"] cStringUsingEncoding:NSUTF8StringEncoding];
    [processStrTip appendFormat:@"1、初始化模型文件路径：%s\n", modelPath.c_str()];
    [self showTipWith:processStrTip];
    // 2、初始化输入视频文件路径，最好为mp4文件[open方法input_video_path参数]
    std::string inputVideoPath = [[[NSBundle mainBundle] pathForResource:@"test" ofType:@"mp4"] cStringUsingEncoding:NSUTF8StringEncoding];
    
    CGRect videoInfo = [self getVideoSizeDurationWithVideoPath:[NSString stringWithFormat:@"%s", inputVideoPath.c_str()]];
    CGFloat videoDuration = videoInfo.origin.y;
    int width = videoInfo.size.width;
    int height = videoInfo.size.height;
    [processStrTip appendFormat:@"2、初始化输入视频文件路径：%s，视频时长：%.0f秒，视频分辨率：%dx%d\n", inputVideoPath.c_str(), videoDuration, width, height];
    [self showTipWith:processStrTip];
    // 3、指定输出文件目录、文件名[open方法output_path、uuid参数]
    NSString *directPath = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES).firstObject;
    std::string outputPath = [directPath cStringUsingEncoding:NSUTF8StringEncoding];
    std::string fileName = [[NSString stringWithFormat:@"%0.f", [[NSDate date] timeIntervalSince1970] * 1000.f] cStringUsingEncoding:NSUTF8StringEncoding];
    [processStrTip appendFormat:@"3、指定输出文件目录：%s、文件名：%s\n", outputPath.c_str(), fileName.c_str()];
    [self showTipWith:processStrTip];
    // 4、视频文件分辨率：宽高，用做输出视屏的宽高，按原视频比例等比缩放计算，宽高需要为2的整数倍。【open方法output_width、output_height参数】
    /* 750x1334-> 375x667*/
    int outputVideoWidth = 444;
    int outputVideoHeight = 960;
    [processStrTip appendFormat:@"4、输出视频文件分辨率：宽高，output_width：%d, output_height：%d\n", outputVideoWidth, outputVideoHeight];
    [self showTipWith:processStrTip];
    // 5、准备其他参数：idx、layout_type、source参数【均从唤起的URLScheme种获取，不影响，open方法idx、layout_type、source参数】
    // idx:
    // layout_type: [1-8]
    // source:
    std::string idx = "";
    int layout_type = 2; // 默认传2
    std::string source = "";
    [processStrTip appendFormat:@"5、准备其他参数：idx：%s、layout_type：%d、source：%s\n", idx.c_str(), layout_type, source.c_str()];
    [self showTipWith:processStrTip];
    // 6、创建VideoProcessorInterface实例【VideoProcessorInterface不是单例】
    vak::VideoProcessorInterface *videoProcessor =  vak::VideoProcessorInterface::getInstance();
    [processStrTip appendString:@"6、创建VideoProcessorInterface实例\n"];
    [self showTipWith:processStrTip];
    CFAbsoluteTime beginTime = CFAbsoluteTimeGetCurrent();
    NSLog(@"开始初始化....");
    // 7、调用Open方法初始化
    vak::Status initStatus = videoProcessor->open(modelPath, inputVideoPath, outputPath, fileName, outputVideoWidth, outputVideoHeight, idx, layout_type, source);
    if (initStatus == vak::Status::kSuccess) {
        [processStrTip appendString:@"7、调用Open方法初始化，初始化结果：Success\n"];
        [self showTipWith:processStrTip];
        NSLog(@"初始化成功，处理中");
        // 7、调用Process，开始处理视屏
        [processStrTip appendString:@"8、调用Process，开始处理视频...\n"];
        [self showTipWith:processStrTip];
        // ===！！！！！！！！由于process为同步方法，避免线程阻塞，自行进行线程调度！！！！！===
        dispatch_async(dispatch_get_global_queue(0, 0), ^{
            vak::Callback processCallBack = [&](int frame_num, float percent) {
                [processStrTip appendString:[NSString stringWithFormat:@"当前处理第%d帧，进度：%f%%\n", frame_num, percent]];
                [self showTipWith:processStrTip];
            };
            vak::Status processStatus = videoProcessor->process(processCallBack);
            if (processStatus == vak::Status::kSuccess) {
                // 8、Process处理成功，开始调用close方法收尾
                [processStrTip appendString:@"9、Process处理成功，开始调用close方法收尾\n"];
                [self showTipWith:processStrTip];

                vak::Status closeStatus = videoProcessor->close();
                if (closeStatus == vak::Status::kSuccess) {

                    CFAbsoluteTime endTime = CFAbsoluteTimeGetCurrent();
                    CFAbsoluteTime costTime = endTime - beginTime;
                    NSString *zipFilePath = [NSString stringWithFormat:@"%s/%s.zip", outputPath.c_str(), fileName.c_str()];
                    NSString *lowerVideoFilePath = [NSString stringWithFormat:@"%s/%s.mp4", outputPath.c_str(), fileName.c_str()];
                    if ([[NSFileManager defaultManager] fileExistsAtPath:zipFilePath] && [[NSFileManager defaultManager] fileExistsAtPath:lowerVideoFilePath]) {
                        // 9、close调用成功，开始调用close方法收尾
                        [processStrTip appendFormat:@"10、close调用成功，总流程Success，处理完毕，用时：%lf秒，文件：%@、%@成功生成\n", costTime, zipFilePath, lowerVideoFilePath];
                        [self showTipWith:processStrTip];
                        NSLog(@"处理完毕，用时：%lf秒，处理结果：Success，文件：%@、%@成功生成", costTime, zipFilePath, lowerVideoFilePath);
                    }
                    else {
                        // 9、close调用成功，开始调用close方法收尾
                        [processStrTip appendFormat:@"9、close调用成功，总流程Faild，处理完毕，用时：%lf秒，文件：%@、%@不存在\n", costTime, zipFilePath, lowerVideoFilePath];
                        [self showTipWith:processStrTip];
                        NSLog(@"处理完毕，用时：%lf秒，处理结果：Success，文件：%@、%@不存在", costTime, zipFilePath, lowerVideoFilePath);
                    }
                }
                else {
                    // 9、close调用成功，开始调用close方法收尾
                    [processStrTip appendString:@"10、close调用失败\n"];
                    [self showTipWith:processStrTip];
                    NSLog(@"close文件失败");
                }
            }
            else {
                // 8、Process处理失败
                [processStrTip appendString:@" 9、Process处理失败\n"];
                [self showTipWith:processStrTip];
                NSLog(@"process处理失败");
            }
        });
    }
    else {
        [processStrTip appendString:@"7、调用Open方法初始化，初始化结果：Failed\n"];
        [self showTipWith:processStrTip];
        NSLog(@"初始化失败");
    }
}

- (void)showTipWith:(NSString *)text
{
    dispatch_async(dispatch_get_main_queue(), ^{
        self.textView.text = text;
    });
}

- (CGRect)getVideoSizeDurationWithVideoPath:(NSString *)videoPath
{
    AVAssetTrack *videoTrack = nil;
    AVURLAsset *asset = [AVURLAsset assetWithURL:[NSURL fileURLWithPath:videoPath]];
    NSArray *videoTracks = [asset tracksWithMediaType:AVMediaTypeVideo];
    if ([videoTracks count] > 0)
    {
        videoTrack = [videoTracks objectAtIndex:0];
    }
    CGSize trackDimensions = {
        .width = 0.0,
        .height = 0.0,
    };
    trackDimensions = [videoTrack naturalSize];
    int width = trackDimensions.width;
    int height = trackDimensions.height;
    CGFloat duration = CMTimeGetSeconds(videoTrack.timeRange.duration);
    return CGRectMake(0.f, duration, width, height);
}

@end
