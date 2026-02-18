package pub.module.finance.curd.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import pub.module.data.api.entity.BaseEntity;

import java.io.Serializable;
/**
 * 金融账户管理
 * @author tg
 * @since 2025-10-09
 * @version V1.0
 */
@Data
@TableName("fc_account")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="fc_account对象")
public class FcAccount extends BaseEntity implements Serializable {
    private String fcAcSysUserCode;
    @Schema(description = "编码")
    private String fcAcCode;
    @Schema(description = "余额")
    private java.math.BigDecimal fcAcBalance;
    @Schema(description = "银行支付渠道")
    private String fcBankPayChannelCode;
    @Schema(description = "银行编码")
    private String fcBankCode;
    @Schema(description = "银行名称")
    private String fcBankName;
    @Schema(description = "银行卡LOGO")
    private String fcBankLogo;
    @Schema(description = "银行卡号")
    private String fcBankCardNo;
    @Schema(description = "银行卡绑定手机号")
    private String fcBankCardPhone;
    @Schema(description = "姓名")
    private String fcAcSysUserRealName;
    @Schema(description = "身份证号")
    private String fcAcIdCardNo;
    @Schema(description = "默认状态")
    private String fcAcDefaultStatusCode;
    @Schema(description = "产品编码")
    private String fcProductCode;
    @Schema(description = "产品类型（详见数据字典）")
    private String fcProductTypeCode;
    @Schema(description = "是否授信（-1未授信、0授信审核中、1已授信、2授信未通过")
    String fcAcCreditStatusCode;
    @Schema(description = "支付状态（0、支付中/放款中）1、正常")
    String fcAcPayStatusCode;
    @Schema(description = "授信编码")
    String fcAcCreditCode;
    @TableField(exist = false)
    @Schema(description = "产品")
    private FcProduct fcProduct;
    @Schema(description = "支付参数")
    private String fcAcPayParam;
    @Schema(description = "绑卡状态（0、绑卡中/失败）1、绑卡成功")
    String fcAcBindCardStatusCode;


}
