package pub.module.customer.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import pub.module.customer.api.constants.CusFollowUpStatusCodeEnum;
import pub.module.customer.biz.service.*;

import org.springframework.stereotype.Service;
import pub.module.customer.curd.entity.Customer;
import pub.module.customer.curd.entity.CustomerContactRecord;
import pub.module.customer.curd.entity.CustomerPromotionTask;
import pub.module.customer.curd.service.CustomerContactRecordService;
import pub.module.customer.curd.service.CustomerPromotionTaskService;
import pub.module.customer.curd.service.CustomerService;
import pub.module.common.constants.BaseEntityFiled;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;


/**
 * Api 联络记录 Service
 *
 * @author tg
 * 2026-02-01 10:25:44
 */
@Service
public class SpiCustomerContactRecordServiceImpl implements SpiCustomerContactRecordService {
    @Resource
    private CustomerContactRecordService customerContactRecordService;
    @Resource
    private CustomerService customerService;
    @Resource
    private CustomerPromotionTaskService customerPromotionTaskService;

    @Override
    public void doRecord(CustomerContactRecord customerContactRecord) {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        Customer customer = customerService.getByCode(customerContactRecord.getCusCode());
        customer.setCusFollowUpStatusCode(CusFollowUpStatusCodeEnum.YES.getCode());
        customer.setCusWechatId(customerContactRecord.getCusWechatId());
        customer.setCusIntentionStatusCode(customerContactRecord.getCusIntentionStatusCode());
        customer.setCusIntentionLevelCode(customerContactRecord.getCusIntentionLevelCode());
        customerService.updateById(customer);
        Assert.notNull(customer, "严重异常，customer不存在！");
        BeanUtil.copyProperties(userDTO, customerContactRecord, BaseEntityFiled.NAMES);
        BeanUtil.copyProperties(customer, customerContactRecord, BaseEntityFiled.NAMES);
        customerContactRecordService.save(customerContactRecord);
        CustomerPromotionTask customerPromotionTask = customerPromotionTaskService.getOne(new QueryWrapper<CustomerPromotionTask>()
                        .lambda()
                        .eq(CustomerPromotionTask::getCusCode, customer.getCusCode())
                        .eq(CustomerPromotionTask::getUserCode, userDTO.getUserCode())
                , false);
        customerPromotionTask.setCusFollowUpStatusCode(CusFollowUpStatusCodeEnum.YES.getCode());
        customerPromotionTask.setCusIntentionStatusCode(customerContactRecord.getCusIntentionStatusCode());
        customerPromotionTask.setCusIntentionLevelCode(customerContactRecord.getCusIntentionLevelCode());
        customerPromotionTaskService.updateById(customerPromotionTask);
    }
}
