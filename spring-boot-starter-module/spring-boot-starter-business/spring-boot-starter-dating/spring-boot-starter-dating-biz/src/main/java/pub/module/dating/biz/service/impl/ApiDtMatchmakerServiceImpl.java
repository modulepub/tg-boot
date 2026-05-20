package pub.module.dating.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import pub.module.dating.curd.entity.DtMatchmaker;
import pub.module.dating.curd.mapper.DtMatchmakerMapper;
import pub.module.dating.api.service.*;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


/**
 * Api 红娘信息 Service
 *
 * @author tg
 * 2026-03-22 13:32:44
 */
@Service
public class ApiDtMatchmakerServiceImpl implements ApiDtMatchmakerService {

    @Resource
    private DtMatchmakerMapper dtMatchmakerMapper;

    @Override
    public boolean isMatchmakerByUserCode(String userCode) {
        if (userCode == null || userCode.isBlank()) {
            return false;
        }
        return dtMatchmakerMapper.selectCount(new QueryWrapper<DtMatchmaker>().lambda()
                .eq(DtMatchmaker::getMkUserCode, userCode)) > 0;
    }
}
