package pub.module.dating.biz.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.dating.api.service.ApiDtCustomerService;
import pub.module.dating.api.service.dto.DtCustomerDTO;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.dating.api.service.ApiDtCusMatchmakerRelService;
import pub.module.dating.api.service.dto.CusMkRelShowStatusUpdateDTO;
import pub.module.dating.crud.entity.DtCusMatchmakerRel;
import pub.module.dating.crud.entity.DtMatchmaker;
import pub.module.dating.crud.service.DtCusMatchmakerRelService;
import pub.module.dating.crud.service.DtMatchmakerService;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;

/**
 * Api 客户红娘关系 Service
 *
 * @author tg
 * 2026-03-25 00:36:20
 */
@Slf4j
@Service
public class ApiDtCusMatchmakerRelServiceImpl implements ApiDtCusMatchmakerRelService {

    @Resource
    private DtCusMatchmakerRelService dtCusMatchmakerRelService;
    @Resource
    private ApiDtCustomerService apiDtCustomerService;
    @Resource
    private DtMatchmakerService dtMatchmakerService;
    @Resource
    private ApiSysUserService apiSysUserService;

    @Override
    public void updateCusMkRelShowStatus(CusMkRelShowStatusUpdateDTO dto) {
        Assert.notNull(dto, "参数不能为空");
        Assert.notBlank(dto.getCusMkRelCode(), "cusMkRelCode不能为空");
        StatusCodeEnum statusCode = dto.getCusMkRelShowStatusCode();
        Assert.notNull(statusCode, "展示状态不能为空");

        UserDTO userDTO = UserUtil.getCurrentSysUser();
        DtCustomerDTO customerDto = apiDtCustomerService.getCusByUserCode(userDTO.getUserCode());
        Assert.notNull(customerDto, "客户信息不存在");
        Assert.notBlank(customerDto.getCusCode(), "客户编码不存在");

        boolean updated = dtCusMatchmakerRelService.lambdaUpdate()
                .eq(DtCusMatchmakerRel::getCusMkRelCode, dto.getCusMkRelCode())
                .eq(DtCusMatchmakerRel::getCusCode, customerDto.getCusCode())
                .set(DtCusMatchmakerRel::getCusMkRelShowStatusCode, statusCode)
                .update();
        Assert.isTrue(updated, "更新失败：记录不存在或无权操作");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void relateCustomerWithMatchmakerIfAbsent(String orderBuyerUserCode, String goodsSupplierUserCode) {
        Assert.notBlank(orderBuyerUserCode, "下单用户不能为空");
        Assert.notBlank(goodsSupplierUserCode, "商品供应商不能为空");
        if (StrUtil.equals(orderBuyerUserCode, goodsSupplierUserCode)) {
            log.debug("skip cus-matchmaker rel: buyer and supplier are the same user");
            return;
        }
        DtCustomerDTO customer = apiDtCustomerService.getCusByUserCode(orderBuyerUserCode);
        Assert.notNull(customer, "客户不存在");
        Assert.notBlank(customer.getCusCode(), "客户编码不存在");

        DtMatchmaker matchmaker = dtMatchmakerService.getOne(new QueryWrapper<DtMatchmaker>().lambda()
                .eq(DtMatchmaker::getMkUserCode, goodsSupplierUserCode), false);
        Assert.notNull(matchmaker, "商品供应商不是已登记的红娘");
        Assert.notBlank(matchmaker.getMkCode(), "红娘编码不存在");

        insertCusMatchmakerRelIfAbsent(customer, matchmaker, goodsSupplierUserCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void relateCustomerWithMatchmakerByMkCodeIfAbsent(String customerUserCode, String mkCode) {
        Assert.notBlank(customerUserCode, "用户不能为空");
        Assert.notBlank(mkCode, "红娘编码不能为空");
        DtCustomerDTO customer = apiDtCustomerService.getCusByUserCode(customerUserCode);
        Assert.notNull(customer, "客户不存在");
        Assert.notBlank(customer.getCusCode(), "客户编码不存在");

        DtMatchmaker matchmaker = dtMatchmakerService.getByCode(mkCode);
        Assert.notNull(matchmaker, "红娘不存在");
        Assert.notBlank(matchmaker.getMkCode(), "红娘编码不存在");
        if (StrUtil.isNotBlank(matchmaker.getMkUserCode()) && StrUtil.equals(customerUserCode, matchmaker.getMkUserCode())) {
            log.debug("skip cus-matchmaker rel: applicant user is the matchmaker account");
            return;
        }

        insertCusMatchmakerRelIfAbsent(customer, matchmaker, null);
    }

    /**
     * 同一客户+红娘仅插入一条；{@code fallbackMkUserCode} 在库中红娘未填 {@code mk_user_code} 时用于落库（如支付回调场景）。
     */
    private void insertCusMatchmakerRelIfAbsent(DtCustomerDTO customer, DtMatchmaker matchmaker, String fallbackMkUserCode) {
        long existed = dtCusMatchmakerRelService.lambdaQuery()
                .eq(DtCusMatchmakerRel::getCusCode, customer.getCusCode())
                .eq(DtCusMatchmakerRel::getMkCode, matchmaker.getMkCode())
                .count();
        if (existed > 0) {
            dtCusMatchmakerRelService.lambdaUpdate()
                    .eq(DtCusMatchmakerRel::getCusCode, customer.getCusCode())
                    .eq(DtCusMatchmakerRel::getMkCode, matchmaker.getMkCode())
                    .set(DtCusMatchmakerRel::getCusName, customer.getCusName())
                    .set(DtCusMatchmakerRel::getCusNickName, StrUtil.trimToNull(customer.getCusNickName()))
                    .set(DtCusMatchmakerRel::getCusIdentityAuthenticatedStatusCode,
                            customer.getCusIdentityAuthenticatedStatusCode())
                    .set(DtCusMatchmakerRel::getCusAvatar, customer.getCusAvatar())
                    .set(DtCusMatchmakerRel::getCusSexCode, customer.getCusSexCode())
                    .set(DtCusMatchmakerRel::getCusMoment, customer.getCusMoment())
                    .set(DtCusMatchmakerRel::getCusHiddenStatusCode, customer.getCusHiddenStatusCode())
                    .set(DtCusMatchmakerRel::getCusPhone, StrUtil.trimToNull(customer.getCusPhone()))
                    .update();
            log.debug("refresh cus-matchmaker rel snapshot: cusCode={}, mkCode={}", customer.getCusCode(), matchmaker.getMkCode());
            return;
        }

        DtCusMatchmakerRel rel = new DtCusMatchmakerRel();
        rel.setCusCode(customer.getCusCode());
        fillCustomerSnapshot(rel, customer);
        rel.setCusMkRelShowStatusCode(StatusCodeEnum.YES);

        rel.setMkCode(matchmaker.getMkCode());
        rel.setMkUserCode(StrUtil.blankToDefault(matchmaker.getMkUserCode(), fallbackMkUserCode));
        rel.setMkWorkPhoto(matchmaker.getMkWorkPhoto());
        rel.setMkName(matchmaker.getMkName());
        rel.setMkIdNo(matchmaker.getMkIdNo());
        rel.setMkCompanyCode(matchmaker.getMkCompanyCode());
        rel.setMkCompanyName(matchmaker.getMkCompanyName());
        rel.setMkCityCode(matchmaker.getMkCityCode());
        rel.setMkCityName(matchmaker.getMkCityName());
        rel.setMkMoment(matchmaker.getMkMoment());
        rel.setMkIdentityStatusCode(matchmaker.getMkIdentityStatusCode());
        rel.setMkScore(matchmaker.getMkScore());
        rel.setMkPhone(matchmaker.getMkPhone());

        dtCusMatchmakerRelService.save(rel);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void followMatchmakerByMkCode(String mkCode) {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        Assert.notBlank(userDTO.getUserCode(), "用户未登录");
        DtMatchmaker matchmaker = dtMatchmakerService.getByCode(mkCode);
        Assert.notNull(matchmaker, "红娘不存在");
        relateCustomerWithMatchmakerByMkCodeIfAbsent(userDTO.getUserCode(), mkCode);
        if (StrUtil.isNotBlank(matchmaker.getMkUserCode())) {
            apiSysUserService.setReferenceUserCodeIfAbsent(userDTO.getUserCode(), matchmaker.getMkUserCode());
        }
    }

    @Override
    public boolean isFollowedMatchmaker(String mkCode) {
        Assert.notBlank(mkCode, "红娘编码不能为空");
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        DtCustomerDTO customer = apiDtCustomerService.getCusByUserCode(userDTO.getUserCode());
        if (customer == null || StrUtil.isBlank(customer.getCusCode())) {
            return false;
        }
        return dtCusMatchmakerRelService.lambdaQuery()
                .eq(DtCusMatchmakerRel::getCusCode, customer.getCusCode())
                .eq(DtCusMatchmakerRel::getMkCode, mkCode.trim())
                .count() > 0;
    }

    /** 从客户表快照嘉宾展示字段（昵称、实名、头像等）写入关系表冗余列 */
    public static void fillCustomerSnapshot(DtCusMatchmakerRel rel, DtCustomerDTO customer) {
        if (rel == null || customer == null) {
            return;
        }
        rel.setCusName(customer.getCusName());
        rel.setCusNickName(StrUtil.trimToNull(customer.getCusNickName()));
        rel.setCusIdentityAuthenticatedStatusCode(customer.getCusIdentityAuthenticatedStatusCode());
        rel.setCusAvatar(customer.getCusAvatar());
        rel.setCusSexCode(customer.getCusSexCode());
        rel.setCusMoment(customer.getCusMoment());
        rel.setCusPhone(StrUtil.trimToNull(customer.getCusPhone()));
        rel.setCusHiddenStatusCode(customer.getCusHiddenStatusCode());
    }

    /** 排除已隐藏主页的客户关系行（冗余字段 cus_hidden_status_code） */
    public static void excludeHiddenCusMatchmakerRel(QueryWrapper<DtCusMatchmakerRel> queryWrapper) {
        queryWrapper.lambda().and(w -> w
                .isNull(DtCusMatchmakerRel::getCusHiddenStatusCode)
                .or()
                .ne(DtCusMatchmakerRel::getCusHiddenStatusCode, StatusCodeEnum.YES));
    }
}
