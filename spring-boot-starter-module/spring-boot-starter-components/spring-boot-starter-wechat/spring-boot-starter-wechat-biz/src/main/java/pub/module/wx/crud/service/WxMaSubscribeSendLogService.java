package pub.module.wx.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.wx.crud.entity.WxMaSubscribeSendLog;

public interface WxMaSubscribeSendLogService extends IService<WxMaSubscribeSendLog> {

    boolean existsByIdempotentKey(String idempotentKey);
}
