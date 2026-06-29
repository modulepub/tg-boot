package pub.module.im.api.service;

/**
 * IM 全员通知
 */
public interface ApiImNoticeService {

    /**
     * 向 im_user 全员发送图文 IM 消息，并更新通知状态
     *
     * @param noticeId 通知主键 id
     * @return 成功发送人数
     */
    int publishAndBroadcast(String noticeId);
}
