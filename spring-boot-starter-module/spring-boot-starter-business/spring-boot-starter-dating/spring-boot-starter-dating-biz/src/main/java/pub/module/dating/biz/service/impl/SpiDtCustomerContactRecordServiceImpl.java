package pub.module.dating.biz.service.impl;

import pub.module.common.enums.StatusCodeEnum;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import jakarta.annotation.Resource;
import pub.module.dating.biz.service.*;

import org.springframework.stereotype.Service;
import pub.module.dating.crud.entity.DtCustomer;
import pub.module.dating.crud.entity.DtCustomerContactRecord;
import pub.module.dating.crud.service.DtCustomerContactRecordService;
import pub.module.dating.crud.service.DtCustomerService;
import pub.module.common.enums.BaseEntityFiled;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;


/**
 * Api 联络记录 Service
 *
 * @author tg
 * 2026-02-01 10:25:44
 */
@Service
public class SpiDtCustomerContactRecordServiceImpl implements SpiDtCustomerContactRecordService {
    @Resource
    private DtCustomerContactRecordService customerContactRecordService;
    @Resource
    private DtCustomerService customerService;

    @Override
    public void doRecord(DtCustomerContactRecord customerContactRecord) {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        DtCustomer customer = customerService.getByCode(customerContactRecord.getCusCode());
        customer.setCusFollowUpStatusCode(StatusCodeEnum.YES);
        customer.setCusWechatId(customerContactRecord.getCusWechatId());
        customer.setCusIntentionStatusCode(customerContactRecord.getCusIntentionStatusCode());
        customer.setCusIntentionLevelCode(customerContactRecord.getCusIntentionLevelCode());
        customerService.updateById(customer);
        Assert.notNull(customer, "严重异常，customer不存在！");
        BeanUtil.copyProperties(userDTO, customerContactRecord, BaseEntityFiled.NAMES);
        BeanUtil.copyProperties(customer, customerContactRecord, BaseEntityFiled.NAMES);
        customerContactRecordService.save(customerContactRecord);
    }
}
