package pub.module.dating.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import pub.module.dating.api.service.ApiDtContactService;

import org.springframework.stereotype.Service;
import pub.module.dating.curd.entity.DtContact;
import pub.module.dating.curd.entity.DtContactApply;
import pub.module.dating.curd.service.DtContactApplyService;
import pub.module.dating.curd.service.DtContactService;


/**
 * Api 联系人 Service
 *
 * @author tg
 */
@Service
public class ApiDtContactServiceImpl implements ApiDtContactService {

    @Resource
    DtContactService dtContactService;
    @Resource
    DtContactApplyService dtContactApplyService;

    @Override
    public void remove(RemoveDTO removeDTO) {
        DtContact dtContact = dtContactService.getByCode(removeDTO.getContactCode());
        dtContactService.remove(new QueryWrapper<DtContact>().lambda().eq(DtContact::getContactApplyCode, dtContact.getContactApplyCode()));
        dtContactApplyService.remove(new QueryWrapper<DtContactApply>().lambda().eq(DtContactApply::getContactApplyCode, dtContact.getContactApplyCode()));

    }
}
