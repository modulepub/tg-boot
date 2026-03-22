
日志模块
===============
自动获取线程事务ID,方法名,在同一个线程内 各处记录日志 同属于 一个事务ID。
```angular2html
//异步记录
LogUtil.record(String logName, String logContent, String logUserCode, String logClientIp);
```
后续优化计划：
使用队列单线程入库，提高吞吐量及解放数据库压力。
