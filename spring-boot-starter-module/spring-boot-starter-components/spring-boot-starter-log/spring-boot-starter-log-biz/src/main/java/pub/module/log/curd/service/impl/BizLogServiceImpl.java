package pub.module.log.curd.service.impl;

import lombok.extern.slf4j.Slf4j;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.IdUtil;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import pub.module.data.api.entity.BaseEntity;
import pub.module.log.curd.entity.BizLog;
import pub.module.log.curd.mapper.BizLogMapper;
import pub.module.log.curd.service.BizLogService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.lang.reflect.Field;

import cn.hutool.core.util.ReflectUtil;


/**
 * 日志表 Service
 *
 * @author tg
 * 2026-01-12 01:41:07
 */
@Slf4j
@Service
public class BizLogServiceImpl extends ServiceImpl<BizLogMapper, BizLog> implements BizLogService {

    String bizCode = "logCode";

    public void setDefaultValue(BizLog entity) {
        Field declaredField = ReflectUtil.getField(entity.getClass(), bizCode);
        Assert.notNull(declaredField,"CODE 字段名称未設置");
        if (ReflectUtil.getFieldValue(entity, declaredField) == null) {
            ReflectUtil.setFieldValue(entity, declaredField, IdUtil.getSnowflakeNextIdStr());
        }
    }

    @Override
    public BizLog getByCode(String code) {
        return this.getBaseMapper().selectOne(new QueryWrapper<BizLog>().eq(StrUtil.toUnderlineCase(bizCode), code),false);
    }

    @Override
    @Transactional
    public boolean save(BizLog entity) {
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
    public boolean saveBatch(Collection<BizLog> entityList) {
        for ( BizLog entity : entityList) {
            this.save(entity);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
        BizLog entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "Log 不存在");
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
    public boolean updateById(BizLog entity) {
        BaseEntity target = this.getByCode(ReflectUtil.getFieldValue(entity, bizCode).toString());
        Assert.isFalse(target!= null && !target.getId().equals(entity.getId()), "编码已存在");
        this.getBaseMapper().updateById(entity);

        return true;
    }

    @Override
    public BizLog getById(Serializable id) {
        BizLog entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "Log 不存在");
        this.setDefaultValue(entity);
        return entity;
    }

    @Override
    public BizLog getOne(Wrapper<BizLog> queryWrapper,
                         boolean throwEx) {
        BizLog entity = null;
        List<BizLog> list = this.list(queryWrapper);
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
