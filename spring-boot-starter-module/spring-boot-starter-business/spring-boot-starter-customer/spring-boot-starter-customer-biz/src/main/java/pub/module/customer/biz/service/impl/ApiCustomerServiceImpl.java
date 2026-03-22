package pub.module.customer.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import pub.module.customer.api.constants.CusSourceEnum;
import pub.module.customer.curd.entity.Customer;
import pub.module.customer.curd.service.CustomerService;
import pub.module.customer.api.service.*;

import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * Api 客户 Service
 *
 * @author tg
 * 2026-02-01 10:25:44
 */
@Slf4j
@Service
public class ApiCustomerServiceImpl implements ApiCustomerService {
    @Resource
    CustomerService customerService;

    @Override
    public void importData(Map<String, Object> data) {
        Customer customer = BeanUtil.copyProperties(data, Customer.class);
        customer.setCusSourceCode(CusSourceEnum.EXCEL.getCode());
        log.info("导入客户数据数据{}", customer);
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Customer::getCusPhone, customer.getCusPhone()).or().eq(Customer::getCusIdNo, customer.getCusIdNo());
        Customer old = customerService.getOne(queryWrapper, false);
        if (old != null) {
            BeanUtil.copyProperties(old, customer, CopyOptions.create().setIgnoreNullValue(true));
            customerService.updateById(customer);
        } else {
            customerService.save(customer);
        }

    }
}
