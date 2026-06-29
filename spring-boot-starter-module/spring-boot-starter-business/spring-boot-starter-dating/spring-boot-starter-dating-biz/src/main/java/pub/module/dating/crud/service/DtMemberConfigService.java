package pub.module.dating.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.dating.crud.entity.DtMemberConfig;

/**
 * 婚恋系统-会员配置 Service
 *
 * @author tg
 */
public interface DtMemberConfigService extends IService<DtMemberConfig> {

    /**
     * 获取唯一的会员配置行；不存在时初始化一条默认（关闭）记录。
     */
    DtMemberConfig getOrInitConfig();

    /**
     * 是否开启「注册即赠钻石会员·体验7天」。
     */
    boolean isRegisterGiftEnabled();

    /**
     * 保存「注册即赠钻石会员」开关。
     */
    DtMemberConfig saveRegisterGiftStatus(StatusCodeEnum statusCode);
}
