package pub.module.sms.crud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pub.module.sms.crud.entity.SmsTemplate;

@Mapper
public interface SmsTemplateMapper extends BaseMapper<SmsTemplate> {
}
