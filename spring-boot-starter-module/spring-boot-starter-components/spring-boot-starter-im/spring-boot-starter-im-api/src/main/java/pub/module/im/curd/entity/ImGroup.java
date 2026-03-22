package pub.module.im.curd.entity;

import java.io.Serial;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import io.swagger.v3.oas.annotations.media.Schema;
import pub.module.system.api.service.dto.UserDTO;

/**
 * 即时通讯群组
 * @author tg
 * @since 2025-10-05
 * @version V1.0
 */
@Data
@TableName("im_group")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="im_group对象")
public class ImGroup implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private java.lang.String id;
    /**创建人*/
    @Schema(description = "创建人")
    private java.lang.String createBy;
    /**创建日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建日期")
    private java.util.Date createTime;
    /**更新人*/
    @Schema(description = "更新人")
    private java.lang.String updateBy;
    /**更新日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新日期")
    private java.util.Date updateTime;
    /**所属部门*/
    @Schema(description = "所属部门")
    private java.lang.String sysOrgCode;
    /**群组类型（0系统通知，在线客服1，在线客服2……）*/
    @Schema(description = "群组类型（0系统通知，在线客服1，在线客服2……）")
    private java.lang.String imGroupTypeCode;
    /**群组编码*/
    @Schema(description = "群组编码")
    private java.lang.String imGroupCode;
    /**群组名称*/
    @Schema(description = "群组名称")
    private java.lang.String imGroupName;
    /**归属人用户编码*/
    @Schema(description = "归属人用户编码")
    private java.lang.String imGroupBelongSysUserCode;
    /**群组头图*/
    @Schema(description = "群组头图")
    private java.lang.String imGroupHeadImg;
    /**群组工作状态(离线、繁忙……)*/
    @Schema(description = "群组工作状态(离线、繁忙……)")
    private java.lang.String imGroupWorkStatusCode;
    @TableField(exist = false)
    UserDTO imGroupBelongSysUser;
}
