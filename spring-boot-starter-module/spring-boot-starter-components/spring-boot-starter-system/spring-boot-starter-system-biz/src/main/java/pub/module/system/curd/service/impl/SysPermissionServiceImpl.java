package pub.module.system.curd.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.IdUtil;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import pub.module.data.entity.BaseEntity;
import pub.module.system.curd.entity.SysPermission;
import pub.module.system.curd.mapper.SysPermissionMapper;
import pub.module.system.curd.service.SysPermissionService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.lang.reflect.Field;

import cn.hutool.core.util.ReflectUtil;


/**
 * 菜单管理 Service
 *
 * @author tg
 * 2026-01-04 13:16:23
 */
@Slf4j
@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {

    String bizCode = "perCode";

    public void setDefaultValue(SysPermission entity) {
        Field declaredField = ReflectUtil.getField(entity.getClass(), bizCode);
        Assert.notNull(declaredField, "CODE 字段名称未設置");
        if (StrUtil.isEmpty((CharSequence) ReflectUtil.getFieldValue(entity, declaredField))) {
            ReflectUtil.setFieldValue(entity, declaredField, IdUtil.getSnowflakeNextIdStr());
        }
    }


    @Override
    public SysPermission getByCode(String code) {
        return this.getBaseMapper().selectOne(new QueryWrapper<SysPermission>().eq(StrUtil.toUnderlineCase(bizCode), code), false);
    }

    @Override
    @Transactional
    public boolean save(SysPermission entity) {
        Assert.isNull(this.getByCode(ReflectUtil.getFieldValue(entity, bizCode).toString()), "编码已存在");
        this.setDefaultValue(entity);
        this.getBaseMapper().insert(entity);

        return true;
    }

    @Override
    @Transactional
    public boolean saveBatch(Collection<SysPermission> entityList) {
        for (SysPermission entity : entityList) {
            this.save(entity);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
        SysPermission entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "SysPermission 不存在");
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
    public boolean updateById(SysPermission entity) {
        SysPermission target = this.getByCode(ReflectUtil.getFieldValue(entity, bizCode).toString());
        Assert.isFalse(target!= null && !target.getId().equals(entity.getId()), "编码已存在");
        SysPermission oldEntity = this.getBaseMapper().selectById(entity.getId());
        this.update(new UpdateWrapper<SysPermission>().lambda().set(SysPermission::getPerParentCode, entity.getPerCode()).eq(SysPermission::getPerParentCode, oldEntity.getPerCode()));
        this.getBaseMapper().updateById(entity);
        return true;
    }

    @Override
    public SysPermission getById(Serializable id) {
        SysPermission entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "SysPermission 不存在");
        this.setDefaultValue(entity);
        return entity;
    }

    @Override
    public SysPermission getOne(Wrapper<SysPermission> queryWrapper,
                                boolean throwEx) {
        SysPermission entity = null;
        List<SysPermission> list = this.list(queryWrapper);
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
