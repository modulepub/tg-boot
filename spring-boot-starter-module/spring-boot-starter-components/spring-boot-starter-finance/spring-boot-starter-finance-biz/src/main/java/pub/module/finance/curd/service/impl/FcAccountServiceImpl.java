package pub.module.finance.curd.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import lombok.extern.slf4j.Slf4j;

import pub.module.finance.api.constants.FcAcCreditStatusCodeEnum;
import pub.module.finance.api.constants.FcAcDefaultStatusCodeEnum;
import pub.module.finance.curd.entity.FcAccount;
import pub.module.finance.curd.mapper.FcAccountMapper;
import pub.module.finance.curd.service.IFcAccountService;
import cn.hutool.core.util.IdUtil;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import jakarta.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.lang.reflect.Field;

import cn.hutool.core.util.ReflectUtil;


/**
 * 金融账户管理
 *
 * @author tg
 * @version V1.0
 * @since 2025-10-09
 */
@Slf4j
@Service
public class FcAccountServiceImpl extends ServiceImpl<FcAccountMapper, FcAccount> implements IFcAccountService {

    @Resource
    ApplicationEventPublisher applicationEventPublisher;

    public void setDefaultValue(FcAccount entity) {
        Field declaredField = ReflectUtil.getField(entity.getClass(), "fcAcCode");
        Assert.notNull(declaredField, "CODE 字段名称未設置");
        if (ReflectUtil.getFieldValue(entity, declaredField) == null) {
            ReflectUtil.setFieldValue(entity, declaredField, IdUtil.getSnowflakeNextIdStr());
        }
        if(StrUtil.isEmpty(entity.getFcAcDefaultStatusCode())){
            entity.setFcAcDefaultStatusCode(FcAcDefaultStatusCodeEnum.NOT_PAY.getCode());
        }
        if(StrUtil.isEmpty(entity.getFcAcCreditStatusCode())){
            entity.setFcAcCreditStatusCode(FcAcCreditStatusCodeEnum.NOT.getCode());
        }
    }


    @Override
    @Transactional
    public boolean save(FcAccount entity) {
        Assert.notNull(entity, "entity 不能为空");
        Assert.notEmpty(entity.getFcProductTypeCode(), "金融产品类型 不能为空");
        this.setDefaultValue(entity);
        this.getBaseMapper().insert(entity);
        
        return true;
    }

    @Override
    @Transactional
    public boolean saveBatch(Collection<FcAccount> entityList) {
        for (FcAccount entity : entityList) {
            this.save(entity);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
        FcAccount entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "FcAccount不存在");
        this.getBaseMapper().deleteById(id);
        
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
    public boolean updateById(FcAccount entity) {
        this.getBaseMapper().updateById(entity);
        
        return true;
    }

    @Override
    public FcAccount getById(Serializable id) {
        FcAccount entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "FcAccount不存在");
        this.setDefaultValue(entity);
        return entity;
    }

    @Override
    public FcAccount getOne(Wrapper<FcAccount> queryWrapper,
                            boolean throwEx) {
        FcAccount entity = null;
        List<FcAccount> list = this.list(queryWrapper);
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
