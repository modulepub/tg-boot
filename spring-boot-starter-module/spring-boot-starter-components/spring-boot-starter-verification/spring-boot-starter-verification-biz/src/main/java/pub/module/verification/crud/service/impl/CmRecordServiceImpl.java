package pub.module.verification.crud.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.verification.crud.entity.CmRecord;
import pub.module.verification.crud.mapper.CmRecordMapper;
import pub.module.verification.crud.service.CmRecordService;

import java.util.Collection;

@Service
public class CmRecordServiceImpl extends ServiceImpl<CmRecordMapper, CmRecord> implements CmRecordService {

    private static final String BIZ_CODE_FIELD = "cmRecordCode";

    @Override
    public CmRecord getByCode(String cmRecordCode) {
        if (StrUtil.isBlank(cmRecordCode)) {
            return null;
        }
        return getBaseMapper().selectOne(
                new QueryWrapper<CmRecord>().eq(StrUtil.toUnderlineCase(BIZ_CODE_FIELD), cmRecordCode.trim()),
                false);
    }

    @Override
    public CmRecord getByVendorTraceId(String cmRecordVendorTraceId) {
        if (StrUtil.isBlank(cmRecordVendorTraceId)) {
            return null;
        }
        return getBaseMapper().selectOne(
                new QueryWrapper<CmRecord>().eq("cm_record_vendor_trace_id", cmRecordVendorTraceId.trim()),
                false);
    }

    @Override
    public CmRecord findReusableByContent(String sourceModuleCode, String userCode,
                                          String contentTypeCode, String content) {
        if (StrUtil.isBlank(content) || StrUtil.isBlank(contentTypeCode)) {
            return null;
        }
        QueryWrapper<CmRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cm_record_content_type_code", contentTypeCode.trim());
        queryWrapper.eq("cm_record_content", content.trim());
        if (StrUtil.isNotBlank(sourceModuleCode)) {
            queryWrapper.eq("cm_record_source_module_code", sourceModuleCode.trim());
        }
        if (StrUtil.isNotBlank(userCode)) {
            queryWrapper.eq("cm_record_user_code", userCode.trim());
        }
        queryWrapper.orderByDesc("id").last("limit 1");
        return getBaseMapper().selectOne(queryWrapper, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(CmRecord entity) {
        ensureBizCode(entity);
        getBaseMapper().insert(entity);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(CmRecord entity) {
        if (StrUtil.isNotBlank(entity.getCmRecordCode())) {
            CmRecord existing = getByCode(entity.getCmRecordCode());
            if (existing != null) {
                entity.setId(existing.getId());
            }
        }
        getBaseMapper().updateById(entity);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByBizCodes(Collection<String> cmRecordCodes) {
        if (cmRecordCodes == null || cmRecordCodes.isEmpty()) {
            return true;
        }
        for (String code : cmRecordCodes) {
            CmRecord row = getByCode(code);
            if (row != null) {
                removeById(row.getId());
            }
        }
        return true;
    }

    private static void ensureBizCode(CmRecord entity) {
        Object code = ReflectUtil.getFieldValue(entity, BIZ_CODE_FIELD);
        if (code == null || StrUtil.isBlank(code.toString())) {
            ReflectUtil.setFieldValue(entity, BIZ_CODE_FIELD, IdUtil.getSnowflakeNextIdStr());
        }
    }
}
