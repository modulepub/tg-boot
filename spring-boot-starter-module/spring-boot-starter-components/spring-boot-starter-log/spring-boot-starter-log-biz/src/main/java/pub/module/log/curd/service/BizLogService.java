package pub.module.log.curd.service;

import pub.module.log.curd.entity.BizLog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 日志表 Service
 *
 * @author tg
 * 2026-01-12 01:41:07
 */
public interface BizLogService extends IService<BizLog> {
    BizLog getByCode(String code);
}
