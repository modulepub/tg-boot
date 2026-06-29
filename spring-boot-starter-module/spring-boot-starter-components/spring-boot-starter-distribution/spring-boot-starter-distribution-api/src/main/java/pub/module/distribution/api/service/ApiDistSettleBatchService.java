package pub.module.distribution.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import pub.module.distribution.api.service.dto.DistSettleBatchDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 结算批次 API。
 */
public interface ApiDistSettleBatchService {

    /**
     * 企业申请结算：创建批次记录，并关联旗下结算明细。
     *
     * @param distPayerUserCodes 纳入本批次的下单付款用户编码（红娘及下级）
     */
    DistSettleBatchDTO apply(String mkCompanyCode, String mkCompanyName, String mkCompanyAdminUserCode,
            String distBizLineCode, BigDecimal distSettleTotalAmount, List<String> distPayerUserCodes);

    /**
     * 按公司分页查询结算批次。
     */
    IPage<DistSettleBatchDTO> pageByCompany(String mkCompanyCode, String distBizLineCode, int pageNo, int pageSize);

    /**
     * 按公司及红娘分页查询结算批次。
     */
    IPage<DistSettleBatchDTO> pageByCompanyAndStaff(String mkCompanyCode, String distMatchmakerUserCode,
            String distBizLineCode, int pageNo, int pageSize);

    /**
     * 按红娘申请结算：创建批次并关联其名下客户结算明细。
     */
    DistSettleBatchDTO applyForStaff(String mkCompanyCode, String mkCompanyName, String mkCompanyAdminUserCode,
            String distMatchmakerUserCode, String distBizLineCode, BigDecimal distSettleTotalAmount,
            List<String> distPayerUserCodes);

    /**
     * 管理端标记批次结算完成（id 与 distSettleBatchCode 二选一）。
     */
    void complete(String id, String distSettleBatchCode);
}
