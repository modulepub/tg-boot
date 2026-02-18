package pub.module.dict.curd.service;

import pub.module.dict.curd.entity.Dict;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * sys_dict
 * @author tg
 * @since 2025-09-29
 * @version V1.0
 */
public interface DictService extends IService<Dict> {
    Dict getByCode(String code);
}
