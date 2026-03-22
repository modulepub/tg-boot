package pub.module.im.api.service;

import pub.module.im.curd.entity.ImSysNotice;

/**
 * 系统通知
 * @author tg
 * @since 2025-10-05
 * @version V1.0
 */
public interface BizImSysNoticeService{
    void publish(ImSysNotice imSysNotice) throws Exception;
    void sendNotice(String userCode, String[] toSysUserCodes,String content, String extra, String title, String imageUri, String url) throws Exception;
}
