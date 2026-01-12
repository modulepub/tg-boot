package pub.module.contract.api.service.dto;

import lombok.Data;

@Data
public class UserSignStrikeInputDTO {
    private Integer attachNo;
    private Double offsetX;
    private Double offsetY;
    private Integer clipNumber = 0;
    private String signKey;
    private Integer oddEven;
    private String signPage;
    private Integer crossStyle;
}
