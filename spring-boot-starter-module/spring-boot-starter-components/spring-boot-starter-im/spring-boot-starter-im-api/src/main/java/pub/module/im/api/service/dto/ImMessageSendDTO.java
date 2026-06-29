package pub.module.im.api.service.dto;

import lombok.Data;

/**
 * IM 消息发送请求
 */
@Data
public class ImMessageSendDTO {

    /**
     * 接收方用户编码
     */
    private String toUserCode;

    /**
     * 消息类型：text / rich
     */
    private String typeCode;

    /**
     * 文本内容（text类型必填）
     */
    private String content;

    /**
     * 图文标题（rich类型必填）
     */
    private String title;

    /**
     * 图文图片地址（rich类型必填）
     */
    private String imageUrl;

    /**
     * 图文跳转链接（rich类型可选）
     */
    private String linkUrl;
}
