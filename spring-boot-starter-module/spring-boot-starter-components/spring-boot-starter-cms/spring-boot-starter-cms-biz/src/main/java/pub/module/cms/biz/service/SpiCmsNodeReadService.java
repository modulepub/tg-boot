package pub.module.cms.biz.service;

import pub.module.cms.api.dto.CmsNodeReadProgressReq;
import pub.module.cms.api.dto.CmsNodeReadStartReq;
import pub.module.cms.api.dto.CmsNodeReadStartVO;

public interface SpiCmsNodeReadService {

    CmsNodeReadStartVO startRead(CmsNodeReadStartReq req);

    void reportProgress(CmsNodeReadProgressReq req);
}
