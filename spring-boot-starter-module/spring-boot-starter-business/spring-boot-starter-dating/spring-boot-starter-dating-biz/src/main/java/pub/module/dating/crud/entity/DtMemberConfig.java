package pub.module.dating.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.common.model.po.BaseEntity;

/**
 * 婚恋系统-会员配置（单行配置）
 *
 * @author tg
 */
@Data
@TableName("dt_member_config")
@EqualsAndHashCode(callSuper = false)
@Schema(description = "婚恋系统-会员配置")
public class DtMemberConfig extends BaseEntity {

    /**
     * 注册即赠钻石会员·体验7天 开关（商品编码 freevip，复用客户管理赠送会员核心方法）。
     * 0-关闭，1-开启。
     */
    @Schema(description = "注册即赠会员开关：0-关闭，1-开启")
    private StatusCodeEnum cfgRegisterGiftFreevipStatusCode;
}
