package pub.module.system.crud.entity;

import pub.module.common.enums.StatusCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import pub.module.system.api.constants.UserEnabledCodeEnum;
import pub.module.system.api.constants.UserLockedCodeEnum;

/**
 * 用户表 对象
 *
 * @author tg
 * 2026-01-04 13:16:24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "用户表")
public class SysUser extends BaseEntity {
    @Schema(description = "业务主键")
    private String userCode;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "密码")
    private String userPassword;

    @Schema(description = "密码盐值")
    private String userPasswordSalt;

    @Schema(description = "是否锁定")
    private UserLockedCodeEnum userLockedCode;

    @Schema(description = "是否有效")
    private UserEnabledCodeEnum userEnabledCode;

    @Schema(description = "OPEN_ID")
    private String userWxOpenId;

    @Schema(description = "UNION_ID")
    private String userWxUnionId;

    @Schema(description = "手机号")
    private String userPhone;

    @Schema(description = "推荐人")
    private String userReferenceCode;

    @Schema(description = "分享人用户编码")
    private String userReferenceUserCode;

    @Schema(description = "用户头像")
    private String userAvatar;

    @Schema(description = "用户真实姓名")
    private String userRealName;

    @Schema(description = "实名认证状态")
    private StatusCodeEnum userIdentityAuthenticatedStatusCode;

    @Schema(description = "用户昵称")
    private String userNickName;

    @Schema(description = "用户来源")
    private String userSourceCode;

    @Schema(description = "支付密码")
    private String userPayPassword;

    @Schema(description = "用户所属机构")
    private String userOrgCode;

    @Schema(description = "是否在线")
    private StatusCodeEnum userOlineStatusCode;

    @Schema(description = "IM同步状态")
    private StatusCodeEnum userImSynStatusCode;

    @Schema(description = "是否测试数据（StatusCode：1是 0否）")
    private StatusCodeEnum userTestStatusCode;

    @Schema(description = "是否限制登录（StatusCode：1限制 0不限制）")
    private StatusCodeEnum userLoginRestrictStatusCode;

}
