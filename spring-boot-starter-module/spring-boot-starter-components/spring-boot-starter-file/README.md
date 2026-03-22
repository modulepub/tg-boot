
文件模块
===============
- 1、实现图片、文件的回查并封装的实体类中返给前端。
- 2、实现上传功能，兼容分片上传，秒传能力。
- 当前最新版本： 取决于所在项目的springboot框架版本，请查看pom.xml文件中依赖的springboot版本。

- 3、上传文件: FileController
```java
/**
 * POST generic/upload
 * 上传文件
 * 上传不一定非得分片，可以直接上传，直接上传的话，sliceIndex和totalPieces参数分别传0和1即可
 *
 * @param file        上传的文件
 * @param filePath    上传的文件路径
 * @param md5         上传的文件的md5值 唯一性就行，不一定是MD5值
 * @param sliceIndex  上传的文件片段的索引
 * @param totalPieces 上传的文件片段的总数
 * @return SysFile 上传结果
 * 
 */
```