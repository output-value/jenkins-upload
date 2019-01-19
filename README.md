# jenkins-plugins
jenkins打包之后上传apk文件

## 设置参数

1. params需要获取到的参数，所有的参数使用`$`分割，所有的参数会以key-value的形式上传到服务器
2. upload 上传apk的接口路径，目前1.0版本会遍历项目下的目录，并上传所有的apk文件
