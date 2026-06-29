package pub.module.cms.biz.service;

import pub.module.cms.api.dto.CmsShortUrlCreateReq;
import pub.module.cms.api.dto.CmsShortUrlResolveVO;

public interface SpiCmsShortUrlService {
    CmsShortUrlResolveVO createShortUrl(CmsShortUrlCreateReq req);

    CmsShortUrlResolveVO resolveByKey(String shortUrlKey);
}
