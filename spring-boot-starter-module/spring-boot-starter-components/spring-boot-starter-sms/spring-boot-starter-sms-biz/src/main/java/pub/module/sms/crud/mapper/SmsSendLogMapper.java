package pub.module.sms.crud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pub.module.sms.crud.entity.SmsSendLog;

@Mapper
public interface SmsSendLogMapper extends BaseMapper<SmsSendLog> {
}
