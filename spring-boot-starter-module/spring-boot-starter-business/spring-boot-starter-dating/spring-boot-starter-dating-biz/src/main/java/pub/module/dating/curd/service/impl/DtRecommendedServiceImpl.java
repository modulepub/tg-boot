package pub.module.dating.curd.service.impl;

import lombok.extern.slf4j.Slf4j;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.IdUtil;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import pub.module.common.model.po.BaseEntity;
import pub.module.dating.curd.entity.DtRecommended;
import pub.module.dating.curd.mapper.DtRecommendedMapper;
import pub.module.dating.curd.service.DtRecommendedService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.lang.reflect.Field;

import cn.hutool.core.util.ReflectUtil;


/**
 * 对象推荐 Service
 *
 * @author tg
 * 2026-03-30 00:52:26
 */
@Slf4j
@Service
public class DtRecommendedServiceImpl extends ServiceImpl<DtRecommendedMapper, DtRecommended> implements DtRecommendedService {

    String bizCode = "recommendedCode";

    public void setDefaultValue(DtRecommended entity) {
        Field declaredField = ReflectUtil.getField(entity.getClass(), bizCode);
        Assert.notNull(declaredField,"CODE 字段名称未設置");
        if (ReflectUtil.getFieldValue(entity, declaredField) == null||StrUtil.isEmpty(ReflectUtil.getFieldValue(entity, declaredField).toString())) {
            ReflectUtil.setFieldValue(entity, declaredField,"RC"+ IdUtil.getSnowflakeNextIdStr());
        }
    }

    @Override
    public DtRecommended getByCode(String code) {
        return this.getBaseMapper().selectOne(new QueryWrapper<DtRecommended>().eq(StrUtil.toUnderlineCase(bizCode), code),false);
    }

    @Override
    @Transactional
    public boolean save(DtRecommended entity) {
        Object code = ReflectUtil.getFieldValue(entity, bizCode);
        if (code!=null&&StrUtil.isNotEmpty(code.toString())) {
            Assert.isNull(this.getByCode(code.toString()), "编码已存在");
        }
        this.setDefaultValue(entity);
        this.getBaseMapper().insert(entity);

        return true;
    }

    @Override
    @Transactional
    public boolean saveBatch(Collection<DtRecommended> entityList) {
        for ( DtRecommended entity : entityList) {
            this.save(entity);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
        DtRecommended entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "DtRecommended 不存在");
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
    public boolean updateById(DtRecommended entity) {
        BaseEntity target = this.getByCode(ReflectUtil.getFieldValue(entity, bizCode).toString());
        Assert.isFalse(target!= null && !target.getId().equals(entity.getId()), "编码已存在");
        this.getBaseMapper().updateById(entity);

        return true;
    }

    @Override
    public DtRecommended getById(Serializable id) {
        DtRecommended entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "DtRecommended 不存在");
        this.setDefaultValue(entity);
        return entity;
    }

    @Override
    public DtRecommended getOne(Wrapper<DtRecommended> queryWrapper,
                                boolean throwEx) {
        DtRecommended entity = null;
        List<DtRecommended> list = this.list(queryWrapper);
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
