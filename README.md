# setu-import-helper

在 [laosepi](https://github.com/laosepi/setu) 上 pull 下来的 json 文件的格式并不符合常规 json 格式，所以无法直接将通过文件的方式将数据导出。

此工具实现了自动解析并将数据导入数据库。

## 用到的一些依赖

- kotlinx.serialization
- exposed
- hikaricp