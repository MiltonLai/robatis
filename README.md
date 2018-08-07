## 构建说明

创建Artifest任务

* File -> Project Structure -> Artifacts -> + JAR
* 选择 From module with depencies
* 选择包含正确main方法的class
* 选择 copy to the output directory and link via manifest
* 选择[module]/src/main/resources保存MANIFEST.MF
* 点击OK, 对话框关闭

调整构建任务

* 移除列出的所有依赖库文件
* <output root>上右键create directory, 创建lib, 创建conf
* 在右侧选择[module]下所有库文件, 都拖到lib下
* 在conf目录上右键, 选择Add Copy of -> Directory Content, 选择 [module]/ext/resource
* 点击module.jar, 在下面的properties里面, 点开 Class Path, 将内容复制出来, 每行前面增加 "lib/", 最后再加一行 "conf/", 保存, 回到主界面

执行构建

* 构建之前, 需要在文件系统里, 把 resource/*.properties 文件移动到[module]/ext/resource下,
* 然后构建jar文件 Build -> Build Artifacts -> module.jar -> Rebuild
* 前往项目的workspace/out/ 下寻找输出的文件
* 最后, 要记得把那些properties文件移回去

## 使用说明

* 修改properties里的配置
* 运行 java -jar module.jar, 在输出目录中找到生成的java和xml文件
