package pub.module.cms.crud.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.cms.crud.entity.CmsShortUrl;
import pub.module.cms.crud.mapper.CmsShortUrlMapper;
import pub.module.cms.crud.service.CmsShortUrlService;

import java.io.Serializable;
import java.lang.reflect.Field;

@Slf4j
@Service
public class CmsShortUrlServiceImpl extends ServiceImpl<CmsShortUrlMapper, CmsShortUrl> implements CmsShortUrlService {

    private static final String BIZ_CODE = "shortUrlCode";

    private void setDefaultCode(CmsShortUrl entity) {
        Field field = ReflectUtil.getField(CmsShortUrl.class, BIZ_CODE);
        Assert.notNull(field, "shortUrlCode 字段未配置");
        Object val = ReflectUtil.getFieldValue(entity, field);
        if (val == null || StrUtil.isBlank(String.valueOf(val))) {
            ReflectUtil.setFieldValue(entity, field, IdUtil.getSnowflakeNextIdStr());
        }
    }

    @Override
    public CmsShortUrl getByKey(String shortUrlKey) {
        if (StrUtil.isBlank(shortUrlKey)) {
            return null;
        }
        return getBaseMapper().selectOne(new QueryWrapper<CmsShortUrl>()
                .eq("short_url_key", shortUrlKey.trim())
                .eq("deleted", "0"), false);
    }

    @Override
    public CmsShortUrl getByCode(String shortUrlCode) {
        if (StrUtil.isBlank(shortUrlCode)) {
            return null;
        }
        return getBaseMapper().selectOne(new QueryWrapper<CmsShortUrl>()
                .eq("short_url_code", shortUrlCode.trim())
                .eq("deleted", "0"), false);
    }

    @Override
    @Transactional
    public boolean save(CmsShortUrl entity) {
        Assert.notNull(entity, "短链不能为空");
        Assert.notBlank(entity.getShortUrlKey(), "短码不能为空");
        Assert.notBlank(entity.getShortUrlTarget(), "目标链接不能为空");
        Assert.isNull(getByKey(entity.getShortUrlKey()), "短码已存在");
        setDefaultCode(entity);
        if (entity.getShortUrlClickCount() == null) {
            entity.setShortUrlClickCount(0L);
        }
        return getBaseMapper().insert(entity) > 0;
    }

    @Override
    @Transactional
    public boolean updateById(CmsShortUrl entity) {
        Assert.notNull(entity.getId(), "ID不能为空");
        return getBaseMapper().updateById(entity) > 0;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
        CmsShortUrl row = getBaseMapper().selectById(id);
        Assert.notNull(row, "短链不存在");
        row.setDeleted(1);
        return getBaseMapper().updateById(row) > 0;
    }
}
