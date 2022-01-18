
- SDK所在目录：EdfuVideoHandler/EdfuVideoHandler/VideoEditor/AlgorithmModule

- SDK导入
	- 1、将'AlgorithmModel'拽到工程中，勾选"Copy items if needed" 和 ”Create Groups“，选择对应的Target。
	-  2、工程中展开"AlgorithmModel->lib"，右键lib，show in founder，把lib中所有的文件拖拽到工程配置BuildPhase->Link Binary中。
	- 3、Build Phase->Link Binary额外添加以下系统库：
		- libbz2.tbd
		- libiconv.tbd
		- VideoToolbox.framework
		- AudioToolbox.framework
		- CoreMedia.framework
	- 4、工程中BuildSettings->Other C++ Flags添加*'-std=c++14'*
	- 5、将工程目录"AlgorithmModel->Resource"下的"rec_int8.mnn"文件拖拽到BuildPhase->Copy Bundle Resources下。

- SDK接入
	- 参考EdfuVideoHandler工程->ViewControoler->Process方法。
	
```
  // 1、import头文件
  #import "video_processor_interface.h"
  #import "common.h"
  // 2、将引入头文件的.m文件改为.mm，使其能编译C++，其他步骤参考ViewControoler->Process方法
```
