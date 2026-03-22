package pub.module.system.curd.service.impl;

import lombok.extern.slf4j.Slf4j;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.IdUtil;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import pub.module.system.curd.entity.SysOrganization;
import pub.module.system.curd.mapper.SysOrganizationMapper;
import pub.module.system.curd.service.SysOrganizationService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.lang.reflect.Field;

import cn.hutool.core.util.ReflectUtil;


/**
 * 组织机构表 Service
 *
 * @author tg
 * 2026-01-04 13:16:22
 */
@Slf4j
@Service
public class SysOrganizationServiceImpl extends ServiceImpl<SysOrganizationMapper, SysOrganization> implements SysOrganizationService {

    String bizCode = "orgCode";

    public void setDefaultValue(SysOrganization entity) {
        Field declaredField = ReflectUtil.getField(entity.getClass(), bizCode);
        Assert.notNull(declaredField,"CODE 字段名称未設置");
        if (ReflectUtil.getFieldValue(entity, declaredField) == null) {
            ReflectUtil.setFieldValue(entity, declaredField, IdUtil.getSnowflakeNextIdStr());
        }
    }

    @Override
    public SysOrganization getByCode(String code) {
        return this.getBaseMapper().selectOne(new QueryWrapper<SysOrganization>().eq(StrUtil.toUnderlineCase(bizCode), code),false);
    }

    @Override
    @Transactional
    public boolean save(SysOrganization entity) {
        this.setDefaultValue(entity);
        this.getBaseMapper().insert(entity);

        return true;
    }

    @Override
    @Transactional
    public boolean saveBatch(Collection<SysOrganization> entityList) {
        for ( SysOrganization entity : entityList) {
            this.save(entity);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
        SysOrganization entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "SysOrganization 不存在");
        this.getBaseMapper().deleteById(id);
        List<SysOrganization> childList = this.getBaseMapper().selectList(new QueryWrapper<SysOrganization>().lambda().eq(SysOrganization::getOrgParentCode, entity.getOrgCode()));
        for (SysOrganization child : childList) {
            this.removeById(child.getId());
        }
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
    public boolean updateById(SysOrganization entity) {
        this.getBaseMapper().updateById(entity);

        return true;
    }

    @Override
    public SysOrganization getById(Serializable id) {
        SysOrganization entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "SysOrganization 不存在");
        this.setDefaultValue(entity);
        return entity;
    }

    @Override
    public SysOrganization getOne(Wrapper<SysOrganization> queryWrapper,
                                boolean throwEx) {
        SysOrganization entity = null;
        List<SysOrganization> list = this.list(queryWrapper);
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
