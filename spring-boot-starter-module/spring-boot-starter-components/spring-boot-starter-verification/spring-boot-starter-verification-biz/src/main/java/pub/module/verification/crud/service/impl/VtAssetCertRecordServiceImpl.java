package pub.module.verification.crud.service.impl;

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
import pub.module.verification.crud.entity.VtAssetCertRecord;
import pub.module.verification.crud.mapper.VtAssetCertRecordMapper;
import pub.module.verification.crud.service.VtAssetCertRecordService;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class VtAssetCertRecordServiceImpl
        extends ServiceImpl<VtAssetCertRecordMapper, VtAssetCertRecord>
        implements VtAssetCertRecordService {

    private final String bizCode = "assetCertCode";

    public void setDefaultValue(VtAssetCertRecord entity) {
        Field declaredField = ReflectUtil.getField(entity.getClass(), bizCode);
        Assert.notNull(declaredField, "CODE 字段名称未設置");
        if (ReflectUtil.getFieldValue(entity, declaredField) == null
                || StrUtil.isEmpty(ReflectUtil.getFieldValue(entity, declaredField).toString())) {
            ReflectUtil.setFieldValue(entity, declaredField, "AC" + IdUtil.getSnowflakeNextIdStr());
        }
    }

    @Override
    public VtAssetCertRecord getByCode(String code) {
        return this.getBaseMapper().selectOne(
                new QueryWrapper<VtAssetCertRecord>().eq(StrUtil.toUnderlineCase(bizCode), code),
                false);
    }

    @Override
    @Transactional
    public boolean save(VtAssetCertRecord entity) {
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
    public boolean saveBatch(Collection<VtAssetCertRecord> entityList) {
        for (VtAssetCertRecord entity : entityList) {
            this.save(entity);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
        VtAssetCertRecord entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "VtAssetCertRecord 不存在");
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
    public boolean updateById(VtAssetCertRecord entity) {
        BaseEntity target = this.getByCode(ReflectUtil.getFieldValue(entity, bizCode).toString());
        Assert.isFalse(target != null && !target.getId().equals(entity.getId()), "编码已存在");
        this.getBaseMapper().updateById(entity);
        return true;
    }

    @Override
    public VtAssetCertRecord getById(Serializable id) {
        VtAssetCertRecord entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "VtAssetCertRecord 不存在");
        this.setDefaultValue(entity);
        return entity;
    }

    @Override
    public VtAssetCertRecord getOne(Wrapper<VtAssetCertRecord> queryWrapper, boolean throwEx) {
        VtAssetCertRecord entity = null;
        List<VtAssetCertRecord> list = this.list(queryWrapper);
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
