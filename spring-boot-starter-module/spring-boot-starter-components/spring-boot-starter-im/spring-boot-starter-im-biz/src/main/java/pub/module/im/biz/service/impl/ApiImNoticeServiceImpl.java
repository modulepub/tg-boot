package pub.module.im.biz.service.impl;

import pub.module.common.enums.StatusCodeEnum;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pub.module.im.api.constants.ImMessageTypeCodeEnum;
import pub.module.im.api.constants.ImNoticePublishStateCodeEnum;
import pub.module.im.api.constants.ImNoticeTargetTypeCodeEnum;
import pub.module.im.api.service.ApiImMessageService;
import pub.module.im.api.service.ApiImNoticeService;
import pub.module.im.api.service.dto.ImMessageSendDTO;
import pub.module.im.crud.entity.ImNotice;
import pub.module.im.crud.entity.ImNoticeRecipient;
import pub.module.im.crud.entity.ImUser;
import pub.module.im.crud.service.ImNoticeRecipientService;
import pub.module.im.crud.service.ImNoticeService;
import pub.module.im.crud.service.ImUserService;

import java.util.List;

@Slf4j
@Service
public class ApiImNoticeServiceImpl implements ApiImNoticeService {

    @Resource
    private ImNoticeService imNoticeService;
    @Resource
    private ImNoticeRecipientService imNoticeRecipientService;
    @Resource
    private ImUserService imUserService;
    @Resource
    private ApiImMessageService apiImMessageService;

    @Override
    public int publishAndBroadcast(String noticeId) {
        Assert.notBlank(noticeId, "noticeId is null");
        ImNotice notice = imNoticeService.getById(noticeId);
        Assert.notNull(notice, "通知不存在");
        Assert.isTrue(isDraft(notice.getImNoticePublishStateCode()), "通知已发送，不可重复发送");

        String senderUserCode = StrUtil.trim(notice.getImNoticeSenderUserCode());
        Assert.notBlank(senderUserCode, "请选择发送人");
        Assert.notBlank(notice.getImNoticeName(), "通知标题不能为空");
        Assert.notBlank(notice.getImNoticeImg(), "通知图片不能为空");

        ImUser sender = imUserService.getByUserCode(senderUserCode);
        Assert.notNull(sender, "发送人不存在于 IM 用户表");

        ImNoticeTargetTypeCodeEnum targetType = notice.getImNoticeTargetTypeCode();
        if (targetType == null) {
            targetType = ImNoticeTargetTypeCodeEnum.ALL;
        }
        Assert.isTrue(ImNoticeTargetTypeCodeEnum.ALL.equals(targetType), "暂仅支持全员通知");

        List<ImUser> recipients = imUserService.list(new QueryWrapper<ImUser>()
                .eq("deleted", 0)
                .ne("im_user_user_code", senderUserCode));

        String title = notice.getImNoticeName().trim();
        String text = StrUtil.nullToEmpty(notice.getImNoticeText());
        String imageUrl = notice.getImNoticeImg().trim();
        String linkUrl = StrUtil.blankToDefault(StrUtil.trim(notice.getImNoticeUrl()), "");

        int success = 0;
        int fail = 0;
        for (ImUser recipient : recipients) {
            String toUserCode = StrUtil.trim(recipient.getImUserUserCode());
            if (StrUtil.isBlank(toUserCode)) {
                continue;
            }
            boolean sent = sendToRecipient(notice, senderUserCode, toUserCode, title, text, imageUrl, linkUrl);
            if (sent) {
                success++;
            } else {
                fail++;
            }
        }

        notice.setImNoticePublishStateCode(ImNoticePublishStateCodeEnum.SENT);
        notice.setImNoticeSendCount(success);
        notice.setImNoticeFailCount(fail);
        imNoticeService.markPublished(notice.getId(), success, fail);
        log.info("IM全员通知发送完成 noticeCode={} success={} fail={}", notice.getImNoticeCode(), success, fail);
        return success;
    }

    private boolean sendToRecipient(ImNotice notice, String fromUserCode, String toUserCode,
                                    String title, String text, String imageUrl, String linkUrl) {
        ImNoticeRecipient record = new ImNoticeRecipient();
        record.setImNcRpCode(IdUtil.getSnowflakeNextIdStr());
        record.setImNoticeCode(notice.getImNoticeCode());
        record.setImNcRpUserCode(toUserCode);
        try {
            ImMessageSendDTO sendDTO = new ImMessageSendDTO();
            sendDTO.setToUserCode(toUserCode);
            sendDTO.setTypeCode(ImMessageTypeCodeEnum.RICH.getCode());
            sendDTO.setTitle(title);
            sendDTO.setContent(text);
            sendDTO.setImageUrl(imageUrl);
            sendDTO.setLinkUrl(linkUrl);
            apiImMessageService.sendMessage(fromUserCode, sendDTO);

            record.setImNcNpStatusCode(StatusCodeEnum.YES);
            imNoticeRecipientService.save(record);
            return true;
        } catch (Exception ex) {
            log.warn("IM通知发送失败 from={} to={}: {}", fromUserCode, toUserCode, ex.getMessage());
            record.setImNcNpStatusCode(StatusCodeEnum.NO);
            imNoticeRecipientService.save(record);
            return false;
        }
    }

    private static boolean isDraft(ImNoticePublishStateCodeEnum state) {
        return state == null || ImNoticePublishStateCodeEnum.DRAFT.equals(state);
    }
}
