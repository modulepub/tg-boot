package pub.module.cms.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.cms.crud.entity.CmsShortUrl;

public interface CmsShortUrlService extends IService<CmsShortUrl> {
    CmsShortUrl getByKey(String shortUrlKey);

    CmsShortUrl getByCode(String shortUrlCode);
}
