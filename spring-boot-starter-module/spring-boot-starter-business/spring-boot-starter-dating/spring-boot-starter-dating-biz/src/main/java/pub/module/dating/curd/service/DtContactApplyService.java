package pub.module.dating.curd.service;

import pub.module.dating.curd.entity.DtContactApply;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 联系人申请表 Service
 *
 * @author tg
 * 2026-05-03 03:39:43
 */
public interface DtContactApplyService extends IService<DtContactApply> {
    DtContactApply getByCode(String code);
}
