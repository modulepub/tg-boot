package pub.module.wx.crud.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.wx.crud.entity.WxMaSubscribeSendLog;
import pub.module.wx.crud.mapper.WxMaSubscribeSendLogMapper;
import pub.module.wx.crud.service.WxMaSubscribeSendLogService;

@Service
public class WxMaSubscribeSendLogServiceImpl
        extends ServiceImpl<WxMaSubscribeSendLogMapper, WxMaSubscribeSendLog>
        implements WxMaSubscribeSendLogService {

    @Override
    public boolean existsByIdempotentKey(String idempotentKey) {
        if (StrUtil.isBlank(idempotentKey)) {
            return false;
        }
        return this.count(new QueryWrapper<WxMaSubscribeSendLog>().lambda()
                .eq(WxMaSubscribeSendLog::getIdempotentKey, idempotentKey.trim())
                .eq(WxMaSubscribeSendLog::getSendStatusCode, StatusCodeEnum.YES)) > 0;
    }
}
