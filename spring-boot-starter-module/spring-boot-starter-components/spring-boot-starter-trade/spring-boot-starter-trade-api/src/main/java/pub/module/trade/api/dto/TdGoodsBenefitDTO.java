package pub.module.trade.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 商品权益 DTO
 */
@Data
@Schema(title = "商品权益 TdGoodsBenefitDTO")
public class TdGoodsBenefitDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键 id")
    private String id;

    @Schema(description = "序号")
    private Long seqNo;

    @Schema(description = "商品权益编码")
    private String tdGdBnfCode;

    @Schema(description = "商品编码")
    private String tdGdCode;

    @Schema(description = "权益key")
    private String tdGdBnfKey;

    @Schema(description = "权益值")
    private Long tdGdBnfValue;

    @Schema(description = "权益描述")
    private String tdGdBnfDesc;
}
