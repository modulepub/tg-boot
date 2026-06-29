package pub.module.wx.crud.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.wx.crud.entity.WxPayConfig;
import pub.module.wx.crud.mapper.WxPayConfigMapper;
import pub.module.wx.crud.service.WxPayConfigService;

import java.util.Collection;

/**
 * 微信支付配置 CRUD 实现。
 */
@Slf4j
@Service
public class WxPayConfigServiceImpl extends ServiceImpl<WxPayConfigMapper, WxPayConfig> implements WxPayConfigService {

    private static final String BIZ_CODE = "wxPayConfigCode";

    @Override
    public WxPayConfig getByCode(String wxPayConfigCode) {
        return getBaseMapper().selectOne(
                new QueryWrapper<WxPayConfig>().eq(StrUtil.toUnderlineCase(BIZ_CODE), wxPayConfigCode), false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(WxPayConfig entity) {
        Object code = ReflectUtil.getFieldValue(entity, BIZ_CODE);
        Assert.notBlank(code == null ? null : code.toString(), "wx_pay_config_code 不能为空");
        Assert.isNull(getByCode(code.toString()), "微信支付配置编码已存在");
        return getBaseMapper().insert(entity) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(WxPayConfig entity) {
        Object code = ReflectUtil.getFieldValue(entity, BIZ_CODE);
        Assert.notNull(code, "wx_pay_config_code 不能为空");
        WxPayConfig existing = getByCode(code.toString());
        Assert.notNull(existing, "微信支付配置不存在");
        if (StrUtil.isNotBlank(entity.getId())) {
            Assert.isTrue(existing.getId().equals(entity.getId()), "配置编码与主键不匹配");
        } else {
            entity.setId(existing.getId());
        }
        return getBaseMapper().updateById(entity) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByBizCodes(Collection<String> wxPayConfigCodes) {
        Assert.notEmpty(wxPayConfigCodes, "请选择要删除的配置");
        for (String code : wxPayConfigCodes) {
            WxPayConfig row = getByCode(code);
            if (row != null) {
                getBaseMapper().deleteById(row.getId());
            }
        }
        return true;
    }
}
