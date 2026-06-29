package pub.module.dating.api.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pub.module.dating.api.service.dto.DtCustomerDTO;

import java.io.Serializable;

/**
 * 用户端客户资料更新消息（同步婚恋模块冗余快照）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtProfileUpdatedMessage implements Serializable {

    private String userCode;
    private DtCustomerDTO customerDto;
}
