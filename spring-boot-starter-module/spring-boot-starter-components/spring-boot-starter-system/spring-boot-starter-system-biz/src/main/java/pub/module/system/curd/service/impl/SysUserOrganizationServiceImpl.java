package pub.module.system.curd.service.impl;

import lombok.extern.slf4j.Slf4j;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.IdUtil;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import pub.module.system.curd.entity.SysUserOrganization;
import pub.module.system.curd.mapper.SysUserOrganizationMapper;
import pub.module.system.curd.service.SysUserOrganizationService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.lang.reflect.Field;

import cn.hutool.core.util.ReflectUtil;


/**
 * 用户所属组织机构 Service
 *
 * @author tg
 * 2026-01-04 13:16:25
 */
@Slf4j
@Service
public class SysUserOrganizationServiceImpl extends ServiceImpl<SysUserOrganizationMapper, SysUserOrganization> implements SysUserOrganizationService {

    String bizCode = "userOrgCode";

    public void setDefaultValue(SysUserOrganization entity) {
        Field declaredField = ReflectUtil.getField(entity.getClass(), bizCode);
        Assert.notNull(declaredField,"CODE 字段名称未設置");
        if (ReflectUtil.getFieldValue(entity, declaredField) == null) {
            ReflectUtil.setFieldValue(entity, declaredField, IdUtil.getSnowflakeNextIdStr());
        }
    }

    @Override
    public SysUserOrganization getByCode(String code) {
        return this.getBaseMapper().selectOne(new QueryWrapper<SysUserOrganization>().eq(StrUtil.toUnderlineCase(bizCode), code),false);
    }

    @Override
    @Transactional
    public boolean save(SysUserOrganization entity) {
        this.setDefaultValue(entity);
        this.getBaseMapper().insert(entity);

        return true;
    }

    @Override
    @Transactional
    public boolean saveBatch(Collection<SysUserOrganization> entityList) {
        for ( SysUserOrganization entity : entityList) {
            this.save(entity);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
        SysUserOrganization entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "SysUserOrganization 不存在");
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
    public boolean updateById(SysUserOrganization entity) {
        this.getBaseMapper().updateById(entity);

        return true;
    }

    @Override
    public SysUserOrganization getById(Serializable id) {
        SysUserOrganization entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "SysUserOrganization 不存在");
        this.setDefaultValue(entity);
        return entity;
    }

    @Override
    public SysUserOrganization getOne(Wrapper<SysUserOrganization> queryWrapper,
                                boolean throwEx) {
        SysUserOrganization entity = null;
        List<SysUserOrganization> list = this.list(queryWrapper);
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
