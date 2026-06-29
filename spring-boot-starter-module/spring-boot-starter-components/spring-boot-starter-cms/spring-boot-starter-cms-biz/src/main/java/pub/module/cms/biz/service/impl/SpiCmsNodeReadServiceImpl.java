package pub.module.cms.biz.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.cms.api.dto.CmsNodeReadProgressReq;
import pub.module.cms.api.dto.CmsNodeReadStartReq;
import pub.module.cms.api.dto.CmsNodeReadStartVO;
import pub.module.cms.biz.service.SpiCmsNodeReadService;
import pub.module.cms.crud.entity.CmsNode;
import pub.module.cms.crud.entity.CmsNodeReadRecord;
import pub.module.cms.crud.mapper.CmsNodeMapper;
import pub.module.cms.crud.service.CmsNodeReadRecordService;
import pub.module.cms.crud.service.CmsNodeService;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.common.util.IpLocationUtil;
import pub.module.common.util.IpUtil;

@Slf4j
@Service
public class SpiCmsNodeReadServiceImpl implements SpiCmsNodeReadService {

    private static final int READ_DEDUP_MINUTES = 1;

    @Resource
    private CmsNodeService cmsNodeService;
    @Resource
    private CmsNodeReadRecordService cmsNodeReadRecordService;
    @Resource
    private CmsNodeMapper cmsNodeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CmsNodeReadStartVO startRead(CmsNodeReadStartReq req) {
        Assert.notNull(req, "请求不能为空");
        String nodeId = StrUtil.trim(req.getNodeId());
        Assert.notBlank(nodeId, "文章 id 不能为空");

        CmsNode cmsNode = cmsNodeService.getById(nodeId);
        Assert.notNull(cmsNode, "文章不存在");
        Assert.isTrue(StatusCodeEnum.YES.equals(cmsNode.getNodePublishStatusCode()), "文章未发布");

        String clientIp = IpUtil.getRealIp();
        if (isDedupEligibleIp(clientIp)) {
            CmsNodeReadRecord recent = cmsNodeReadRecordService.getRecentByNodeCodeAndIp(
                    cmsNode.getNodeCode(), clientIp, READ_DEDUP_MINUTES);
            if (recent != null) {
                return buildStartVo(recent, cmsNode);
            }
        }

        String ipLocation = IpLocationUtil.resolve(clientIp);
        String sessionCode = IdUtil.getSnowflakeNextIdStr();

        CmsNodeReadRecord record = new CmsNodeReadRecord();
        record.setNodeReadRecordCode(IdUtil.getSnowflakeNextIdStr());
        record.setNodeCode(cmsNode.getNodeCode());
        record.setNodeReadRecordSessionCode(sessionCode);
        record.setNodeReadRecordUserCode(StrUtil.trimToNull(req.getUserCode()));
        record.setNodeReadRecordClientIp(clientIp);
        record.setNodeReadRecordIpLocation(ipLocation);
        record.setNodeReadRecordProgress(0);
        cmsNodeReadRecordService.save(record);

        cmsNodeMapper.incrementViewCount(nodeId);

        CmsNode refreshed = cmsNodeService.getById(nodeId);
        long viewCount = refreshed != null && refreshed.getNodeViewCount() != null
                ? refreshed.getNodeViewCount()
                : 1L;

        CmsNodeReadStartVO vo = new CmsNodeReadStartVO();
        vo.setSessionCode(sessionCode);
        vo.setProgress(0);
        vo.setNodeViewCount(viewCount);
        return vo;
    }

    private static CmsNodeReadStartVO buildStartVo(CmsNodeReadRecord record, CmsNode cmsNode) {
        CmsNodeReadStartVO vo = new CmsNodeReadStartVO();
        vo.setSessionCode(record.getNodeReadRecordSessionCode());
        vo.setProgress(record.getNodeReadRecordProgress() == null ? 0 : record.getNodeReadRecordProgress());
        long viewCount = cmsNode.getNodeViewCount() == null ? 0L : cmsNode.getNodeViewCount();
        vo.setNodeViewCount(viewCount);
        return vo;
    }

    private static boolean isDedupEligibleIp(String clientIp) {
        return StrUtil.isNotBlank(clientIp) && !"unknown".equalsIgnoreCase(clientIp.trim());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reportProgress(CmsNodeReadProgressReq req) {
        Assert.notNull(req, "请求不能为空");
        String sessionCode = StrUtil.trim(req.getSessionCode());
        Assert.notBlank(sessionCode, "阅读会话编码不能为空");

        int progress = normalizeProgress(req.getProgress());
        CmsNodeReadRecord record = cmsNodeReadRecordService.getBySessionCode(sessionCode);
        Assert.notNull(record, "阅读记录不存在");

        int current = record.getNodeReadRecordProgress() == null ? 0 : record.getNodeReadRecordProgress();
        if (progress <= current) {
            return;
        }

        CmsNodeReadRecord update = new CmsNodeReadRecord();
        update.setId(record.getId());
        update.setNodeReadRecordProgress(progress);
        cmsNodeReadRecordService.updateById(update);
    }

    private static int normalizeProgress(Integer progress) {
        if (progress == null) {
            return 0;
        }
        return Math.max(0, Math.min(100, progress));
    }

}
