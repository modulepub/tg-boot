package pub.module.dating.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import pub.module.common.enums.BaseEntityFiled;
import pub.module.dating.api.constants.CusMemberBenefitTypeEnum;
import pub.module.dating.api.service.ApiDtCustomerService;
import pub.module.dating.api.service.dto.MemberBenefitConsumeResultDTO;
import pub.module.dating.biz.util.DatingMemberBenefitConsumeUtil;
import pub.module.dating.api.service.dto.DtCustomerDTO;
import pub.module.dating.api.constants.ContactApplySourceCodeEnum;
import pub.module.dating.api.constants.ContactSourceCodeEnum;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.dating.api.service.dto.DtContactApplyDTO;
import pub.module.dating.crud.entity.DtContact;
import pub.module.dating.crud.entity.DtContactApply;
import pub.module.dating.crud.service.DtContactApplyService;
import pub.module.dating.api.service.*;

import org.springframework.stereotype.Service;
import pub.module.dating.biz.messaging.DatingWxSubscribeNotifyPublisher;
import pub.module.dating.crud.service.DtContactService;
import pub.module.im.api.service.ApiImService;
import pub.module.im.api.service.dto.ImAddFriendDTO;
import pub.module.system.api.constants.SysUserBadgeKeyEnum;
import pub.module.system.api.service.ApiSysUserBadgeService;


/**
 * Api 联系人申请表 Service
 *
 * @author tg
 * 2026-05-03 03:39:43
 */
@Service
public class ApiDtContactApplyServiceImpl implements ApiDtContactApplyService {

    private static final String MUTUAL_LIKE_GREETING = "相互喜欢，已成为好友";

    @Resource
    ApiDtCustomerService apiDtCustomerService;
    @Resource
    DtContactApplyService dtContactApplyService;
    @Resource
    DtContactService dtContactService;
    @Resource
    ApiImService apiImService;
    @Resource
    DatingWxSubscribeNotifyPublisher datingWxSubscribeNotifyPublisher;
    @Resource
    ApiSysUserBadgeService apiSysUserBadgeService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void apply(ApplyDTO applyDTO, String userCode) {
        ContactSourceCodeEnum source = applyDTO.getContactSourceCode();
        if (source == ContactSourceCodeEnum.FRIEND_REQUEST) {
            MemberBenefitConsumeResultDTO consumeResult = apiDtCustomerService.tryConsumeMemberBenefit(
                    userCode, CusMemberBenefitTypeEnum.ADD_FRIEND, 1L, applyDTO.getCusCode());
            DatingMemberBenefitConsumeUtil.assertConsumed(consumeResult);
        }
        DtCustomerDTO target = apiDtCustomerService.getCusByCusCode(applyDTO.getCusCode());
        DtCustomerDTO applicant = apiDtCustomerService.getCusByUserCode(userCode);
        DtContactApply row = BeanUtil.copyProperties(target, DtContactApply.class, BaseEntityFiled.NAMES);
        row.setCusUserCode(target.getCusUserCode());
        row.setUserCode(userCode);
        row.setContactApplyGreeting(applyDTO.getContactApplyGreeting());
        row.setContactApplySourceCode(toContactApplySource(applyDTO.getContactSourceCode()));
        fillApplicantSnapshot(applicant, row);
        dtContactApplyService.save(row);
        datingWxSubscribeNotifyPublisher.publishFriendRequestAfterCommit(row);
        incrementContactBadge(target.getCusUserCode());
    }

