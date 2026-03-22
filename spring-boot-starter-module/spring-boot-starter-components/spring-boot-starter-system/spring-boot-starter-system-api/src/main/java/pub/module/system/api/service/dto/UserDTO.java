package pub.module.system.api.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "用户管理")
public class UserDTO implements Serializable {
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description= "用户编码")
    private String userCode;
    @Schema(description= "用户名")
    private String userName;
    @Schema(description= "微信OPEN_ID")
    private java.lang.String userWxOpenId;
    @Schema(description= "微信_UNION_ID")
    private java.lang.String userWxUnionId;
    @Schema(description= "手机号")
    private java.lang.String userPhone;
    @Schema(description= "推荐码")
    private java.lang.String userReferenceCode;
    @Schema(description= "头像")
    private java.lang.String userAvatar;
    @Schema(description= "姓名")
    private java.lang.String userRealName;
    @Schema(description= "昵称")
    private java.lang.String userNickName;
    @Schema(description= "用户注册 APP渠道编码")
    private java.lang.String userRegAppChannelCode;
    @Schema(description = "身份证号")
    private String userIdCardNum;
    @Schema(description = "支付密码")
    private String userPayPassword;
    @Schema(description = "性别")
    private String userSexCode;
    @Schema(description = "是否锁定（1，0")
    private String userLockedCode;
    @Schema(description = "是否支持（1，0")
    private String userEnabledCode;
    /** 用户所属机构 */
    @Schema(description = "用户所属机构")
    private String userOrgCode;
}
