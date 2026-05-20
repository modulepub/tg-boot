package pub.module.cms.biz.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.cms.api.constants.ShortUrlStatusCodeEnum;
import pub.module.cms.api.dto.CmsShortUrlCreateReq;
import pub.module.cms.api.dto.CmsShortUrlResolveVO;
import pub.module.cms.biz.service.SpiCmsShortUrlService;
import pub.module.cms.curd.entity.CmsShortUrl;
import pub.module.cms.curd.service.CmsShortUrlService;

import java.util.Date;

@Service
public class SpiCmsShortUrlServiceImpl implements SpiCmsShortUrlService {

    private static final String KEY_CHARS = "23456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
    private static final int KEY_LEN = 8;

    @Resource
    private CmsShortUrlService cmsShortUrlService;

    @Override
    @Transactional
    public CmsShortUrlResolveVO createShortUrl(CmsShortUrlCreateReq req) {
        Assert.notNull(req, "请求不能为空");
        String target = normalizeTarget(req.getShortUrlTarget());
        Assert.notBlank(target, "目标链接不能为空");
        if (target.length() > 2000) {
            throw new IllegalArgumentException("目标链接过长");
        }

        CmsShortUrl row = new CmsShortUrl();
        row.setShortUrlKey(generateUniqueKey());
        row.setShortUrlTarget(target);
        row.setShortUrlTitle(StrUtil.trim(req.getShortUrlTitle()));
        row.setShortUrlStatusCode(ShortUrlStatusCodeEnum.ENABLED);
        row.setShortUrlClickCount(0L);
        cmsShortUrlService.save(row);
        return toResolveVo(row);
    }

    @Override
    @Transactional
    public CmsShortUrlResolveVO resolveByKey(String shortUrlKey) {
        String key = StrUtil.trim(shortUrlKey);
        Assert.notBlank(key, "短码不能为空");
        CmsShortUrl row = cmsShortUrlService.getByKey(key);
        Assert.notNull(row, "短链不存在或已失效");
        assertEnabled(row);
        Long clicks = row.getShortUrlClickCount() == null ? 0L : row.getShortUrlClickCount();
        row.setShortUrlClickCount(clicks + 1);
        cmsShortUrlService.updateById(row);
        return toResolveVo(row);
    }

    private void assertEnabled(CmsShortUrl row) {
        if (row.getShortUrlStatusCode() == ShortUrlStatusCodeEnum.DISABLED) {
            throw new IllegalArgumentException("短链已禁用");
        }
        Date expire = row.getShortUrlExpireTime();
        if (expire != null && expire.before(new Date())) {
            throw new IllegalArgumentException("短链已过期");
        }
    }

    private String generateUniqueKey() {
        for (int i = 0; i < 20; i++) {
            String key = RandomUtil.randomString(KEY_CHARS, KEY_LEN);
            if (cmsShortUrlService.getByKey(key) == null) {
                return key;
            }
        }
        throw new IllegalStateException("短码生成失败，请重试");
    }

    private String normalizeTarget(String raw) {
        String t = StrUtil.trim(raw);
        if (t.startsWith("/")) {
            t = t.substring(1);
        }
        return t;
    }

    private CmsShortUrlResolveVO toResolveVo(CmsShortUrl row) {
        CmsShortUrlResolveVO vo = new CmsShortUrlResolveVO();
        vo.setShortUrlKey(row.getShortUrlKey());
        vo.setShortUrlTarget(row.getShortUrlTarget());
        vo.setShortUrlTitle(row.getShortUrlTitle());
        return vo;
    }
}
