package pub.module.finance.curd.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import lombok.extern.slf4j.Slf4j;

import pub.module.finance.api.constants.FcLoanBillOverdueStatusCodeEnum;
import pub.module.finance.api.constants.FcLoanBillSettleStatusCodeEnum;
import pub.module.finance.curd.entity.FcLoanBill;
import pub.module.finance.curd.mapper.FcLoanBillMapper;
import pub.module.finance.curd.service.IFcLoanBillService;
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
 * 借贷分期账单
 * @author tg
 * @since 2025-10-02
 * @version V1.0
 */
@Slf4j
@Service
public class FcLoanBillServiceImpl extends ServiceImpl<FcLoanBillMapper, FcLoanBill> implements IFcLoanBillService {

    @Resource
    ApplicationEventPublisher applicationEventPublisher;

    public void setDefaultValue(FcLoanBill entity) {
        Field declaredField = ReflectUtil.getField(entity.getClass(), "fcLoanBillCode");
           Assert.notNull(declaredField,"CODE 字段名称未設置");
        if (ReflectUtil.getFieldValue(entity, declaredField) == null) {
            ReflectUtil.setFieldValue(entity, declaredField, IdUtil.getSnowflakeNextIdStr());
        }
        if(entity.getFcLoanBillSettleAmount()==null){
            entity.setFcLoanBillSettleAmount(BigDecimal.ZERO);
        }
        if(StrUtil.isEmpty(entity.getFcLoanBillOverdueStatusCode())){
            entity.setFcLoanBillOverdueStatusCode(FcLoanBillOverdueStatusCodeEnum.NOT.getCode());
        }
        if(StrUtil.isEmpty(entity.getFcLoanBillSettleStatusCode())){
            entity.setFcLoanBillSettleStatusCode(FcLoanBillSettleStatusCodeEnum.NOT.getCode());
        }
        if(StrUtil.isEmpty(entity.getFcLoanBillDueStatusCode())){
            entity.setFcLoanBillDueStatusCode(FcLoanBillSettleStatusCodeEnum.NOT.getCode());
        }
    }


    @Override
    @Transactional
    public boolean save(FcLoanBill entity) {
        this.setDefaultValue(entity);
        this.getBaseMapper().insert(entity);
        
        return true;
    }

    @Override
    @Transactional
    public boolean saveBatch(Collection<FcLoanBill> entityList) {
        for ( FcLoanBill entity : entityList) {
            this.save(entity);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
         FcLoanBill entity = this.getBaseMapper().selectById(id);
         Assert.notNull(entity, "FcLoanBill不存在");
         this.getBaseMapper().deleteById(id);
         
         return true;
     }

    @Transactional
       @Override
       public boolean removeByIds(Collection<?> ids) {
        for (Object entity : ids) {
            this.removeById((Serializable) entity);
        }
        return true;
       }

    @Override
    @Transactional
    public boolean updateById(FcLoanBill entity) {
        this.getBaseMapper().updateById(entity);
        
        return true;
    }

    @Override
    public FcLoanBill getById(Serializable id) {
        FcLoanBill entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "FcLoanBill不存在");
        this.setDefaultValue(entity);
        return entity;
    }

    @Override
    public FcLoanBill getOne(Wrapper<FcLoanBill> queryWrapper,
                          boolean throwEx) {
        FcLoanBill entity = null;
        List<FcLoanBill> list = this.list(queryWrapper);
        if (!list.isEmpty()) {
            entity = list.get(0);
            this.setDefaultValue(entity);
        }
        if (throwEx && list.size() > 1) {
            throw new IllegalArgumentException("查询条件有误，查询到多条数据");
        }
        return entity;
    }
}
