package pub.module.im.api.service.dto;

import lombok.Data;

/**
 * 用户端-保存/更新 IM 资料（昵称、头像等）
 */
@Data
public class ImSaveProfileDTO {

    private String nickName;

    private String avatar;

    private String realName;
}
