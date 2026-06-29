package pub.module.sms.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.sms.api.dto.SmsTencentConfigDTO;
import pub.module.sms.api.service.ApiSmsTencentConfigService;
import pub.module.sms.biz.config.TencentConfigRuntimeRefresher;
import pub.module.sms.biz.config.TencentConfigRuntimeSnapshot;
import pub.module.sms.crud.entity.SmsTencentConfig;
import pub.module.sms.crud.service.ISmsTencentConfigService;

import java.util.Collection;

/**
 * sms_tencent_config 维护并刷新运行时。
 */
@Service
public class ApiSmsTencentConfigServiceImpl implements ApiSmsTencentConfigService {

    @Resource
    private ISmsTencentConfigService smsTencentConfigService;
    @Resource
    private TencentConfigRuntimeRefresher tencentConfigRuntimeRefresher;

    private void normalize(SmsTencentConfig entity) {
        if (StrUtil.isBlank(entity.getSmsTencentConfigEnabledCode())) {
            entity.setSmsTencentConfigEnabledCode("0");
        }
        if (StrUtil.isBlank(entity.getSmsTencentConfigRegion())) {
            entity.setSmsTencentConfigRegion(TencentConfigRuntimeSnapshot.DEFAULT_REGION);
        }
    }

    private void validateForUpsert(SmsTencentConfig entity) {
        Assert.notBlank(entity.getSmsTencentConfigCode(), "sms_tencent_config_code 不能为空");
        if ("1".equals(entity.getSmsTencentConfigEnabledCode())) {
            Assert.notBlank(entity.getSmsTencentConfigSecretId(), "启用状态下请填写 SecretId");
            Assert.notBlank(entity.getSmsTencentConfigSecretKey(), "启用状态下请填写 SecretKey");
            Assert.notBlank(entity.getSmsTencentConfigSdkAppId(), "启用状态下请填写 SdkAppId");
            Assert.notBlank(entity.getSmsTencentConfigSignName(), "启用状态下请填写短信签名");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addAndRefreshRuntime(SmsTencentConfigDTO dto) {
        Assert.notNull(dto, "SmsTencentConfigDTO 不能为空");
        SmsTencentConfig entity = BeanUtil.copyProperties(dto, SmsTencentConfig.class);
        validateForUpsert(entity);
        normalize(entity);
        long exists = smsTencentConfigService.lambdaQuery()
                .eq(SmsTencentConfig::getSmsTencentConfigCode, entity.getSmsTencentConfigCode())
                .count();
        Assert.isTrue(exists == 0, "腾讯云短信配置编码已存在");
        smsTencentConfigService.save(entity);
        tencentConfigRuntimeRefresher.refreshFromDatabase();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAndRefreshRuntime(SmsTencentConfigDTO dto) {
        Assert.notNull(dto, "SmsTencentConfigDTO 不能为空");
        SmsTencentConfig entity = BeanUtil.copyProperties(dto, SmsTencentConfig.class);
        validateForUpsert(entity);
        normalize(entity);
        long exists = smsTencentConfigService.lambdaQuery()
                .eq(SmsTencentConfig::getSmsTencentConfigCode, entity.getSmsTencentConfigCode())
                .count();
        Assert.isTrue(exists > 0, "腾讯云短信配置不存在");
        smsTencentConfigService.updateById(entity);
        tencentConfigRuntimeRefresher.refreshFromDatabase();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeAndRefreshRuntime(Collection<String> smsTencentConfigCodes) {
        Assert.notEmpty(smsTencentConfigCodes, "请选择要删除的配置");
        smsTencentConfigService.removeByBizCodes(smsTencentConfigCodes);
        tencentConfigRuntimeRefresher.refreshFromDatabase();
    }

    @Override
    public void refreshRuntimeFromDatabase() {
        tencentConfigRuntimeRefresher.refreshFromDatabase();
    }
}
