package pub.module.wx.crud.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.wx.crud.entity.WxMaSubscribeTemplate;
import pub.module.wx.crud.mapper.WxMaSubscribeTemplateMapper;
import pub.module.wx.crud.service.WxMaSubscribeTemplateService;

import java.util.List;

@Service
public class WxMaSubscribeTemplateServiceImpl
        extends ServiceImpl<WxMaSubscribeTemplateMapper, WxMaSubscribeTemplate>
        implements WxMaSubscribeTemplateService {

    private static final String BIZ_CODE = "wxMaSubscribeTemplateCode";

    @Override
    public WxMaSubscribeTemplate getByCode(String wxMaSubscribeTemplateCode) {
        if (StrUtil.isBlank(wxMaSubscribeTemplateCode)) {
            return null;
        }
        return getBaseMapper().selectOne(
                new QueryWrapper<WxMaSubscribeTemplate>().eq(StrUtil.toUnderlineCase(BIZ_CODE), wxMaSubscribeTemplateCode.trim()),
                false);
    }

    @Override
    public WxMaSubscribeTemplate getByTemplateId(String wxMaSubscribeTemplateId) {
        if (StrUtil.isBlank(wxMaSubscribeTemplateId)) {
            return null;
        }
        return getBaseMapper().selectOne(
                new QueryWrapper<WxMaSubscribeTemplate>().eq("wx_ma_subscribe_template_id", wxMaSubscribeTemplateId.trim()),
                false);
    }

    @Override
    public List<WxMaSubscribeTemplate> listAllEnabled() {
        return list(new QueryWrapper<WxMaSubscribeTemplate>()
                .orderByAsc(StrUtil.toUnderlineCase(BIZ_CODE)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(WxMaSubscribeTemplate entity) {
        Object code = ReflectUtil.getFieldValue(entity, BIZ_CODE);
        Assert.notNull(code, "wx_ma_subscribe_template_code 不能为空");
        WxMaSubscribeTemplate existing = getByCode(code.toString());
        Assert.notNull(existing, "订阅消息模板不存在");
        if (StrUtil.isNotBlank(entity.getId())) {
            Assert.isTrue(existing.getId().equals(entity.getId()), "模板编码与主键不匹配");
        } else {
            entity.setId(existing.getId());
        }
        return getBaseMapper().updateById(entity) > 0;
    }
}
