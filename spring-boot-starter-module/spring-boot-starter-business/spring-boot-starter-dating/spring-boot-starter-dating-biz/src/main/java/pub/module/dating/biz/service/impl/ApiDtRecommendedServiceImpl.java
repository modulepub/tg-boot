package pub.module.dating.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import pub.module.customer.api.service.ApiCustomerService;
import pub.module.customer.api.service.dto.CustomerDTO;
import pub.module.dating.curd.entity.DtRecommended;
import pub.module.dating.curd.mapper.DtRecommendedMapper;
import pub.module.dating.curd.service.DtRecommendedService;
import pub.module.dating.api.service.*;

import org.springframework.stereotype.Service;

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
    static boolean isRun = false;

    @Override
    public void synFreeRecommend() {
        if (!isRun) {
            List<String> cusCodes = dtRecommendedService.list(new QueryWrapper<DtRecommended>().lambda().select(DtRecommended::getCusCode)).stream().map(DtRecommended::getCusCode).toList();
            List<CustomerDTO> customerDTOList = apiCustomerService.listAll(cusCodes);
            List<DtRecommended> dtRecommendedList = new ArrayList<>();
            for (CustomerDTO customerDTO : customerDTOList) {
                DtRecommended dtRecommended = BeanUtil.copyProperties(customerDTO, DtRecommended.class);
                dtRecommended.setUserCode(customerDTO.getCusUserCode());
                dtRecommendedList.add(dtRecommended);
            }
            if (!dtRecommendedList.isEmpty()) {
                dtRecommendedService.saveBatch(dtRecommendedList);
            }
        }
    }
}
