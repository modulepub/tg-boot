package pub.module.dating.crud.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.common.model.po.BaseEntity;
import pub.module.dating.crud.entity.DtCustomerProfileAudit;
import pub.module.dating.crud.mapper.DtCustomerProfileAuditMapper;
import pub.module.dating.crud.service.DtCustomerProfileAuditService;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class DtCustomerProfileAuditServiceImpl
        extends ServiceImpl<DtCustomerProfileAuditMapper, DtCustomerProfileAudit>
        implements DtCustomerProfileAuditService {

    private static final String BIZ_CODE = "cusProfileAuditCode";

    @Override
    public DtCustomerProfileAudit getByCode(String cusProfileAuditCode) {
        if (StrUtil.isBlank(cusProfileAuditCode)) {
            return null;
        }
        return getBaseMapper().selectOne(
                new QueryWrapper<DtCustomerProfileAudit>().eq(StrUtil.toUnderlineCase(BIZ_CODE), cusProfileAuditCode.trim()),
                false);
    }

    @Override
    public List<DtCustomerProfileAudit> listByCusUserCode(String cusUserCode) {
        if (StrUtil.isBlank(cusUserCode)) {
            return Collections.emptyList();
        }
        QueryWrapper<DtCustomerProfileAudit> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(DtCustomerProfileAudit::getCusUserCode, cusUserCode.trim())
                .orderByAsc(DtCustomerProfileAudit::getCusProfileAuditFieldName)
                .orderByAsc(DtCustomerProfileAudit::getCusProfileAuditFieldItemIndex);
        return list(queryWrapper);
    }

    @Override
    public DtCustomerProfileAudit getByCmRecordCode(String cmRecordCode) {
        if (StrUtil.isBlank(cmRecordCode)) {
            return null;
        }
        QueryWrapper<DtCustomerProfileAudit> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DtCustomerProfileAudit::getCmRecordCode, cmRecordCode.trim());
        return getOne(queryWrapper, false);
    }

    @Override
    public List<DtCustomerProfileAudit> listByCmRecordCode(String cmRecordCode) {
        if (StrUtil.isBlank(cmRecordCode)) {
            return Collections.emptyList();
        }
        QueryWrapper<DtCustomerProfileAudit> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DtCustomerProfileAudit::getCmRecordCode, cmRecordCode.trim());
        return list(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeByCusUserCodeAndField(String cusUserCode, String fieldName) {
        if (StrUtil.isBlank(cusUserCode) || StrUtil.isBlank(fieldName)) {
            return;
        }
        QueryWrapper<DtCustomerProfileAudit> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(DtCustomerProfileAudit::getCusUserCode, cusUserCode.trim())
                .eq(DtCustomerProfileAudit::getCusProfileAuditFieldName, fieldName.trim());
        remove(queryWrapper);
    }

    private void setDefaultValue(DtCustomerProfileAudit entity) {
        Field declaredField = ReflectUtil.getField(entity.getClass(), BIZ_CODE);
        Assert.notNull(declaredField, "业务编码字段未配置");
        if (ReflectUtil.getFieldValue(entity, declaredField) == null) {
            ReflectUtil.setFieldValue(entity, declaredField, IdUtil.getSnowflakeNextIdStr());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(DtCustomerProfileAudit entity) {
        Object code = ReflectUtil.getFieldValue(entity, BIZ_CODE);
        if (code != null && StrUtil.isNotEmpty(code.toString())) {
            Assert.isNull(getByCode(code.toString()), "资料审核明细编码已存在");
        }
        setDefaultValue(entity);
        getBaseMapper().insert(entity);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(Collection<DtCustomerProfileAudit> entityList) {
        for (DtCustomerProfileAudit entity : entityList) {
            save(entity);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(DtCustomerProfileAudit entity) {
        Object code = ReflectUtil.getFieldValue(entity, BIZ_CODE);
        Assert.notNull(code, "资料审核明细编码不能为空");
        BaseEntity existing = getByCode(code.toString());
        Assert.notNull(existing, "资料审核明细不存在");
        entity.setId(existing.getId());
        getBaseMapper().updateById(entity);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable id) {
        DtCustomerProfileAudit entity = getBaseMapper().selectById(id);
        Assert.notNull(entity, "资料审核明细不存在");
        getBaseMapper().deleteById(id);
        return true;
    }
}
