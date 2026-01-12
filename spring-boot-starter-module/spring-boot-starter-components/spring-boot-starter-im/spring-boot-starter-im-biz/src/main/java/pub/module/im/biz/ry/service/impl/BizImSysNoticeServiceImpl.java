package pub.module.im.biz.ry.service.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.json.JSONObject;
import io.rong.messages.ImgTextMessage;
import io.rong.models.message.SystemMessage;
import io.rong.models.response.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pub.module.im.api.constants.ImSysNoticePublishStateCodeEnum;
import pub.module.im.api.service.BizImSysNoticeService;
import pub.module.im.biz.ry.service.BizRyService;
import pub.module.im.curd.entity.ImSysNotice;
import pub.module.im.curd.service.IImSysNoticeService;
import pub.module.system.api.service.BizSysUserService;
import pub.module.system.api.service.dto.UserDTO;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 系统通知
 *
 * @author tg
 * @version V1.0
 * @since 2025-10-05
 */
@Slf4j
@Service
public class BizImSysNoticeServiceImpl implements BizImSysNoticeService {
    @Resource
    IImSysNoticeService imSysNoticeService;
    @Resource
    BizRyService bizRyService;
    @Resource
    BizSysUserService bizSysUserService;



    public void sendNotice(String fromSysUserCode, String[] toSysUserCodes, String content, String extra, String title, String imageUri, String url) throws Exception {
        SystemMessage systemMessage = new SystemMessage();
        systemMessage.setSenderId(fromSysUserCode);
        systemMessage.setTargetId(toSysUserCodes);
        //    String content, String extra, String title, String imageUri, String url
        ImgTextMessage imgTextMessage = new ImgTextMessage(
                content,
                extra,
                title,
                imageUri,
                url);
        systemMessage.setContent(imgTextMessage);
        systemMessage.setObjectName(imgTextMessage.getType());
        MessageResult messageResult = bizRyService.getRongCloud().message.system.send(systemMessage);
        log.info("发送通知消息返回:{}", messageResult);
    }


    public void publish(ImSysNotice imSysNotice) {
        JSONObject noticeConfig = new JSONObject();
        imSysNotice.setImSysNoticePublishStateCode(ImSysNoticePublishStateCodeEnum.YES.getCode());
        imSysNoticeService.updateById(imSysNotice);
        List<UserDTO> sysUserList = bizSysUserService.list(new UserDTO());
        List<String> userCodeList = sysUserList.stream().map(UserDTO::getUserCode).toList();

        int spNum = userCodeList.size() / 80;
        List<List<String>> partition = ListUtil.partition(userCodeList, spNum);
        for (List<String> list : partition) {
            String[] toUserCodes = list.toArray(new String[0]);
            try {
                this.sendNotice(noticeConfig.getStr("userCode"), toUserCodes, imSysNotice.getImSysNoticeText(), "", imSysNotice.getImSysNoticeName(), imSysNotice.getImSysNoticeImg(), imSysNotice.getImSysNoticeUrl());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }

    }


}