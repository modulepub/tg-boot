package pub.module.log.curd.service;

import pub.module.log.curd.entity.Log;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 日志表 Service
 *
 * @author tg
 * 2026-01-12 01:41:07
 */
public interface LogService extends IService<Log> {
    Log getByCode(String code);
}
