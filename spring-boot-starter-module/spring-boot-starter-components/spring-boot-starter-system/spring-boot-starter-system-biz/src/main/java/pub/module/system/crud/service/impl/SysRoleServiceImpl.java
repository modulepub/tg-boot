package pub.module.system.crud.service.impl;

import lombok.extern.slf4j.Slf4j;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.IdUtil;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import pub.module.system.crud.entity.SysRole;
import pub.module.system.crud.mapper.SysRoleMapper;
import pub.module.system.crud.service.SysRoleService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.lang.reflect.Field;

import cn.hutool.core.util.ReflectUtil;


/**
 * 角色表 Service
 *
 * @author tg
 * 2026-01-04 13:16:23
 */
@Slf4j
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    String bizCode = "roleCode";

    public void setDefaultValue(SysRole entity) {
        Field declaredField = ReflectUtil.getField(entity.getClass(), bizCode);
        Assert.notNull(declaredField,"CODE 字段名称未設置");
        if (ReflectUtil.getFieldValue(entity, declaredField) == null) {
            ReflectUtil.setFieldValue(entity, declaredField, IdUtil.getSnowflakeNextIdStr());
        }
    }

    @Override
    public SysRole getByCode(String code) {
        return this.getBaseMapper().selectOne(new QueryWrapper<SysRole>().eq(StrUtil.toUnderlineCase(bizCode), code),false);
    }

    @Override
    @Transactional
    public boolean save(SysRole entity) {
        this.setDefaultValue(entity);
        this.getBaseMapper().insert(entity);

        return true;
    }

    @Override
    @Transactional
    public boolean saveBatch(Collection<SysRole> entityList) {
        for ( SysRole entity : entityList) {
            this.save(entity);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
        SysRole entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "SysRole 不存在");
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
    public boolean updateById(SysRole entity) {
        this.getBaseMapper().updateById(entity);

        return true;
    }

    @Override
    public SysRole getById(Serializable id) {
        SysRole entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "SysRole 不存在");
        this.setDefaultValue(entity);
        return entity;
    }

    @Override
    public SysRole getOne(Wrapper<SysRole> queryWrapper,
                                boolean throwEx) {
        SysRole entity = null;
        List<SysRole> list = this.list(queryWrapper);
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
