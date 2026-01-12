package pub.module.finance.curd.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;

import pub.module.finance.api.constants.FcLoanApprovalStatusCodeEnum;
import pub.module.finance.api.constants.FcLoanOverdueStatusCodeEnum;
import pub.module.finance.api.constants.FcLoanSettleStatusCodeEnum;
import pub.module.finance.curd.entity.FcLoan;
import pub.module.finance.curd.entity.FcLoanBill;
import pub.module.finance.curd.mapper.FcLoanMapper;
import pub.module.finance.curd.service.IFcLoanBillService;
import pub.module.finance.curd.service.IFcLoanService;
import cn.hutool.core.util.IdUtil;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import jakarta.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.lang.reflect.Field;

import cn.hutool.core.util.ReflectUtil;


/**
 * 信用借贷管理
 *
 * @author tg
 * @version V1.0
 * @since 2025-10-03
 */
@Slf4j
@Service
public class FcLoanServiceImpl extends ServiceImpl<FcLoanMapper, FcLoan> implements IFcLoanService {

    @Resource
    ApplicationEventPublisher applicationEventPublisher;
    @Resource
    IFcLoanBillService fcLoanBillService;

    public void setDefaultValue(FcLoan entity) {
        Field declaredField = ReflectUtil.getField(entity.getClass(), "fcLoanCode");
        Assert.notNull(declaredField, "CODE 字段名称未設置");
        if (ReflectUtil.getFieldValue(entity, declaredField) == null) {
            ReflectUtil.setFieldValue(entity, declaredField, IdUtil.getSnowflakeNextIdStr());
        }
        if (StrUtil.isEmpty(entity.getFcLoanApprovalStatusCode())) {
            entity.setFcLoanApprovalStatusCode(FcLoanApprovalStatusCodeEnum.ING.getCode());
        }
        if (entity.getFcLoanAmount() == null) {
            entity.setFcLoanAmount(BigDecimal.ZERO);
        }
        if (entity.getFcLoanApyAmount() == null) {
            entity.setFcLoanApyAmount(BigDecimal.ZERO);
        }
        if (StrUtil.isEmpty(entity.getFcLoanOverdueStatusCode())) {
            entity.setFcLoanOverdueStatusCode(FcLoanOverdueStatusCodeEnum.NOT.getCode());
        }
        if (StrUtil.isEmpty(entity.getFcLoanSettleStatusCode())) {
            entity.setFcLoanSettleStatusCode(FcLoanSettleStatusCodeEnum.NOT.getCode());
        }
        if (entity.getFcLoanSettleAmount() == null) {
            entity.setFcLoanSettleAmount(BigDecimal.ZERO);
        }
        if (entity.getFcLoanSettlePeriods() == null) {
            entity.setFcLoanSettlePeriods(0);
        }
    }


    @Override
    @Transactional
    public boolean save(FcLoan entity) {
        this.setDefaultValue(entity);
        this.getBaseMapper().insert(entity);
        
        return true;
    }

    @Override
    @Transactional
    public boolean saveBatch(Collection<FcLoan> entityList) {
        for (FcLoan entity : entityList) {
            this.save(entity);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
        FcLoan entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "FcLoan不存在");
        this.getBaseMapper().deleteById(id);

        QueryWrapper<FcLoanBill> fcLoanBillQueryWrapper = new QueryWrapper<>();
        fcLoanBillQueryWrapper.lambda().eq(FcLoanBill::getFcLoanCode, entity.getFcLoanCode());
        fcLoanBillService.remove(fcLoanBillQueryWrapper);
        return true;
    }

    @Transactional
    @Override
    public boolean removeByIds(Collection<?> ids) {
        ids.forEach(entity -> this.removeById((Serializable) entity));
        return true;
    }

    @Override
    @Transactional
    public boolean updateById(FcLoan entity) {
        this.getBaseMapper().updateById(entity);
        
        return true;
    }

    @Override
    public FcLoan getById(Serializable id) {
        FcLoan entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "FcLoan不存在");
        this.setDefaultValue(entity);
        return entity;
    }

    @Override
    public FcLoan getOne(Wrapper<FcLoan> queryWrapper,
                         boolean throwEx) {
        FcLoan entity = null;
        List<FcLoan> list = this.list(queryWrapper);
        if (!list.isEmpty()) {
            entity = list.get(0);
            this.setDefaultValue(entity);
        }
        if (throwEx && list.size() > 1) {
            throw new IllegalArgumentException("查询条件有误，查询到多条数据");
        }
        return entity;
    }

    public FcLoan getByCode(String code){
        QueryWrapper<FcLoan> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FcLoan::getFcLoanCode, code);
        return this.getOne(queryWrapper, false);
    }
}
