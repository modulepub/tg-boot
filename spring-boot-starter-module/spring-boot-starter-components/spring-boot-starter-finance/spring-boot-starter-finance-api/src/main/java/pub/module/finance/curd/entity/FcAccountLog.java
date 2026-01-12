package pub.module.finance.curd.entity;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;

/**
 * 金融账户变动日志
 *
 * @author tg
 * @version V1.0
 * @since 2025-09-30
 */
@Data
@TableName("fc_account_log")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "fc_account_log对象")
public class FcAccountLog implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private String id;
    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createBy;
    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建日期")
    private java.util.Date createTime;
    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private String updateBy;
    /**
     * 更新日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新日期")
    private java.util.Date updateTime;
    /**
     * 所属部门
     */
    @Schema(description = "所属部门")
    private String sysOrgCode;
    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String fcAcSysUserCode;
    /**
     * 币种
     */
    @Schema(description = "币种")
    private String fcCyCode;
    /**
     * 余额
     */
    @Schema(description = "余额")
    private java.math.BigDecimal fcAcBalance;
    /**
     * 姓名
     */
    @Schema(description = "姓名")
    private String fcAcSysUserRealName;
    /**
     * 编码
     */
    @Schema(description = "编码")
    private String fcAcCode;
    @Schema(description = "产品编码")
    private String fcProductCode;
    /**
     * 货币符号
     */
    @Schema(description = "货币符号")
    private String fcCyIcon;
    /**
     * 账户类型
     */
    @Schema(description = "账户类型")
    private String fcAcType;
    /**
     * 日志编码
     */
    @Schema(description = "日志编码")
    private String fcAcLogCode;
    /**
     * 操作类型
     */
    @Schema(description = "操作类型")
    private String fcAcLogOpType;
    @Schema(description = "支付状态")
    private String fcAcLogPayStatusCode;
    @Schema(description = "支付回调API")
    private String fcAcLogNotifyApi;

    /**
     * 订单号
     */
    @Schema(description = "订单号")
    private String fcAcLogTradeNo;
    /**
     * 余额
     */
    @Schema(description = "交易金额")
    private java.math.BigDecimal fcAcLogAmount;
    /**
     * 订单号
     */
    @Schema(description = "分期期数")
    private Integer fcAcLogPeriod;
    @Schema(description = "回调状态")
    private String fcAcLogNotifyStatusCode;
    @Schema(description = "回调结果")
    private String fcAcLogNotifyResult;
    @Schema(description = "备注")
    private String fcAcLogRemark;
}
