package pub.module.dating.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import pub.module.common.enums.BaseEntityFiled;
import pub.module.customer.api.service.ApiCustomerService;
import pub.module.customer.api.service.dto.CustomerDTO;
import pub.module.dating.api.constants.RelationPassedStatusCodeEnum;
import pub.module.dating.api.service.dto.DtContactApplyDTO;
import pub.module.dating.curd.entity.DtContact;
import pub.module.dating.curd.entity.DtContactApply;
import pub.module.dating.curd.service.DtContactApplyService;
import pub.module.dating.api.service.*;

import org.springframework.stereotype.Service;
import pub.module.dating.curd.service.DtContactService;
import pub.module.im.api.service.ApiImService;
import pub.module.im.api.service.dto.ImAddFriendDTO;


/**
 * Api 联系人申请表 Service
 *
 * @author tg
 * 2026-05-03 03:39:43
 */
@Service
public class ApiDtContactApplyServiceImpl implements ApiDtContactApplyService {

    @Resource
    ApiCustomerService apiCustomerService;
    @Resource
    DtContactApplyService dtContactApplyService;
    @Resource
    DtContactService dtContactService;
    @Resource
    ApiImService apiImService;

    @Override
    public void apply(ApplyDTO applyDTO, String userCode) {
        CustomerDTO target = apiCustomerService.getCusByCusCode(applyDTO.getCusCode());
        CustomerDTO applicant = apiCustomerService.getCusByUserCode(userCode);
        DtContactApply row = BeanUtil.copyProperties(target, DtContactApply.class, BaseEntityFiled.NAMES);
        row.setCusUserCode(target.getCusUserCode());
        row.setUserCode(userCode);
        row.setContactApplyGreeting(applyDTO.getContactApplyGreeting());
        row.setContactApplySourceCode(applyDTO.getContactSourceCode());
        fillApplicantSnapshot(applicant, row);
        dtContactApplyService.save(row);
    }

    private static void fillApplicantSnapshot(CustomerDTO applicant, DtContactApply row) {
        if (applicant == null) {
            return;
        }
        row.setAppCusCode(applicant.getCusCode());
        row.setAppCusAvatar(applicant.getCusAvatar());
        row.setAppCusName(applicant.getCusName());
        row.setAppCusSexCode(applicant.getCusSexCode());
        row.setAppCusAge(applicant.getCusAge());
        row.setAppCusKinshipCode(applicant.getCusKinshipCode());
        row.setAppCusCityResidenceName(applicant.getCusCityResidenceName());
        row.setAppCusMoment(applicant.getCusMoment());
        row.setAppCusPhone(applicant.getCusPhone());
    }

    @Override
    public DtContactApplyDTO check(CheckDTO checkDTO, String userCode) {
        QueryWrapper<DtContactApply> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DtContactApply::getCusCode, checkDTO.getCusCode());
        queryWrapper.lambda().eq(DtContactApply::getUserCode, userCode);
        DtContactApply apply = dtContactApplyService.getOne(queryWrapper, false);
        if (apply != null) {
            return BeanUtil.copyProperties(apply, DtContactApplyDTO.class);
        }else {
            CustomerDTO customerDTOA = apiCustomerService.getCusByCusCode(checkDTO.getCusCode());//对方信息
            long isFriend = dtContactService.count(new QueryWrapper<DtContact>().lambda()
                    .eq(DtContact::getUserCode,userCode)
                    .eq(DtContact::getCusCode,customerDTOA.getCusCode())
            );
            if(isFriend>0){
                DtContactApplyDTO dtContactApply = new DtContactApplyDTO();
                dtContactApply.setContactApplyPassedStatusCode("1");
                return dtContactApply;
            }
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void pass(PassDTO passDTO) {
        //己方查询
        DtContactApply apply = dtContactApplyService.getByCode(passDTO.getContactApplyCode());
        CustomerDTO customerDTOA = apiCustomerService.getCusByCusCode(apply.getCusCode());//A
        DtContact dtContactA = BeanUtil.copyProperties(customerDTOA, DtContact.class, BaseEntityFiled.NAMES);
        dtContactA.setCusUserCode(customerDTOA.getCusUserCode());
        dtContactA.setUserCode(apply.getUserCode());//B
        dtContactA.setContactApplyCode(apply.getContactApplyCode());
        dtContactA.setContactSourceCode(apply.getContactApplySourceCode());
        dtContactService.save(dtContactA);
        //对方查询
        CustomerDTO customerDTOB = apiCustomerService.getCusByUserCode(apply.getUserCode());//B
        DtContact dtContactB = BeanUtil.copyProperties(customerDTOB, DtContact.class, BaseEntityFiled.NAMES);
        dtContactB.setCusUserCode(customerDTOB.getCusUserCode());
        dtContactB.setUserCode(customerDTOA.getCusUserCode());//A
        dtContactB.setContactSourceCode(apply.getContactApplySourceCode());
        dtContactB.setContactApplyCode(apply.getContactApplyCode());
        dtContactService.save(dtContactB);

        apply.setContactApplyPassedStatusCode(RelationPassedStatusCodeEnum.YES);
        ImAddFriendDTO imAddFriendDTO = new ImAddFriendDTO();
        imAddFriendDTO.setFromUserCode(apply.getUserCode());
        imAddFriendDTO.setToUserCode(customerDTOA.getCusUserCode());
        imAddFriendDTO.setAddWording(apply.getContactApplyGreeting());
        apiImService.addFriend(imAddFriendDTO);
        dtContactApplyService.updateById(apply);
    }

    @Override
    public void reject(RejectDTO rejectDTO) {
        Assert.notNull(rejectDTO, "参数不能为空");
        DtContactApply entity = dtContactApplyService.getByCode(rejectDTO.getContactApplyCode());
        Assert.notNull(entity, "联系人不存在");
        entity.setContactApplyPassedStatusCode(RelationPassedStatusCodeEnum.REJECTED);
        dtContactApplyService.updateById(entity);
    }

}
