package pub.module.wx.crud.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.wx.crud.entity.WxMpConfig;
import pub.module.wx.crud.mapper.WxMpConfigMapper;
import pub.module.wx.crud.service.WxMpConfigService;

import java.util.Collection;

/**
 * 微信公众号配置 CRUD 实现。
 */
@Slf4j
@Service
public class WxMpConfigServiceImpl extends ServiceImpl<WxMpConfigMapper, WxMpConfig> implements WxMpConfigService {

    private static final String BIZ_CODE = "wxMpConfigCode";

    @Override
    public WxMpConfig getByCode(String wxMpConfigCode) {
        return getBaseMapper().selectOne(
                new QueryWrapper<WxMpConfig>().eq(StrUtil.toUnderlineCase(BIZ_CODE), wxMpConfigCode), false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(WxMpConfig entity) {
        Object code = ReflectUtil.getFieldValue(entity, BIZ_CODE);
        Assert.notBlank(code == null ? null : code.toString(), "wx_mp_config_code 不能为空");
        Assert.isNull(getByCode(code.toString()), "微信公众号配置编码已存在");
        if (StrUtil.isBlank(entity.getId())) {
            entity.setId(code.toString());
        }
        return getBaseMapper().insert(entity) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(WxMpConfig entity) {
        Object code = ReflectUtil.getFieldValue(entity, BIZ_CODE);
        Assert.notNull(code, "wx_mp_config_code 不能为空");
        WxMpConfig existing = getByCode(code.toString());
        Assert.notNull(existing, "微信公众号配置不存在");
        if (StrUtil.isNotBlank(entity.getId())) {
            Assert.isTrue(existing.getId().equals(entity.getId()), "配置编码与主键不匹配");
        } else {
            entity.setId(existing.getId());
        }
        return getBaseMapper().updateById(entity) > 0;
    }

    @Override
    public WxMpConfig getByAppId(String appId) {
        WxMpConfig config = findByAppId(appId);
        if (config == null || !StatusCodeEnum.isYesValue(config.getWxMpConfigEnabledStatusCode())) {
            return null;
        }
        return config;
    }

    @Override
    public WxMpConfig findByAppId(String appId) {
        if (StrUtil.isBlank(appId)) {
            return null;
        }
        return lambdaQuery()
                .eq(WxMpConfig::getWxMpConfigAppId, appId.trim())
                .orderByAsc(WxMpConfig::getSeqNo)
                .orderByAsc(WxMpConfig::getWxMpConfigCode)
                .last("LIMIT 1")
                .one();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByBizCodes(Collection<String> wxMpConfigCodes) {
        Assert.notEmpty(wxMpConfigCodes, "请选择要删除的配置");
        for (String code : wxMpConfigCodes) {
            WxMpConfig row = getByCode(code);
            if (row != null) {
                getBaseMapper().deleteById(row.getId());
            }
        }
        return true;
    }
}
