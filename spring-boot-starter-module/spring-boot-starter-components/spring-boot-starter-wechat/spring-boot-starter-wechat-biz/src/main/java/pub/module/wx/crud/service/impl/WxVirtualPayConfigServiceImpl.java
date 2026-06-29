package pub.module.wx.crud.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.wx.crud.entity.WxVirtualPayConfig;
import pub.module.wx.crud.mapper.WxVirtualPayConfigMapper;
import pub.module.wx.crud.service.WxVirtualPayConfigService;

import java.util.Collection;

/**
 * 微信小程序虚拟支付配置 CRUD 实现。
 */
@Slf4j
@Service
public class WxVirtualPayConfigServiceImpl
        extends ServiceImpl<WxVirtualPayConfigMapper, WxVirtualPayConfig>
        implements WxVirtualPayConfigService {

    private static final String BIZ_CODE = "wxVirtualPayConfigCode";

    @Override
    public WxVirtualPayConfig getByCode(String wxVirtualPayConfigCode) {
        return getBaseMapper().selectOne(
                new QueryWrapper<WxVirtualPayConfig>().eq(StrUtil.toUnderlineCase(BIZ_CODE), wxVirtualPayConfigCode), false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(WxVirtualPayConfig entity) {
        Object code = ReflectUtil.getFieldValue(entity, BIZ_CODE);
        Assert.notBlank(code == null ? null : code.toString(), "wx_virtual_pay_config_code 不能为空");
        Assert.isNull(getByCode(code.toString()), "虚拟支付配置编码已存在");
        return getBaseMapper().insert(entity) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(WxVirtualPayConfig entity) {
        Object code = ReflectUtil.getFieldValue(entity, BIZ_CODE);
        Assert.notNull(code, "wx_virtual_pay_config_code 不能为空");
        WxVirtualPayConfig existing = getByCode(code.toString());
        Assert.notNull(existing, "虚拟支付配置不存在");
        if (StrUtil.isNotBlank(entity.getId())) {
            Assert.isTrue(existing.getId().equals(entity.getId()), "配置编码与主键不匹配");
        } else {
            entity.setId(existing.getId());
        }
        return getBaseMapper().updateById(entity) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByBizCodes(Collection<String> wxVirtualPayConfigCodes) {
        Assert.notEmpty(wxVirtualPayConfigCodes, "请选择要删除的配置");
        for (String code : wxVirtualPayConfigCodes) {
            WxVirtualPayConfig row = getByCode(code);
            if (row != null) {
                getBaseMapper().deleteById(row.getId());
            }
        }
        return true;
    }
}
