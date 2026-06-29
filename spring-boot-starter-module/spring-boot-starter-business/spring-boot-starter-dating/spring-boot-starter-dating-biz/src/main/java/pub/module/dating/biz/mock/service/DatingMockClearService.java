package pub.module.dating.biz.mock.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.dating.biz.mock.support.DatingMockClearResult;
import pub.module.dating.crud.entity.DtCusMatchmakerRel;
import pub.module.dating.crud.entity.DtCustomer;
import pub.module.dating.crud.entity.DtMatchmaker;
import pub.module.dating.crud.entity.DtMatchmakingCompany;
import pub.module.dating.crud.service.DtCusMatchmakerRelService;
import pub.module.dating.crud.service.DtCustomerService;
import pub.module.dating.crud.service.DtMatchmakerService;
import pub.module.dating.crud.service.DtMatchmakingCompanyService;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.trade.api.service.ApiTdGoodsService;

import java.util.List;

/**
 * 一键清除标记为测试的 mock 数据（逻辑删除）。
 */
@Slf4j
@Service
public class DatingMockClearService {

    private static final StatusCodeEnum TEST = StatusCodeEnum.YES;

    @Resource
    private DtCusMatchmakerRelService dtCusMatchmakerRelService;
    @Resource
    private ApiTdGoodsService apiTdGoodsService;
    @Resource
    private DtCustomerService dtCustomerService;
    @Resource
    private DtMatchmakerService dtMatchmakerService;
    @Resource
    private DtMatchmakingCompanyService dtMatchmakingCompanyService;
    @Resource
    private ApiSysUserService apiSysUserService;

    @Transactional(rollbackFor = Exception.class)
    public DatingMockClearResult clear() {
        DatingMockClearResult result = new DatingMockClearResult();

        List<DtCusMatchmakerRel> rels = dtCusMatchmakerRelService.lambdaQuery()
                .eq(DtCusMatchmakerRel::getCusMkRelTestStatusCode, TEST)
                .list();
        if (!rels.isEmpty()) {
            dtCusMatchmakerRelService.removeByIds(rels.stream().map(DtCusMatchmakerRel::getId).toList());
        }
        result.setRelationCount(rels.size());

        result.setGoodsCount(apiTdGoodsService.removeTestGoods());

        List<DtCustomer> customers = dtCustomerService.lambdaQuery()
                .eq(DtCustomer::getCusTestStatusCode, TEST)
                .list();
        if (!customers.isEmpty()) {
            dtCustomerService.removeByIds(customers.stream().map(DtCustomer::getId).toList());
        }
        result.setCustomerCount(customers.size());

        List<DtMatchmaker> matchmakers = dtMatchmakerService.lambdaQuery()
                .eq(DtMatchmaker::getMkTestStatusCode, TEST)
                .list();
        if (!matchmakers.isEmpty()) {
            dtMatchmakerService.removeByIds(matchmakers.stream().map(DtMatchmaker::getId).toList());
        }
        result.setMatchmakerCount(matchmakers.size());

        List<DtMatchmakingCompany> companies = dtMatchmakingCompanyService.lambdaQuery()
                .eq(DtMatchmakingCompany::getMkCompanyTestStatusCode, TEST)
                .list();
        if (!companies.isEmpty()) {
            dtMatchmakingCompanyService.removeByIds(companies.stream().map(DtMatchmakingCompany::getId).toList());
        }
        result.setCompanyCount(companies.size());

        result.setUserCount(apiSysUserService.removeTestUsers());
        result.setMessage(String.format(
                "测试数据已清除：公司 %d、红娘 %d、客户 %d、关联 %d、商品 %d、用户 %d",
                result.getCompanyCount(), result.getMatchmakerCount(), result.getCustomerCount(),
                result.getRelationCount(), result.getGoodsCount(), result.getUserCount()));
        log.info("dating mock clear done: {}", result);
        return result;
    }
}
