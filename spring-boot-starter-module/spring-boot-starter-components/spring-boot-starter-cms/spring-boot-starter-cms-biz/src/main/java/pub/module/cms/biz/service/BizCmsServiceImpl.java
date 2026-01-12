package pub.module.cms.biz.service;


import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import pub.module.cms.api.service.BizCmsService;
import pub.module.cms.curd.constants.CcPublishStatusCodeEnum;
import pub.module.cms.curd.constants.CdPublishStatusCodeEnum;
import pub.module.cms.curd.entity.CmsChannel;
import pub.module.cms.curd.entity.CmsDocument;
import pub.module.cms.curd.service.ICmsChannelService;
import pub.module.cms.curd.service.ICmsDocumentService;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
@Service
public class BizCmsServiceImpl implements BizCmsService {
    @Resource
    ICmsChannelService cmsChannelService;
    @Resource
    ICmsDocumentService cmsDocumentService;

    public void setTree(CmsChannel cmsChannel) {
        Assert.notNull(cmsChannel,"cmsChannel is null");
        QueryWrapper<CmsChannel> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(CmsChannel::getCcParentCode, cmsChannel.getCcCode());
        List<CmsChannel> childList = cmsChannelService.list(queryWrapper);
        cmsChannel.setChildren(childList);
        for (CmsChannel item : childList) {
            this.setTree(item);
        }
    }

    @Override
    public CmsChannel getTree(String ccCode) {
        CmsChannel cmsChannel = cmsChannelService.getOne(new QueryWrapper<CmsChannel>().lambda().eq(CmsChannel::getCcCode, ccCode),false);
        Assert.notNull(cmsChannel,"cmsChannel is null");
        this.setTree(cmsChannel);
        return cmsChannel;
    }

    @Override
    public void publishCmsChannel(String ccCode) {
        CmsChannel cmsChannel = cmsChannelService.getOne(new QueryWrapper<CmsChannel>().lambda().eq(CmsChannel::getCcCode, ccCode),false);
        Assert.notNull(cmsChannel,"cmsChannel is null");
        cmsChannel.setCcPublishStatusCode(CcPublishStatusCodeEnum.PUBLISHED.getCode());
        cmsChannel.setCcPublishTime(new Date());
        cmsChannelService.updateById(cmsChannel);
    }

    @Override
    public void cancelPublishCmsChannel(String ccCode) {
        CmsChannel cmsChannel = cmsChannelService.getOne(new QueryWrapper<CmsChannel>().lambda().eq(CmsChannel::getCcCode, ccCode),false);
        Assert.notNull(cmsChannel,"cmsChannel is null");
        cmsChannel.setCcPublishStatusCode(CcPublishStatusCodeEnum.NOT_PUBLISHED.getCode());
        cmsChannel.setCcPublishTime(null);
        cmsChannelService.updateById(cmsChannel);
    }

    @Override
    public void publishCmsDocument(String cdCode) {
        CmsDocument cmsDocument = cmsDocumentService.getOne(new QueryWrapper<CmsDocument>().lambda().eq(CmsDocument::getCdCode, cdCode),false);
        Assert.notNull(cmsDocument,"cmsDocument is null");
        cmsDocument.setCdPublishStatusCode(CdPublishStatusCodeEnum.PUBLISHED.getCode());
        cmsDocument.setCdPublishTime(new Date());
        cmsDocumentService.updateById(cmsDocument);
    }

    @Override
    public void cancelPublishCmsDocument(String cdCode) {
        CmsDocument cmsDocument = cmsDocumentService.getOne(new QueryWrapper<CmsDocument>().lambda().eq(CmsDocument::getCdCode, cdCode),false);
        Assert.notNull(cmsDocument,"cmsDocument is null");
        cmsDocument.setCdPublishStatusCode(CdPublishStatusCodeEnum.NOT_PUBLISHED.getCode());
        cmsDocument.setCdPublishTime(null);
        cmsDocumentService.updateById(cmsDocument);
    }


}
