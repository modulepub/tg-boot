package pub.module.customer.api.service.dto;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 客户常驻城市选项（编码 + 名称）
 */
@Data
@Schema(description = "客户常驻城市选项")
public class CusCityResidenceOptionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "常驻城市编码")
    private String cusCityResidenceCode;

    @Schema(description = "常驻城市名称")
    private String cusCityResidenceName;
}