    private static void fillApplicantSnapshot(DtCustomerDTO applicant, DtContactApply row) {
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
            DtCustomerDTO customerDtoA = apiDtCustomerService.getCusByCusCode(checkDTO.getCusCode());//对方信息
            long isFriend = dtContactService.count(new QueryWrapper<DtContact>().lambda()
                    .eq(DtContact::getUserCode,userCode)
                    .eq(DtContact::getCusCode,customerDtoA.getCusCode())
            );
            if(isFriend>0){
                DtContactApplyDTO dtContactApply = new DtContactApplyDTO();
                dtContactApply.setContactApplyPassedStatusCode(StatusCodeEnum.YES);
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
        DtCustomerDTO customerDtoA = apiDtCustomerService.getCusByCusCode(apply.getCusCode());//A
        DtCustomerDTO customerDtoB = apiDtCustomerService.getCusByUserCode(apply.getUserCode());//B
        DtContact dtContactA = BeanUtil.copyProperties(customerDtoA, DtContact.class, BaseEntityFiled.NAMES);
        dtContactA.setCusUserCode(customerDtoA.getCusUserCode());
        dtContactA.setUserCode(apply.getUserCode());//B
        dtContactA.setContactApplyCode(apply.getContactApplyCode());
        dtContactA.setContactSourceCode(toContactSource(apply.getContactApplySourceCode()));
        // 列表 Tab 按归属用户身份过滤，冗余字段存归属方身份而非对方快照
        dtContactA.setCusKinshipCode(customerDtoB.getCusKinshipCode());
        dtContactService.save(dtContactA);
        //对方查询
        DtContact dtContactB = BeanUtil.copyProperties(customerDtoB, DtContact.class, BaseEntityFiled.NAMES);
        dtContactB.setCusUserCode(customerDtoB.getCusUserCode());
        dtContactB.setUserCode(customerDtoA.getCusUserCode());//A
        dtContactB.setContactSourceCode(toContactSource(apply.getContactApplySourceCode()));
        dtContactB.setContactApplyCode(apply.getContactApplyCode());
        dtContactB.setCusKinshipCode(customerDtoA.getCusKinshipCode());
        dtContactService.save(dtContactB);

        apply.setContactApplyPassedStatusCode(StatusCodeEnum.YES);
        ImAddFriendDTO imAddFriendDTO = new ImAddFriendDTO();
        imAddFriendDTO.setFromUserCode(apply.getUserCode());
        imAddFriendDTO.setToUserCode(customerDtoA.getCusUserCode());
        imAddFriendDTO.setAddWording(apply.getContactApplyGreeting());
        apiImService.addFriend(imAddFriendDTO);
        dtContactApplyService.updateById(apply);
        datingWxSubscribeNotifyPublisher.publishFriendAddSuccessAfterCommit(apply);
        incrementContactBadge(apply.getUserCode());
        incrementContactBadge(customerDtoA.getCusUserCode());
    }

    private static ContactApplySourceCodeEnum toContactApplySource(ContactSourceCodeEnum source) {
        return source == null ? null : ContactApplySourceCodeEnum.fromJson(source.getCode());
    }

    private static ContactSourceCodeEnum toContactSource(ContactApplySourceCodeEnum source) {
        return source == null ? null : ContactSourceCodeEnum.fromJson(source.getCode());
    }

    @Override
    public void reject(RejectDTO rejectDTO) {
        Assert.notNull(rejectDTO, "参数不能为空");
        DtContactApply entity = dtContactApplyService.getByCode(rejectDTO.getContactApplyCode());
        Assert.notNull(entity, "联系人不存在");
        entity.setContactApplyPassedStatusCode(StatusCodeEnum.NO);
        dtContactApplyService.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void ensureMutualLikeContacts(String cusCodeA, String cusCodeB) {
        if (StrUtil.hasBlank(cusCodeA, cusCodeB) || StrUtil.equals(cusCodeA, cusCodeB)) {
            return;
        }
        DtCustomerDTO customerA = apiDtCustomerService.getCusByCusCode(cusCodeA);
        DtCustomerDTO customerB = apiDtCustomerService.getCusByCusCode(cusCodeB);
        if (customerA == null || customerB == null) {
            return;
        }
        String userCodeA = StrUtil.trim(customerA.getCusUserCode());
        String userCodeB = StrUtil.trim(customerB.getCusUserCode());
        if (StrUtil.hasBlank(userCodeA, userCodeB)) {
            return;
        }
        ensureMutualContactsByUserCode(userCodeA, userCodeB, ContactSourceCodeEnum.MUTUAL_LIKE, MUTUAL_LIKE_GREETING);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void ensureMutualContactsByUserCode(
            String userCodeA,
            String userCodeB,
            ContactSourceCodeEnum source,
            String greeting) {
        String normalizedA = StrUtil.trim(userCodeA);
        String normalizedB = StrUtil.trim(userCodeB);
        if (StrUtil.hasBlank(normalizedA, normalizedB) || StrUtil.equals(normalizedA, normalizedB)) {
            return;
        }
        DtCustomerDTO customerA = apiDtCustomerService.getCusByUserCode(normalizedA);
        DtCustomerDTO customerB = apiDtCustomerService.getCusByUserCode(normalizedB);
        if (customerA == null || customerB == null) {
            return;
        }

        boolean contactAToB = hasContact(normalizedA, customerB.getCusCode());
        boolean contactBToA = hasContact(normalizedB, customerA.getCusCode());
        if (contactAToB && contactBToA) {
            return;
        }

        ContactApplySourceCodeEnum applySource = toContactApplySource(source);
        String greet = StrUtil.blankToDefault(greeting, MUTUAL_LIKE_GREETING);

        DtContactApply apply = findApplyBetween(normalizedA, customerB.getCusCode(), normalizedB, customerA.getCusCode());
        if (apply == null) {
            apply = buildApplyBetween(customerA, customerB, applySource, greet);
            dtContactApplyService.save(apply);
        }
        else {
            apply.setContactApplySourceCode(applySource);
            apply.setContactApplyPassedStatusCode(StatusCodeEnum.YES);
            if (StrUtil.isBlank(apply.getContactApplyGreeting())) {
                apply.setContactApplyGreeting(greet);
            }
            dtContactApplyService.updateById(apply);
        }

        String applyCode = apply.getContactApplyCode();
        if (!contactAToB) {
            saveContactForOwner(customerB, normalizedA, customerA, applyCode, source);
            incrementContactBadge(normalizedA);
        }
        if (!contactBToA) {
            saveContactForOwner(customerA, normalizedB, customerB, applyCode, source);
            incrementContactBadge(normalizedB);
        }

        ImAddFriendDTO imAddFriendDTO = new ImAddFriendDTO();
        imAddFriendDTO.setFromUserCode(normalizedA);
        imAddFriendDTO.setToUserCode(normalizedB);
        imAddFriendDTO.setAddWording(greet);
        apiImService.addFriend(imAddFriendDTO);
        datingWxSubscribeNotifyPublisher.publishFriendAddSuccessAfterCommit(apply);
    }

    private void incrementContactBadge(String userCode) {
        if (StrUtil.isBlank(userCode)) {
            return;
        }
        apiSysUserBadgeService.incrementBadgeCount(
                userCode.trim(),
                SysUserBadgeKeyEnum.ME_CONTACT.getCode(),
                1);
    }

    private boolean hasContact(String ownerUserCode, String peerCusCode) {
        if (StrUtil.hasBlank(ownerUserCode, peerCusCode)) {
            return false;
        }
        return dtContactService.count(new QueryWrapper<DtContact>().lambda()
                .eq(DtContact::getUserCode, ownerUserCode)
                .eq(DtContact::getCusCode, peerCusCode)) > 0;
    }

    private DtContactApply findApplyBetween(String userCodeA, String cusCodeB, String userCodeB, String cusCodeA) {
        DtContactApply apply = dtContactApplyService.getOne(new QueryWrapper<DtContactApply>().lambda()
                .eq(DtContactApply::getUserCode, userCodeA)
                .eq(DtContactApply::getCusCode, cusCodeB)
                .orderByDesc(DtContactApply::getCreateTime), false);
        if (apply != null) {
            return apply;
        }
        return dtContactApplyService.getOne(new QueryWrapper<DtContactApply>().lambda()
                .eq(DtContactApply::getUserCode, userCodeB)
                .eq(DtContactApply::getCusCode, cusCodeA)
                .orderByDesc(DtContactApply::getCreateTime), false);
    }

    private DtContactApply buildMutualLikeApply(DtCustomerDTO applicant, DtCustomerDTO target) {
        return buildApplyBetween(applicant, target, ContactApplySourceCodeEnum.MUTUAL_LIKE, MUTUAL_LIKE_GREETING);
    }

    private DtContactApply buildApplyBetween(
            DtCustomerDTO applicant,
            DtCustomerDTO target,
            ContactApplySourceCodeEnum source,
            String greeting) {
        DtContactApply row = BeanUtil.copyProperties(target, DtContactApply.class, BaseEntityFiled.NAMES);
        row.setCusUserCode(target.getCusUserCode());
        row.setUserCode(applicant.getCusUserCode());
        row.setContactApplyGreeting(greeting);
        row.setContactApplySourceCode(source);
        row.setContactApplyPassedStatusCode(StatusCodeEnum.YES);
        fillApplicantSnapshot(applicant, row);
        return row;
    }

    private void saveContactForOwner(
            DtCustomerDTO peer,
            String ownerUserCode,
            DtCustomerDTO ownerCustomer,
            String applyCode,
            ContactSourceCodeEnum source) {
        DtContact row = BeanUtil.copyProperties(peer, DtContact.class, BaseEntityFiled.NAMES);
        row.setCusUserCode(peer.getCusUserCode());
        row.setUserCode(ownerUserCode);
        row.setContactApplyCode(applyCode);
        row.setContactSourceCode(source);
        row.setCusKinshipCode(ownerCustomer.getCusKinshipCode());
        dtContactService.save(row);
    }

}
