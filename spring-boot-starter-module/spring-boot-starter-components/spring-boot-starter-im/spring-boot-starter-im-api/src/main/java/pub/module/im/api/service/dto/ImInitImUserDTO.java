package pub.module.im.api.service.dto;

import lombok.Data;

/**
 * 用户端-初始化 IM 用户请求
 */
@Data
public class ImInitImUserDTO {

    /**
     * 用户标签，最长 50 字符
     */
    private String tag;
}
