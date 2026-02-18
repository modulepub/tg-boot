package pub.module.customer.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import jakarta.annotation.Resource;

import cn.hutool.core.lang.Assert;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import pub.module.customer.api.constants.CusAssignSalesStatusCodeEnum;
import pub.module.customer.api.constants.CusDealtCompleteStatusCodeEnum;
import pub.module.customer.api.constants.CusDealtStatusCodeEnum;
import pub.module.customer.api.constants.PromotionTaskCodeEnum;
import pub.module.customer.curd.entity.Customer;
import pub.module.customer.curd.entity.CustomerPromotionRelation;
import pub.module.customer.curd.service.CustomerService;
import pub.module.data.api.constants.BaseEntityFiled;
import pub.module.customer.curd.service.CustomerPromotionRelationService;
import pub.module.customer.biz.service.*;

import org.springframework.stereotype.Service;

import java.util.List;

import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.service.dto.UserDTO;


/**
 * Api 客户营销关系 Service
 *
 * @author tg
 * 2026-02-01 10:25:44
 */
@Service
public class SpiCustomerPromotionRelationServiceImpl implements SpiCustomerPromotionRelationService {

    @Resource
    CustomerService customerService;
    @Resource
    ApiSysUserService apiSysUserService;
    @Resource
    CustomerPromotionRelationService customerPromotionRelationService;

    @Override
    public void assign(String promotionTaskCode, List<String> cusCodeList, List<String> userCodeList) {
        Assert.notEmpty(promotionTaskCode,"promotionTaskCode is not null");
        for (String cusCode : cusCodeList) {
            for (String userCode : userCodeList) {
                UserDTO userDTO = apiSysUserService.getUserByUserCode(userCode);
                Assert.notNull(userDTO, "严重异常！");
                Customer customer = customerService.getByCode(cusCode);
                Assert.notNull(customer, "严重异常！");
                if(PromotionTaskCodeEnum.CC.getCode().equals(promotionTaskCode)){
                    customer.setCusAssignSalesStatusCode(CusAssignSalesStatusCodeEnum.YES.getCode());
                }
                if(PromotionTaskCodeEnum.SC.getCode().equals(promotionTaskCode)){
                    customer.setCusAssignServersStatusCode(CusAssignSalesStatusCodeEnum.YES.getCode());
                }
                this.customerService.updateById(customer);
                QueryWrapper<CustomerPromotionRelation> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(CustomerPromotionRelation::getCusCode, cusCode);
                long count = customerPromotionRelationService.count(queryWrapper);
                if (count > 0) {
                   continue;
                }
                CustomerPromotionRelation customerPromotionRelation = BeanUtil.copyProperties(customer, CustomerPromotionRelation.class, BaseEntityFiled.NAMES);
                BeanUtil.copyProperties(userDTO, customerPromotionRelation, BaseEntityFiled.NAMES);
                customerPromotionRelation.setPromotionTaskCode(promotionTaskCode);
                customerPromotionRelationService.save(customerPromotionRelation);
            }

        }
    }

    @Override
    public void dealt(String promotionRelCode) {
        CustomerPromotionRelation customerPromotionRelation = customerPromotionRelationService.getByCode(promotionRelCode);
        customerPromotionRelation.setCusDealtStatusCode(CusDealtStatusCodeEnum.YES.getCode());
        customerPromotionRelationService.updateById(customerPromotionRelation);
        Customer customer = customerService.getByCode(customerPromotionRelation.getCusCode());
        customer.setCusDealtStatusCode(CusDealtStatusCodeEnum.YES.getCode());
        customerService.updateById(customer);
    }

    @Override
    public void complete(String promotionRelCode) {
        CustomerPromotionRelation customerPromotionRelation = customerPromotionRelationService.getByCode(promotionRelCode);
        customerPromotionRelation.setCusDealtCompleteStatusCode(CusDealtCompleteStatusCodeEnum.YES.getCode());
        customerPromotionRelationService.updateById(customerPromotionRelation);
        Customer customer = customerService.getByCode(customerPromotionRelation.getCusCode());
        customer.setCusDealtCompleteStatusCode(CusDealtCompleteStatusCodeEnum.YES.getCode());
        customerService.updateById(customer);
    }
}
