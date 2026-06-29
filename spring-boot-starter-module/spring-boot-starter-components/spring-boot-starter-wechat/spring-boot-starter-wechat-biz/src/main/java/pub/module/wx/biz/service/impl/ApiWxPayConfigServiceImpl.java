package pub.module.wx.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.wx.api.dto.WxPayConfigDTO;
import pub.module.wx.api.service.ApiWxPayConfigService;
import pub.module.wx.biz.config.WxPayRuntimeRefresher;
import pub.module.wx.crud.entity.WxPayConfig;
import pub.module.wx.crud.service.WxPayConfigService;

import java.util.Collection;

/**
 * 微信支付配置业务实现。
 */
@Service
public class ApiWxPayConfigServiceImpl implements ApiWxPayConfigService {

    @Resource
    private WxPayConfigService wxPayConfigService;
    @Resource
    private WxPayRuntimeRefresher wxPayRuntimeRefresher;

    private void normalize(WxPayConfig entity) {
        if (entity.getWxPayConfigUseSandbox() == null) {
            entity.setWxPayConfigUseSandbox(0);
        }
        if (StrUtil.isBlank(entity.getWxPayConfigEnabledCode())) {
            entity.setWxPayConfigEnabledCode("1");
        }
    }

    private void validateForUpsert(WxPayConfig entity) {
        Assert.notBlank(entity.getWxPayConfigCode(), "wx_pay_config_code 不能为空");
        Assert.notBlank(entity.getWxPayConfigAppId(), "wx_pay_config_app_id 不能为空");
        Assert.notBlank(entity.getWxPayConfigMchId(), "wx_pay_config_mch_id 不能为空");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addAndRefreshRuntime(WxPayConfigDTO dto) {
        Assert.notNull(dto, "WxPayConfigDTO 不能为空");
        WxPayConfig entity = BeanUtil.copyProperties(dto, WxPayConfig.class);
        validateForUpsert(entity);
        normalize(entity);
        long exists = wxPayConfigService.lambdaQuery()
                .eq(WxPayConfig::getWxPayConfigCode, entity.getWxPayConfigCode())
                .count();
        Assert.isTrue(exists == 0, "微信支付配置编码已存在");
        wxPayConfigService.save(entity);
        wxPayRuntimeRefresher.refreshFromDatabase();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAndRefreshRuntime(WxPayConfigDTO dto) {
        Assert.notNull(dto, "WxPayConfigDTO 不能为空");
        WxPayConfig entity = BeanUtil.copyProperties(dto, WxPayConfig.class);
        validateForUpsert(entity);
        normalize(entity);
        long exists = wxPayConfigService.lambdaQuery()
                .eq(WxPayConfig::getWxPayConfigCode, entity.getWxPayConfigCode())
                .count();
        Assert.isTrue(exists > 0, "微信支付配置不存在");
        wxPayConfigService.updateById(entity);
        wxPayRuntimeRefresher.refreshFromDatabase();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeAndRefreshRuntime(Collection<String> wxPayConfigCodes) {
        Assert.notEmpty(wxPayConfigCodes, "请选择要删除的配置");
        wxPayConfigService.removeByBizCodes(wxPayConfigCodes);
        wxPayRuntimeRefresher.refreshFromDatabase();
    }

    @Override
    public void refreshWxPayRuntimeFromDatabase() {
        wxPayRuntimeRefresher.refreshFromDatabase();
    }
}
