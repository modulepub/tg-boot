package pub.module.dating.crud.service.impl;

import lombok.extern.slf4j.Slf4j;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.IdUtil;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import pub.module.common.model.po.BaseEntity;
import pub.module.dating.crud.entity.DtContactApply;
import pub.module.dating.crud.mapper.DtContactApplyMapper;
import pub.module.dating.crud.service.DtContactApplyService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.lang.reflect.Field;

import cn.hutool.core.util.ReflectUtil;


/**
 * 联系人申请表 Service
 *
 * @author tg
 * 2026-05-03 03:39:43
 */
@Slf4j
@Service
public class DtContactApplyServiceImpl extends ServiceImpl<DtContactApplyMapper, DtContactApply> implements DtContactApplyService {

    String bizCode = "contactApplyCode";

    public void setDefaultValue(DtContactApply entity) {
        Field declaredField = ReflectUtil.getField(entity.getClass(), bizCode);
        Assert.notNull(declaredField,"CODE 字段名称未設置");
        if (ReflectUtil.getFieldValue(entity, declaredField) == null||StrUtil.isEmpty(ReflectUtil.getFieldValue(entity, declaredField).toString())) {
            ReflectUtil.setFieldValue(entity, declaredField, "CA"+ IdUtil.getSnowflakeNextIdStr());
        }
    }

    @Override
    public DtContactApply getByCode(String code) {
        return this.getBaseMapper().selectOne(new QueryWrapper<DtContactApply>().eq(StrUtil.toUnderlineCase(bizCode), code),false);
    }

    @Override
    @Transactional
    public boolean save(DtContactApply entity) {
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
    public boolean saveBatch(Collection<DtContactApply> entityList) {
        for ( DtContactApply entity : entityList) {
            this.save(entity);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
        DtContactApply entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "DtContactApply 不存在");
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
    public boolean updateById(DtContactApply entity) {
        BaseEntity target = this.getByCode(ReflectUtil.getFieldValue(entity, bizCode).toString());
        Assert.isFalse(target!= null && !target.getId().equals(entity.getId()), "编码已存在");
        this.getBaseMapper().updateById(entity);

        return true;
    }

    @Override
    public DtContactApply getById(Serializable id) {
        DtContactApply entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "DtContactApply 不存在");
        this.setDefaultValue(entity);
        return entity;
    }

    @Override
    public DtContactApply getOne(Wrapper<DtContactApply> queryWrapper,
                                boolean throwEx) {
        DtContactApply entity = null;
        List<DtContactApply> list = this.list(queryWrapper);
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
