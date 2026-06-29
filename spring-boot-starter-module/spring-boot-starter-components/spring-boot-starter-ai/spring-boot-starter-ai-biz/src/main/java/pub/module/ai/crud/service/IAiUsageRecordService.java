package pub.module.ai.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.ai.crud.entity.AiUsageRecord;

public interface IAiUsageRecordService extends IService<AiUsageRecord> {

    AiUsageRecord getByCode(String aiUsageRecordCode);
}
