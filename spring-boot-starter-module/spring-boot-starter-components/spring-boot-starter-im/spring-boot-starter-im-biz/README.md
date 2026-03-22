
文件模块
===============
- 1、实现图片、文件的回查并封装的实体类中返给前端。
- 2、实现上传功能，兼容分片上传，秒传能力。
- 当前最新版本： 取决于所在项目的springboot框架版本，请查看pom.xml文件中依赖的springboot版本。


## 框架环境
- springboot & mybatis plus



## 开发环境


- IDE(JAVA)： Eclipse安装lombok插件 或者 IDEA

- 依赖管理：Maven

- 数据库：MySQL5.7+  &  Oracle 11g



## 使用说明

 在类上加上该注解，下面的所有方法都会支持文件自动 set get 操作。@Files注解实现接口返回关联实体的文件，需要在实体字段上加上本注解，并设置字段类型为List<SysFile>，返回时自动set值，实现原理是以实体的类型+字段名称+实体的ID作为文件的key，使用切面编程的方式，在controller层获取到实体后，通过注解获取到文件信息，并封装成SysFile对象返回给前端。
- 1、将本模块作为springboot 的模块集成到项目。
```java    
    // ···
    @TableField(exist = false)
    @Files
    private List<SysFile> xxxFiles;
    // ···

```
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