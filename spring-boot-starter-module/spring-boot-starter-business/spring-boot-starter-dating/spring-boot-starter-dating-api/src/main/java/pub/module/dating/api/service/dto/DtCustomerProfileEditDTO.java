package pub.module.dating.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 客户资料编辑记录 DTO（字段同客户，附加每项审核结果与提示）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "客户资料编辑记录")
public class DtCustomerProfileEditDTO extends DtCustomerDTO {

    @Schema(description = "各字段审核结果（key 为 Java 属性名）")
    private Map<String, ProfileFieldAuditDTO> fieldAudits = new LinkedHashMap<>();
}
