package pub.module.contract.api.service.dto;

import lombok.Data;

import java.awt.*;

@Data
public class UserSignStrategyInputDTO {
    private Integer attachNo;
    private Integer versionKey;
    private String sealNo;
    private Long contractUserId;
    private Integer locationMode;
    private String signKey;
    private Integer crossTo;
    private Boolean isCross;
    private Integer crossFrom;
    private String signatrueName;
    private Integer signType;
    private Integer signPage;
    private Double signX;
    private Float sealWidth;
    private Float sealHeight;
    private Integer clipNumber = 0;
    private Double signY;
    private Double offsetX;
    private Double offsetY;
    private Integer unions;
    private Integer lineSpace;
    private Integer lineNum;
    private Integer canDrag;
}
