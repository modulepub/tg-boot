package pub.module.customer.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import jakarta.annotation.Resource;

import cn.hutool.core.lang.Assert;


import pub.module.customer.api.constants.*;
import pub.module.customer.curd.entity.Customer;
import pub.module.customer.curd.entity.CustomerPromotionTask;
import pub.module.customer.curd.service.CustomerPromotionTaskService;
import pub.module.customer.curd.service.CustomerService;
import pub.module.common.enums.BaseEntityFiled;
import pub.module.customer.biz.service.*;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    CustomerPromotionTaskService customerPromotionTaskService;

    @Override
    public void assign(String promotionTaskTypeCode, List<String> cusCodeList, List<String> userCodeList) {
        Assert.notEmpty(promotionTaskTypeCode,"promotionTaskTypeCode is not null");
        PromotionTaskTypeCodeEnum taskType = PromotionTaskTypeCodeEnum.fromValue(promotionTaskTypeCode);
        for (String cusCode : cusCodeList) {
            for (String userCode : userCodeList) {
                UserDTO userDTO = apiSysUserService.getUserByUserCode(userCode);
                Assert.notNull(userDTO, "严重异常！");
                Customer customer = customerService.getByCode(cusCode);
                Assert.notNull(customer, "严重异常！");
                if (taskType == PromotionTaskTypeCodeEnum.CONTACT) {
                    customer.setCusAssignSalesStatusCode(CusAssignSalesStatusCodeEnum.ASSIGNED);
                    customer.setCusAssignSalesTime(LocalDateTime.now());
                }
                if (taskType == PromotionTaskTypeCodeEnum.SERVICE) {
                    customer.setCusAssignServersStatusCode(CusAssignServersStatusCodeEnum.ASSIGNED);
                }
                this.customerService.updateById(customer);
                CustomerPromotionTask customerPromotionTask = BeanUtil.copyProperties(customer, CustomerPromotionTask.class, BaseEntityFiled.NAMES);
                BeanUtil.copyProperties(userDTO, customerPromotionTask, BaseEntityFiled.NAMES);
                customerPromotionTask.setPromotionTaskTypeCode(taskType);
                customerPromotionTask.setCusFollowUpStatusCode(CusFollowUpStatusCodeEnum.NO);
                customerPromotionTaskService.save(customerPromotionTask);
            }

        }
    }

    @Override
    public void dealt(String promotionTaskCode) {
        Assert.notEmpty(promotionTaskCode,"promotionTaskCode not null");
        CustomerPromotionTask customerPromotionTask = customerPromotionTaskService.getByCode(promotionTaskCode);
        customerPromotionTask.setCusDealtStatusCode(CusDealtStatusCodeEnum.YES);
        customerPromotionTaskService.updateById(customerPromotionTask);
        Customer customer = customerService.getByCode(customerPromotionTask.getCusCode());
        customer.setCusDealtStatusCode(CusDealtStatusCodeEnum.YES);
        customerService.updateById(customer);
    }

    @Override
    public void complete(String promotionTaskCode) {
        CustomerPromotionTask customerPromotionTask = customerPromotionTaskService.getByCode(promotionTaskCode);
        customerPromotionTask.setCusDealtCompleteStatusCode(CusDealtCompleteStatusCodeEnum.YES);
        customerPromotionTaskService.updateById(customerPromotionTask);
        Customer customer = customerService.getByCode(customerPromotionTask.getCusCode());
        customer.setCusDealtCompleteStatusCode(CusDealtCompleteStatusCodeEnum.YES);
        customerService.updateById(customer);
    }
}
