package pub.module.dating.biz.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.customer.api.service.ApiCustomerService;
import pub.module.customer.api.service.dto.CustomerDTO;
import pub.module.dating.api.service.ApiDtCusMatchmakerRelService;
import pub.module.dating.api.service.dto.CusMkRelShowStatusUpdateDTO;
import pub.module.dating.curd.entity.DtCusMatchmakerRel;
import pub.module.dating.curd.entity.DtMatchmaker;
import pub.module.dating.curd.service.DtCusMatchmakerRelService;
import pub.module.dating.curd.service.DtMatchmakerService;
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
    private ApiCustomerService apiCustomerService;
    @Resource
    private DtMatchmakerService dtMatchmakerService;

    @Override
    public void updateCusMkRelShowStatus(CusMkRelShowStatusUpdateDTO dto) {
        Assert.notNull(dto, "参数不能为空");
        Assert.notBlank(dto.getCusMkRelCode(), "cusMkRelCode不能为空");
        String statusCode = StrUtil.trimToEmpty(dto.getCusMkRelShowStatusCode());
        Assert.isTrue("0".equals(statusCode) || "1".equals(statusCode), "展示状态仅支持 0 或 1");

        UserDTO userDTO = UserUtil.getCurrentSysUser();
        CustomerDTO customerDTO = apiCustomerService.getCusByUserCode(userDTO.getUserCode());
        Assert.notNull(customerDTO, "客户信息不存在");
        Assert.notBlank(customerDTO.getCusCode(), "客户编码不存在");

        boolean updated = dtCusMatchmakerRelService.lambdaUpdate()
                .eq(DtCusMatchmakerRel::getCusMkRelCode, dto.getCusMkRelCode())
                .eq(DtCusMatchmakerRel::getCusCode, customerDTO.getCusCode())
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
        CustomerDTO customer = apiCustomerService.getCusByUserCode(orderBuyerUserCode);
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
        CustomerDTO customer = apiCustomerService.getCusByUserCode(customerUserCode);
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
    private void insertCusMatchmakerRelIfAbsent(CustomerDTO customer, DtMatchmaker matchmaker, String fallbackMkUserCode) {
        long existed = dtCusMatchmakerRelService.lambdaQuery()
                .eq(DtCusMatchmakerRel::getCusCode, customer.getCusCode())
                .eq(DtCusMatchmakerRel::getMkCode, matchmaker.getMkCode())
                .count();
        if (existed > 0) {
            log.debug("skip cus-matchmaker rel: already bound cusCode={}, mkCode={}", customer.getCusCode(), matchmaker.getMkCode());
            return;
        }

        DtCusMatchmakerRel rel = new DtCusMatchmakerRel();
        rel.setCusCode(customer.getCusCode());
        rel.setCusName(customer.getCusName());
        rel.setCusAvatar(customer.getCusAvatar());
        rel.setCusSexCode(customer.getCusSexCode());
        rel.setCusMoment(customer.getCusMoment());
        rel.setCusMkRelShowStatusCode("1");

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
}
