package pub.module.wx.crud.model;

import lombok.Data;

/**
 * 订阅消息发送日志按模板 ID 分组统计行。
 */
@Data
public class WxMaSubscribeTemplateGroupRow {

    private String templateId;

    private Long sendCount;

    /** 任取一条幂等键，用于解析场景展示名 */
    private String sampleIdempotentKey;
}
