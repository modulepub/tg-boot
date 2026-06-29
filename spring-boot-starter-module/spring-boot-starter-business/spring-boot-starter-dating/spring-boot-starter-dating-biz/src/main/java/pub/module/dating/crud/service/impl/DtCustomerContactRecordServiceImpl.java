package pub.module.dating.crud.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.common.model.po.BaseEntity;
import pub.module.dating.crud.entity.DtCustomerContactRecord;
import pub.module.dating.crud.mapper.DtCustomerContactRecordMapper;
import pub.module.dating.crud.service.DtCustomerContactRecordService;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

/**
 * 联络记录 Service
 */
@Slf4j
@Service
public class DtCustomerContactRecordServiceImpl
        extends ServiceImpl<DtCustomerContactRecordMapper, DtCustomerContactRecord>
        implements DtCustomerContactRecordService {

    private final String bizCode = "contactRecordCode";

    public void setDefaultValue(DtCustomerContactRecord entity) {
        Field declaredField = ReflectUtil.getField(entity.getClass(), bizCode);
        Assert.notNull(declaredField, "CODE 字段名称未設置");
        if (ReflectUtil.getFieldValue(entity, declaredField) == null) {
            ReflectUtil.setFieldValue(entity, declaredField, IdUtil.getSnowflakeNextIdStr());
        }
    }

    @Override
    public DtCustomerContactRecord getByCode(String code) {
        return this.getBaseMapper().selectOne(
                new QueryWrapper<DtCustomerContactRecord>().eq(StrUtil.toUnderlineCase(bizCode), code), false);
    }

    @Override
    @Transactional
    public boolean save(DtCustomerContactRecord entity) {
        Object code = ReflectUtil.getFieldValue(entity, bizCode);
        if (code != null && StrUtil.isNotEmpty(code.toString())) {
            Assert.isNull(this.getByCode(code.toString()), "编码已存在");
        }
        this.setDefaultValue(entity);
        this.getBaseMapper().insert(entity);
        return true;
    }

    @Override
    @Transactional
    public boolean saveBatch(Collection<DtCustomerContactRecord> entityList) {
        for (DtCustomerContactRecord entity : entityList) {
            this.save(entity);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
        DtCustomerContactRecord entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "DtCustomerContactRecord 不存在");
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
    public boolean updateById(DtCustomerContactRecord entity) {
        BaseEntity target = this.getByCode(ReflectUtil.getFieldValue(entity, bizCode).toString());
        Assert.isFalse(target != null && !target.getId().equals(entity.getId()), "编码已存在");
        this.getBaseMapper().updateById(entity);
        return true;
    }

    @Override
    public DtCustomerContactRecord getById(Serializable id) {
        DtCustomerContactRecord entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "DtCustomerContactRecord 不存在");
        this.setDefaultValue(entity);
        return entity;
    }

    @Override
    public DtCustomerContactRecord getOne(Wrapper<DtCustomerContactRecord> queryWrapper, boolean throwEx) {
        DtCustomerContactRecord entity = null;
        List<DtCustomerContactRecord> list = this.list(queryWrapper);
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
