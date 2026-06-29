package pub.module.wx.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.wx.api.dto.WxMiniConfigDTO;
import pub.module.wx.api.service.ApiWxMiniConfigService;
import pub.module.wx.biz.config.WxMaRuntimeRefresher;
import pub.module.wx.crud.entity.WxMiniConfig;
import pub.module.wx.crud.service.WxMiniConfigService;

import java.util.Collection;

/**
 * 微信小程序配置业务实现。
 */
@Service
public class ApiWxMiniConfigServiceImpl implements ApiWxMiniConfigService {

    @Resource
    private WxMiniConfigService wxMiniConfigService;
    @Resource
    private WxMaRuntimeRefresher wxMaRuntimeRefresher;

    private void normalize(WxMiniConfig entity) {
        if (StrUtil.isBlank(entity.getWxMiniConfigMsgDataFormat())) {
            entity.setWxMiniConfigMsgDataFormat("JSON");
        }
        if (StrUtil.isBlank(entity.getWxMiniConfigEnabledCode())) {
            entity.setWxMiniConfigEnabledCode("1");
        }
    }

    private void validateForUpsert(WxMiniConfig entity) {
        Assert.notBlank(entity.getWxMiniConfigCode(), "wx_mini_config_code 不能为空");
        Assert.notBlank(entity.getWxMiniConfigAppId(), "wx_mini_config_app_id 不能为空");
        Assert.notBlank(entity.getWxMiniConfigAppSecret(), "wx_mini_config_app_secret 不能为空");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addAndRefreshRuntime(WxMiniConfigDTO dto) {
        Assert.notNull(dto, "WxMiniConfigDTO 不能为空");
        WxMiniConfig entity = BeanUtil.copyProperties(dto, WxMiniConfig.class);
        validateForUpsert(entity);
        normalize(entity);
        long exists = wxMiniConfigService.lambdaQuery()
                .eq(WxMiniConfig::getWxMiniConfigCode, entity.getWxMiniConfigCode())
                .count();
        Assert.isTrue(exists == 0, "微信小程序配置编码已存在");
        wxMiniConfigService.save(entity);
        wxMaRuntimeRefresher.refreshFromDatabase();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAndRefreshRuntime(WxMiniConfigDTO dto) {
        Assert.notNull(dto, "WxMiniConfigDTO 不能为空");
        WxMiniConfig entity = BeanUtil.copyProperties(dto, WxMiniConfig.class);
        validateForUpsert(entity);
        normalize(entity);
        long exists = wxMiniConfigService.lambdaQuery()
                .eq(WxMiniConfig::getWxMiniConfigCode, entity.getWxMiniConfigCode())
                .count();
        Assert.isTrue(exists > 0, "微信小程序配置不存在");
        wxMiniConfigService.updateById(entity);
        wxMaRuntimeRefresher.refreshFromDatabase();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeAndRefreshRuntime(Collection<String> wxMiniConfigCodes) {
        Assert.notEmpty(wxMiniConfigCodes, "请选择要删除的配置");
        wxMiniConfigService.removeByBizCodes(wxMiniConfigCodes);
        wxMaRuntimeRefresher.refreshFromDatabase();
    }

    @Override
    public void refreshWxMaRuntimeFromDatabase() {
        wxMaRuntimeRefresher.refreshFromDatabase();
    }
}
