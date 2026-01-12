package pub.module.im.curd.entity;

import java.io.Serial;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import io.swagger.v3.oas.annotations.media.Schema;
/**
 * 系统通知
 * @author tg
 * @since 2025-10-05
 * @version V1.0
 */
@Data
@TableName("im_sys_notice")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="im_sys_notice对象")
public class ImSysNotice implements Serializable {
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
    /**通知编码*/
    @Schema(description = "通知编码")
    private java.lang.String imSysNoticeCode;
    /**通知标题*/
    @Schema(description = "通知标题")
    private java.lang.String imSysNoticeName;
    /**通知文本*/
    @Schema(description = "通知文本")
    private java.lang.String imSysNoticeText;
    /**通知图片*/
    @Schema(description = "通知图片")
    private java.lang.String imSysNoticeImg;
    /**跳转链接*/
    @Schema(description = "跳转链接")
    private java.lang.String imSysNoticeUrl;
    /**是否发布（1发布,0未发布）*/
    @Schema(description = "是否发布（1发布,0未发布）")
    private java.lang.String imSysNoticePublishStateCode;
    /**发送对象(1角色，2用户)*/
    @Schema(description = "发送对象")
    private java.lang.Integer imSysSendCode;
    /**是否短信通知（1是。0否）*/
    @Schema(description = "短信通知")
    private java.lang.Integer imSysSmsCode;

}
