package pub.module.system.curd.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.data.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 用户表 对象
 * @author tg
 * 2026-01-04 13:16:24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "用户表")
public class SysUser extends BaseEntity {
                    /** 业务主键 */
                        @Schema(description = "业务主键")
                private String userCode;

                    /** 用户名 */
                        @Schema(description = "用户名")
                private String userName;

                    /** 密码 */
                        @Schema(description = "密码")
                private String userPassword;

                    /** 密码盐值 */
                        @Schema(description = "密码盐值")
                private String userPasswordSalt;

                    /** 是否锁定 */
                        @Schema(description = "是否锁定")
                private String userLockedCode;

                    /** 是否有效 */
                        @Schema(description = "是否有效")
                private String userEnabledCode;

                    /** OPEN_ID */
                        @Schema(description = "OPEN_ID")
                private String userWxOpenId;

                    /** UNION_ID */
                        @Schema(description = "UNION_ID")
                private String userWxUnionId;

                    /** 手机号 */
                        @Schema(description = "手机号")
                private String userPhone;

                    /** 推荐人 */
                        @Schema(description = "推荐人")
                private String userReferenceCode;

                    /** 用户头像 */
                        @Schema(description = "用户头像")
                private String userAvatar;

                    /** 用户真实姓名 */
                        @Schema(description = "用户真实姓名")
                private String userRealName;

                    /** 用户昵称 */
                        @Schema(description = "用户昵称")
                private String userNickName;

                    /** 用户注册的 APP渠道 */
                        @Schema(description = "用户注册的 APP渠道")
                private String userRegAppChannelCode;

                    /** 用户身份证号 */
                        @Schema(description = "用户身份证号")
                private String userIdCardNum;

                    /** 是否金融用户 */
                        @Schema(description = "是否金融用户")
                private String userFinanceStatusCode;

                    /** 支付密码 */
                        @Schema(description = "支付密码")
                private String userPayPassword;

                    /** 用户所属机构 */
                        @Schema(description = "用户所属机构")
                private String userOrgCode;

                    /** 性别 */
                        @Schema(description = "性别")
                private String userSexCode;


        }
