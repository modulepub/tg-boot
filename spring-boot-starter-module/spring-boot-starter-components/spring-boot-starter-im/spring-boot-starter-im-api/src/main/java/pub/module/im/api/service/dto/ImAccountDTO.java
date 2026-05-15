package pub.module.im.api.service.dto;

import lombok.Data;

@Data
public class ImAccountDTO {
    /**
     * 用户编码（IM Identifier）
     */
    private String userCode;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 头像地址
     */
    private String avatar;
}
