package pub.module.wx.crud.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.wx.crud.entity.WxMpFan;
import pub.module.wx.crud.mapper.WxMpFanMapper;
import pub.module.wx.crud.service.WxMpFanService;

import java.util.List;

@Service
public class WxMpFanServiceImpl extends ServiceImpl<WxMpFanMapper, WxMpFan> implements WxMpFanService {

    private static final String BIZ_CODE = "wxMpFanCode";

    @Override
    public WxMpFan getByConfigAndOpenId(String wxMpConfigCode, String openId) {
        if (StrUtil.hasBlank(wxMpConfigCode, openId)) {
            return null;
        }
        return lambdaQuery()
                .eq(WxMpFan::getWxMpConfigCode, wxMpConfigCode.trim())
                .eq(WxMpFan::getWxMpFanOpenId, openId.trim())
                .one();
    }

    @Override
    public List<WxMpFan> listByConfigCode(String wxMpConfigCode) {
        return lambdaQuery()
                .eq(WxMpFan::getWxMpConfigCode, wxMpConfigCode)
                .orderByDesc(WxMpFan::getWxMpFanLastMessageTime)
                .orderByDesc(WxMpFan::getCreateTime)
                .list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(WxMpFan entity) {
        Object code = ReflectUtil.getFieldValue(entity, BIZ_CODE);
        if (code == null || StrUtil.isBlank(code.toString())) {
            ReflectUtil.setFieldValue(entity, BIZ_CODE, IdUtil.getSnowflakeNextIdStr());
        }
        if (StrUtil.isBlank(entity.getId())) {
            entity.setId(entity.getWxMpFanCode());
        }
        return getBaseMapper().insert(entity) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(WxMpFan entity) {
        Assert.notBlank(entity.getWxMpFanCode(), "wx_mp_fan_code 不能为空");
        WxMpFan existing = getBaseMapper().selectOne(
                new QueryWrapper<WxMpFan>().eq(StrUtil.toUnderlineCase(BIZ_CODE), entity.getWxMpFanCode()), false);
        Assert.notNull(existing, "粉丝会话不存在");
        if (StrUtil.isNotBlank(entity.getId())) {
            Assert.isTrue(existing.getId().equals(entity.getId()), "粉丝编码与主键不匹配");
        } else {
            entity.setId(existing.getId());
        }
        return getBaseMapper().updateById(entity) > 0;
    }
}
