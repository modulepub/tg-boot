package pub.module.dating.biz.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pub.module.dating.api.service.ApiMatchmakingCompanyRedundantSyncService;
import pub.module.dating.api.service.dto.MatchmakingCompanyRedundantDTO;
import pub.module.dating.crud.entity.DtMatchmaker;
import pub.module.dating.crud.service.DtMatchmakerService;

/**
 * 婚介公司资料变更后，同步婚恋模块各表冗余快照。
 */
@Slf4j
@Service
public class ApiMatchmakingCompanyRedundantSyncServiceImpl implements ApiMatchmakingCompanyRedundantSyncService {

    @Resource
    private DtMatchmakerService dtMatchmakerService;

    @Override
    public void syncAfterCompanyUpdated(MatchmakingCompanyRedundantDTO company) {
        if (company == null) {
            return;
        }
        String companyCode = StrUtil.trim(company.getMkCompanyCode());
        if (StrUtil.isBlank(companyCode)) {
            log.warn("跳过红娘表企业冗余同步：mkCompanyCode 为空");
            return;
        }
        int count = dtMatchmakerService.getBaseMapper().update(null, new LambdaUpdateWrapper<DtMatchmaker>()
                .eq(DtMatchmaker::getMkCompanyCode, companyCode)
                .set(DtMatchmaker::getMkCompanyName, StrUtil.trim(company.getMkCompanyName())));
        log.info("已同步红娘表企业冗余字段 mkCompanyCode={} count={}", companyCode, count);
    }
}
