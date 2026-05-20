package pub.module.verification.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.verification.api.dto.NpConfigDTO;
import pub.module.verification.api.service.ApiNpConfigService;
import pub.module.verification.biz.config.NpConfigRuntimeRefresher;
import pub.module.verification.biz.config.NpConfigRuntimeSnapshot;
import pub.module.verification.curd.entity.NpConfig;
import pub.module.verification.curd.service.NpConfigService;

import java.util.Collection;

/**
 * vt_np_config 维护并刷新运行时。
 */
@Service
public class ApiNpConfigServiceImpl implements ApiNpConfigService {

    @Resource
    private NpConfigService npConfigService;
    @Resource
    private NpConfigRuntimeRefresher npConfigRuntimeRefresher;

    private void normalize(NpConfig entity) {
        if (StrUtil.isBlank(entity.getNpConfigEnabledCode())) {
            entity.setNpConfigEnabledCode("0");
        }
        if (StrUtil.isBlank(entity.getNpConfigProviderCode())) {
            entity.setNpConfigProviderCode(NpConfigRuntimeSnapshot.PROVIDER_ALIYUN_CLOUDAUTH);
        }
        if (StrUtil.isBlank(entity.getNpConfigEndpoint())) {
            entity.setNpConfigEndpoint("cloudauth.aliyuncs.com");
        }
        if (StrUtil.isBlank(entity.getNpConfigMask())) {
            entity.setNpConfigMask("normal");
        }
    }

    private void validateForUpsert(NpConfig entity) {
        Assert.notBlank(entity.getNpConfigCode(), "np_config_code 不能为空");
        if ("1".equals(entity.getNpConfigEnabledCode())) {
            validateAliyunAccessKey(entity);
        }
    }

    /** RAM AccessKeyId 一般为 LTAI 前缀，勿填主账号 UID / 云市场 AppCode 等 */
    private static void validateAliyunAccessKey(NpConfig entity) {
        String keyId = StrUtil.trim(entity.getNpConfigAccessKeyId());
        String keySecret = StrUtil.trim(entity.getNpConfigAccessKeySecret());
        Assert.notBlank(keyId, "启用状态下请填写 AccessKeyId");
        Assert.notBlank(keySecret, "启用状态下请填写 AccessKeySecret");
        if (!keyId.regionMatches(true, 0, "LTAI", 0, 4)) {
            throw new IllegalArgumentException(
                    "AccessKeyId 格式不正确：请使用 RAM 控制台「AccessKey 管理」中创建的 AccessKey（通常以 LTAI 开头），"
                            + "不要填写阿里云账号 UID、云市场 AppCode 或号码百科授权码");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addAndRefreshRuntime(NpConfigDTO dto) {
        Assert.notNull(dto, "NpConfigDTO 不能为空");
        NpConfig entity = BeanUtil.copyProperties(dto, NpConfig.class);
        validateForUpsert(entity);
        normalize(entity);
        long exists = npConfigService.lambdaQuery()
                .eq(NpConfig::getNpConfigCode, entity.getNpConfigCode())
                .count();
        Assert.isTrue(exists == 0, "二要素配置编码已存在");
        npConfigService.save(entity);
        npConfigRuntimeRefresher.refreshFromDatabase();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAndRefreshRuntime(NpConfigDTO dto) {
        Assert.notNull(dto, "NpConfigDTO 不能为空");
        NpConfig entity = BeanUtil.copyProperties(dto, NpConfig.class);
        validateForUpsert(entity);
        normalize(entity);
        long exists = npConfigService.lambdaQuery()
                .eq(NpConfig::getNpConfigCode, entity.getNpConfigCode())
                .count();
        Assert.isTrue(exists > 0, "二要素配置不存在");
        npConfigService.updateById(entity);
        npConfigRuntimeRefresher.refreshFromDatabase();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeAndRefreshRuntime(Collection<String> npConfigCodes) {
        Assert.notEmpty(npConfigCodes, "请选择要删除的配置");
        npConfigService.removeByIds(npConfigCodes);
        npConfigRuntimeRefresher.refreshFromDatabase();
    }

    @Override
    public void refreshRuntimeFromDatabase() {
        npConfigRuntimeRefresher.refreshFromDatabase();
    }
}
