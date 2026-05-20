package pub.module.dating.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import pub.module.common.enums.BaseEntityFiled;
import pub.module.common.model.po.BaseEntity;
import pub.module.customer.api.service.ApiCustomerService;
import pub.module.customer.api.service.dto.CustomerDTO;
import pub.module.dating.api.service.dto.DtIntentionDTO;
import pub.module.dating.curd.entity.DtRecommended;
import pub.module.dating.curd.service.DtRecommendedService;
import pub.module.dating.api.service.*;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * Api 对象推荐 Service
 *
 * @author tg
 * 2026-03-30 00:52:26
 */
@Service
public class ApiDtRecommendedServiceImpl implements ApiDtRecommendedService {

    @Resource
    DtRecommendedService dtRecommendedService;
    @Resource
    ApiCustomerService apiCustomerService;

    @Override
    public void synFreeRecommend(DtIntentionDTO dtIntentionDTO, String userCode) {
        List<String> cusCodes = dtRecommendedService.list(new QueryWrapper<DtRecommended>().lambda().select(DtRecommended::getCusCode).eq(DtRecommended::getUserCode, userCode)).stream().map(DtRecommended::getCusCode).toList();
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setCusSexCode(dtIntentionDTO.getIntentionSexCode());
        List<CustomerDTO> customerDTOList = apiCustomerService.findCustomer(cusCodes, customerDTO);
        List<DtRecommended> dtRecommendedList = new ArrayList<>();
        for (CustomerDTO item : customerDTOList) {
            DtRecommended dtRecommended = BeanUtil.copyProperties(item, DtRecommended.class, BaseEntityFiled.NAMES);
            dtRecommended.setRecommendedSourceCode("free");
            dtRecommended.setRecommendedMatchScore(new BigDecimal("50"));
            dtRecommended.setUserCode(userCode);
            dtRecommended.setCusLsStatusCode(customerDTO.getCusLsStatusCode());
            dtRecommendedList.add(dtRecommended);
        }
        if (!dtRecommendedList.isEmpty()) {
            dtRecommendedService.saveBatch(dtRecommendedList);
        }
    }
}
