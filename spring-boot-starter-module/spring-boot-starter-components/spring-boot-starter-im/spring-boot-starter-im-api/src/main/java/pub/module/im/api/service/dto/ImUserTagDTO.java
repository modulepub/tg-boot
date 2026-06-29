package pub.module.im.api.service.dto;

import lombok.Data;

/**
 * IM 用户标签设置 DTO
 */
@Data
public class ImUserTagDTO {

    /**
     * 用户编码
     */
    private String userCode;

    /**
     * 标签
     */
    private String tag;
}
