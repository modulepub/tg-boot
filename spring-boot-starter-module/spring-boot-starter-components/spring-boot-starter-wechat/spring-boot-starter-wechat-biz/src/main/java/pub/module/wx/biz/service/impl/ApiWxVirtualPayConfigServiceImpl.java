package pub.module.wx.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.wx.api.dto.WxVirtualPayConfigDTO;
import pub.module.wx.api.service.ApiWxVirtualPayConfigService;
import pub.module.wx.biz.config.WxVirtualPayRuntimeRefresher;
import pub.module.wx.crud.entity.WxVirtualPayConfig;
import pub.module.wx.crud.service.WxVirtualPayConfigService;

import java.util.Collection;

/**
 * 微信小程序虚拟支付配置业务实现。
 */
@Service
public class ApiWxVirtualPayConfigServiceImpl implements ApiWxVirtualPayConfigService {

    @Resource
    private WxVirtualPayConfigService wxVirtualPayConfigService;
    @Resource
    private WxVirtualPayRuntimeRefresher wxVirtualPayRuntimeRefresher;

    private void normalize(WxVirtualPayConfig entity) {
        if (entity.getWxVirtualPayConfigUseSandbox() == null) {
            entity.setWxVirtualPayConfigUseSandbox(0);
        }
        if (StrUtil.isBlank(entity.getWxVirtualPayConfigEnabledCode())) {
            entity.setWxVirtualPayConfigEnabledCode("1");
        }
    }

    private void validateForUpsert(WxVirtualPayConfig entity) {
        Assert.notBlank(entity.getWxVirtualPayConfigCode(), "wx_virtual_pay_config_code 不能为空");
        Assert.notBlank(entity.getWxVirtualPayConfigAppId(), "wx_virtual_pay_config_app_id 不能为空");
        Assert.notBlank(entity.getWxVirtualPayConfigOfferId(), "wx_virtual_pay_config_offer_id 不能为空");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addAndRefreshRuntime(WxVirtualPayConfigDTO dto) {
        Assert.notNull(dto, "WxVirtualPayConfigDTO 不能为空");
        WxVirtualPayConfig entity = BeanUtil.copyProperties(dto, WxVirtualPayConfig.class);
        validateForUpsert(entity);
        normalize(entity);
        long exists = wxVirtualPayConfigService.lambdaQuery()
                .eq(WxVirtualPayConfig::getWxVirtualPayConfigCode, entity.getWxVirtualPayConfigCode())
                .count();
        Assert.isTrue(exists == 0, "虚拟支付配置编码已存在");
        wxVirtualPayConfigService.save(entity);
        wxVirtualPayRuntimeRefresher.refreshFromDatabase();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAndRefreshRuntime(WxVirtualPayConfigDTO dto) {
        Assert.notNull(dto, "WxVirtualPayConfigDTO 不能为空");
        WxVirtualPayConfig entity = BeanUtil.copyProperties(dto, WxVirtualPayConfig.class);
        validateForUpsert(entity);
        normalize(entity);
        long exists = wxVirtualPayConfigService.lambdaQuery()
                .eq(WxVirtualPayConfig::getWxVirtualPayConfigCode, entity.getWxVirtualPayConfigCode())
                .count();
        Assert.isTrue(exists > 0, "虚拟支付配置不存在");
        wxVirtualPayConfigService.updateById(entity);
        wxVirtualPayRuntimeRefresher.refreshFromDatabase();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeAndRefreshRuntime(Collection<String> wxVirtualPayConfigCodes) {
        Assert.notEmpty(wxVirtualPayConfigCodes, "请选择要删除的配置");
        wxVirtualPayConfigService.removeByBizCodes(wxVirtualPayConfigCodes);
        wxVirtualPayRuntimeRefresher.refreshFromDatabase();
    }

    @Override
    public void refreshWxVirtualPayRuntimeFromDatabase() {
        wxVirtualPayRuntimeRefresher.refreshFromDatabase();
    }
}
