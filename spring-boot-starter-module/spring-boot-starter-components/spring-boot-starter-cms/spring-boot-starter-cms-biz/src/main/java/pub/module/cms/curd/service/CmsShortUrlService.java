package pub.module.cms.curd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.cms.curd.entity.CmsShortUrl;

public interface CmsShortUrlService extends IService<CmsShortUrl> {
    CmsShortUrl getByKey(String shortUrlKey);

    CmsShortUrl getByCode(String shortUrlCode);
}
