package pub.module.file.curd.service;

import pub.module.file.curd.entity.BizFile;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 文件 Service
 *
 * @author tg
 * 2026-03-09 07:28:53
 */
public interface BizFileService extends IService<BizFile> {
    BizFile getByCode(String code);
}
