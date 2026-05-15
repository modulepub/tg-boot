package pub.module.customer.curd.service.impl;

import cn.hutool.core.util.PhoneUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.IdUtil;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import pub.module.customer.api.constants.CusPoolStatusCodeEnum;
import pub.module.customer.curd.entity.CustomerContactRecord;
import pub.module.customer.curd.entity.CustomerPromotionTask;
import pub.module.customer.curd.service.CustomerContactRecordService;
import pub.module.customer.curd.service.CustomerPromotionTaskService;
import pub.module.common.model.po.BaseEntity;
import pub.module.customer.api.service.dto.CusCityResidenceOptionDTO;
import pub.module.customer.curd.entity.Customer;
import pub.module.customer.curd.mapper.CustomerMapper;
import pub.module.customer.curd.service.CustomerService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.lang.reflect.Field;

import cn.hutool.core.util.ReflectUtil;


/**
 * 客户 Service
 *
 * @author tg
 * 2026-02-01 10:25:44
 */
@Slf4j
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {
    @Resource
    CustomerContactRecordService customerContactRecordService;
    @Resource
    CustomerPromotionTaskService customerPromotionTaskService;

    String bizCode = "cusCode";

    public void setDefaultValue(Customer entity) {
        Field declaredField = ReflectUtil.getField(entity.getClass(), bizCode);
        Assert.notNull(declaredField,"CODE 字段名称未設置");
        if (ReflectUtil.getFieldValue(entity, declaredField) == null||StrUtil.isEmpty(ReflectUtil.getFieldValue(entity, declaredField).toString())) {
            ReflectUtil.setFieldValue(entity, declaredField, IdUtil.getSnowflakeNextIdStr());
        }
        if(StrUtil.isEmpty(entity.getCusPoolStatusCode())){
            entity.setCusPoolStatusCode(CusPoolStatusCodeEnum.NO.getCode());
        }
        if(StrUtil.isNotEmpty(entity.getCusPhone())){
            entity.setCusPhone(entity.getCusPhone().trim());
            boolean isPhone1Valid = PhoneUtil.isMobile(entity.getCusPhone());
            Assert.isTrue(isPhone1Valid,"手机号格式错误！");
            entity.setCusPhone(entity.getCusPhone());
        }
    }

    @Override
    public Customer getByCode(String code) {
        return this.getBaseMapper().selectOne(new QueryWrapper<Customer>().eq(StrUtil.toUnderlineCase(bizCode), code),false);
    }

    @Override
    @Transactional
    public boolean save(Customer entity) {
        Object code = ReflectUtil.getFieldValue(entity, bizCode);
        if (code != null&&StrUtil.isNotEmpty(code.toString())) {
            Assert.notNull(this.getByCode(code.toString()), "编码已存在");
        }
        long exPhone = this.count(new QueryWrapper<Customer>().lambda().eq(Customer::getCusPhone, entity.getCusPhone()));
        Assert.isTrue(exPhone==0,"手机号已存在！");
        this.setDefaultValue(entity);
        this.getBaseMapper().insert(entity);

        return true;
    }

    @Override
    @Transactional
    public boolean saveBatch(Collection<Customer> entityList) {
        for ( Customer entity : entityList) {
            this.save(entity);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
        Customer entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "Customer 不存在");
        this.getBaseMapper().deleteById(id);
        customerContactRecordService.remove(new QueryWrapper<CustomerContactRecord>().lambda().eq(CustomerContactRecord::getCusCode,entity.getCusCode()));
        customerPromotionTaskService.remove(new QueryWrapper<CustomerPromotionTask>().lambda().eq(CustomerPromotionTask::getCusCode,entity.getCusCode()));
        return true;
    }

    @Transactional
    @Override
    public boolean removeByIds(Collection<?> ids) {
        Assert.notEmpty(ids, "主键集合不能为空");
        ids.forEach(entity -> this.removeById((Serializable) entity));
        return true;
    }

    @Override
    @Transactional
    public boolean updateById(Customer entity) {
        BaseEntity target = this.getByCode(ReflectUtil.getFieldValue(entity, bizCode).toString());
        Assert.isFalse(target!= null && !target.getId().equals(entity.getId()), "编码已存在");
        this.getBaseMapper().updateById(entity);

        return true;
    }

    @Override
    public Customer getById(Serializable id) {
        Customer entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "Customer 不存在");
        this.setDefaultValue(entity);
        return entity;
    }

    @Override
    public Customer getOne(Wrapper<Customer> queryWrapper,
                                boolean throwEx) {
        Customer entity = null;
        List<Customer> list = this.list(queryWrapper);
        if (!list.isEmpty()) {
            entity = list.get(0);
            this.setDefaultValue(entity);
        }
        if (throwEx && list.size() > 1) {
            throw new IllegalArgumentException("查询条件有误，查询到多条数据");
        }
        return entity;
    }

    @Override
    public List<CusCityResidenceOptionDTO> listDistinctResidenceCities() {
        return getBaseMapper().listDistinctResidenceCities();
    }
}
