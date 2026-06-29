package pub.module.verification.crud.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pub.module.verification.crud.entity.NpRecord;
import pub.module.verification.crud.mapper.NpRecordMapper;
import pub.module.verification.crud.service.NpRecordService;

/**
 * 手机号二要素核验记录 Service 实现
 */
@Service
public class NpRecordServiceImpl extends ServiceImpl<NpRecordMapper, NpRecord> implements NpRecordService {
}
