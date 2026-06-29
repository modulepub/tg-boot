package pub.module.dating.api.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pub.module.dating.api.service.dto.MatchmakingCompanyRedundantDTO;

import java.io.Serializable;

/**
 * 婚介公司资料更新消息（同步婚恋模块冗余快照）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtMatchmakingCompanyUpdatedMessage implements Serializable {

    private MatchmakingCompanyRedundantDTO companyDto;
}
