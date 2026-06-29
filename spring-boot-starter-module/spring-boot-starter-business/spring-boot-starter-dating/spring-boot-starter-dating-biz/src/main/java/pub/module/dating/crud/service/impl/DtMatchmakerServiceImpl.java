package pub.module.dating.crud.service.impl;

import lombok.extern.slf4j.Slf4j;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.IdUtil;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import pub.module.common.enums.StatusCodeEnum;
import pub.module.common.model.po.BaseEntity;
import pub.module.dating.crud.entity.DtMatchmaker;
import pub.module.dating.crud.mapper.DtMatchmakerMapper;
import pub.module.dating.crud.service.DtMatchmakerService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.lang.reflect.Field;

import cn.hutool.core.util.ReflectUtil;


/**
 * 红娘信息 Service
 *
 * @author tg
 * 2026-03-22 13:32:44
 */
@Slf4j
@Service
public class DtMatchmakerServiceImpl extends ServiceImpl<DtMatchmakerMapper, DtMatchmaker> implements DtMatchmakerService {

    String bizCode = "mkCode";

    public void setDefaultValue(DtMatchmaker entity) {
        Field declaredField = ReflectUtil.getField(entity.getClass(), bizCode);
        Assert.notNull(declaredField,"CODE 字段名称未設置");
        if (ReflectUtil.getFieldValue(entity, declaredField) == null||StrUtil.isEmpty(ReflectUtil.getFieldValue(entity, declaredField).toString())) {
            ReflectUtil.setFieldValue(entity, declaredField, "MK"+ IdUtil.getSnowflakeNextIdStr());
        }
        if(entity.getMkScore()==null){
            entity.setMkScore(new BigDecimal("4.5"));
        }
    }

    @Override
    public DtMatchmaker getByCode(String code) {
        return this.getBaseMapper().selectOne(new QueryWrapper<DtMatchmaker>().eq(StrUtil.toUnderlineCase(bizCode), code),false);
    }

    @Override
    public DtMatchmaker getByUserCode(String userCode) {
        if (StrUtil.isBlank(userCode)) {
            return null;
        }
        return this.getOne(new QueryWrapper<DtMatchmaker>()
                .eq("mk_user_code", userCode.trim())
                .orderByDesc("create_time"), false);
    }

    @Override
    public List<String> listDistinctMkCityNames() {
        List<Object> objs = this.getBaseMapper().selectObjs(
                new QueryWrapper<DtMatchmaker>()
                        .select("mk_city_name")
                        .eq("mk_identity_status_code", StatusCodeEnum.YES.getCode())
                        .groupBy("mk_city_name")
                        .isNotNull("mk_city_name")
                        .ne("mk_city_name", "")
                        .orderByAsc("mk_city_name")
        );
        return objs.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .map(String::trim)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean save(DtMatchmaker entity) {
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
    public boolean saveBatch(Collection<DtMatchmaker> entityList) {
        for ( DtMatchmaker entity : entityList) {
            this.save(entity);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
        DtMatchmaker entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "DtMatchmaker 不存在");
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
    public boolean updateById(DtMatchmaker entity) {
        BaseEntity target = this.getByCode(ReflectUtil.getFieldValue(entity, bizCode).toString());
        Assert.isFalse(target!= null && !target.getId().equals(entity.getId()), "编码已存在");
        this.getBaseMapper().updateById(entity);

        return true;
    }

    @Override
    public DtMatchmaker getById(Serializable id) {
        DtMatchmaker entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "DtMatchmaker 不存在");
        this.setDefaultValue(entity);
        return entity;
    }

    @Override
    public DtMatchmaker getOne(Wrapper<DtMatchmaker> queryWrapper,
                                boolean throwEx) {
        DtMatchmaker entity = null;
        List<DtMatchmaker> list = this.list(queryWrapper);
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
