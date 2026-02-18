package pub.module.customer.curd.service.impl;

import lombok.extern.slf4j.Slf4j;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.IdUtil;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import pub.module.customer.curd.entity.CustomerPromotionRelation;
import pub.module.customer.curd.service.CustomerPromotionRelationService;
import pub.module.data.api.entity.BaseEntity;
import pub.module.customer.curd.mapper.CustomerPromotionRelationMapper;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.lang.reflect.Field;

import cn.hutool.core.util.ReflectUtil;


/**
 * 客户营销关系 Service
 *
 * @author tg
 * 2026-02-01 10:25:44
 */
@Slf4j
@Service
public class CustomerPromotionRelationServiceImpl extends ServiceImpl<CustomerPromotionRelationMapper, CustomerPromotionRelation> implements CustomerPromotionRelationService {

    String bizCode = "promotionRelCode";

    public void setDefaultValue(CustomerPromotionRelation entity) {
        Field declaredField = ReflectUtil.getField(entity.getClass(), bizCode);
        Assert.notNull(declaredField,"CODE 字段名称未設置");
        if (ReflectUtil.getFieldValue(entity, declaredField) == null) {
            ReflectUtil.setFieldValue(entity, declaredField, IdUtil.getSnowflakeNextIdStr());
        }
    }

    @Override
    public CustomerPromotionRelation getByCode(String code) {
        return this.getBaseMapper().selectOne(new QueryWrapper<CustomerPromotionRelation>().eq(StrUtil.toUnderlineCase(bizCode), code),false);
    }

    @Override
    @Transactional
    public boolean save(CustomerPromotionRelation entity) {
        Object code = ReflectUtil.getFieldValue(entity, bizCode);
        if (code != null) {
            Assert.notNull(this.getByCode(code.toString()), "编码已存在");
        }
        this.setDefaultValue(entity);
        this.getBaseMapper().insert(entity);

        return true;
    }

    @Override
    @Transactional
    public boolean saveBatch(Collection<CustomerPromotionRelation> entityList) {
        for ( CustomerPromotionRelation entity : entityList) {
            this.save(entity);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
        CustomerPromotionRelation entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "CustomerTelemarketerRelation 不存在");
        this.getBaseMapper().deleteById(id);

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
    public boolean updateById(CustomerPromotionRelation entity) {
        BaseEntity target = this.getByCode(ReflectUtil.getFieldValue(entity, bizCode).toString());
        Assert.isFalse(target!= null && !target.getId().equals(entity.getId()), "编码已存在");
        this.getBaseMapper().updateById(entity);

        return true;
    }

    @Override
    public CustomerPromotionRelation getById(Serializable id) {
        CustomerPromotionRelation entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "CustomerTelemarketerRelation 不存在");
        this.setDefaultValue(entity);
        return entity;
    }

    @Override
    public CustomerPromotionRelation getOne(Wrapper<CustomerPromotionRelation> queryWrapper,
                                            boolean throwEx) {
        CustomerPromotionRelation entity = null;
        List<CustomerPromotionRelation> list = this.list(queryWrapper);
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
