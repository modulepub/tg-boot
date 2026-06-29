package pub.module.wx.crud.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.wx.crud.entity.WxMiniConfig;
import pub.module.wx.crud.mapper.WxMiniConfigMapper;
import pub.module.wx.crud.service.WxMiniConfigService;

import java.util.Collection;

/**
 * 微信小程序配置 CRUD 实现。
 */
@Slf4j
@Service
public class WxMiniConfigServiceImpl extends ServiceImpl<WxMiniConfigMapper, WxMiniConfig> implements WxMiniConfigService {

    private static final String BIZ_CODE = "wxMiniConfigCode";

    @Override
    public WxMiniConfig getByCode(String wxMiniConfigCode) {
        return getBaseMapper().selectOne(
                new QueryWrapper<WxMiniConfig>().eq(StrUtil.toUnderlineCase(BIZ_CODE), wxMiniConfigCode), false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(WxMiniConfig entity) {
        Object code = ReflectUtil.getFieldValue(entity, BIZ_CODE);
        Assert.notBlank(code == null ? null : code.toString(), "wx_mini_config_code 不能为空");
        Assert.isNull(getByCode(code.toString()), "微信小程序配置编码已存在");
        return getBaseMapper().insert(entity) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(WxMiniConfig entity) {
        Object code = ReflectUtil.getFieldValue(entity, BIZ_CODE);
        Assert.notNull(code, "wx_mini_config_code 不能为空");
        WxMiniConfig existing = getByCode(code.toString());
        Assert.notNull(existing, "微信小程序配置不存在");
        if (StrUtil.isNotBlank(entity.getId())) {
            Assert.isTrue(existing.getId().equals(entity.getId()), "配置编码与主键不匹配");
        } else {
            entity.setId(existing.getId());
        }
        return getBaseMapper().updateById(entity) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByBizCodes(Collection<String> wxMiniConfigCodes) {
        Assert.notEmpty(wxMiniConfigCodes, "请选择要删除的配置");
        for (String code : wxMiniConfigCodes) {
            WxMiniConfig row = getByCode(code);
            if (row != null) {
                getBaseMapper().deleteById(row.getId());
            }
        }
        return true;
    }
}
