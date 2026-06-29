package pub.module.wx.crud.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.wx.crud.entity.WxMpMessage;
import pub.module.wx.crud.mapper.WxMpMessageMapper;
import pub.module.wx.crud.service.WxMpMessageService;

import java.util.List;

@Service
public class WxMpMessageServiceImpl extends ServiceImpl<WxMpMessageMapper, WxMpMessage> implements WxMpMessageService {

    private static final String BIZ_CODE = "wxMpMessageCode";

    @Override
    public List<WxMpMessage> listByFan(String wxMpConfigCode, String openId) {
        return lambdaQuery()
                .eq(WxMpMessage::getWxMpConfigCode, wxMpConfigCode)
                .eq(WxMpMessage::getWxMpFanOpenId, openId)
                .orderByAsc(WxMpMessage::getCreateTime)
                .list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(WxMpMessage entity) {
        Object code = ReflectUtil.getFieldValue(entity, BIZ_CODE);
        if (code == null || StrUtil.isBlank(code.toString())) {
            ReflectUtil.setFieldValue(entity, BIZ_CODE, IdUtil.getSnowflakeNextIdStr());
        }
        if (StrUtil.isBlank(entity.getId())) {
            entity.setId(entity.getWxMpMessageCode());
        }
        return getBaseMapper().insert(entity) > 0;
    }
}
