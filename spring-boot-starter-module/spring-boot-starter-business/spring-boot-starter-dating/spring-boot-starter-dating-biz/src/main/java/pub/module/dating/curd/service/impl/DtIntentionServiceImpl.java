package pub.module.dating.curd.service.impl;

import lombok.extern.slf4j.Slf4j;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.IdUtil;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import pub.module.dating.curd.entity.DtIntention;
import pub.module.dating.curd.mapper.DtIntentionMapper;
import pub.module.dating.curd.service.DtIntentionService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.lang.reflect.Field;

import cn.hutool.core.util.ReflectUtil;


/**
 * 交友意向 Service
 *
 * @author tg
 * 2026-01-07 23:30:24
 */
@Slf4j
@Service
public class DtIntentionServiceImpl extends ServiceImpl<DtIntentionMapper, DtIntention> implements DtIntentionService {

    String bizCode = "intentionCode";

    public void setDefaultValue(DtIntention entity) {
        Field declaredField = ReflectUtil.getField(entity.getClass(), bizCode);
        Assert.notNull(declaredField,"CODE 字段名称未設置");
        if (ReflectUtil.getFieldValue(entity, declaredField) == null) {
            ReflectUtil.setFieldValue(entity, declaredField, IdUtil.getSnowflakeNextIdStr());
        }
    }

    @Override
    public DtIntention getByCode(String code) {
        return this.getBaseMapper().selectOne(new QueryWrapper<DtIntention>().eq(StrUtil.toUnderlineCase(bizCode), code),false);
    }

    @Override
    @Transactional
    public boolean save(DtIntention entity) {
        this.setDefaultValue(entity);
        this.getBaseMapper().insert(entity);

        return true;
    }

    @Override
    @Transactional
    public boolean saveBatch(Collection<DtIntention> entityList) {
        for ( DtIntention entity : entityList) {
            this.save(entity);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
        DtIntention entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "DtIntention 不存在");
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
    public boolean updateById(DtIntention entity) {
        this.getBaseMapper().updateById(entity);

        return true;
    }

    @Override
    public DtIntention getById(Serializable id) {
        DtIntention entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "DtIntention 不存在");
        this.setDefaultValue(entity);
        return entity;
    }

    @Override
    public DtIntention getOne(Wrapper<DtIntention> queryWrapper,
                                boolean throwEx) {
        DtIntention entity = null;
        List<DtIntention> list = this.list(queryWrapper);
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
