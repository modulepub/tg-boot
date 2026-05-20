package pub.module.verification.curd.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pub.module.verification.curd.entity.NpRecord;
import pub.module.verification.curd.mapper.NpRecordMapper;
import pub.module.verification.curd.service.NpRecordService;

/**
 * 手机号二要素核验记录 Service 实现
 */
@Service
public class NpRecordServiceImpl extends ServiceImpl<NpRecordMapper, NpRecord> implements NpRecordService {
}
