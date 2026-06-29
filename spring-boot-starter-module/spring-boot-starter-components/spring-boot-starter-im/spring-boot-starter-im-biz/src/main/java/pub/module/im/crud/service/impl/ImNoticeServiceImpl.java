package pub.module.im.crud.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.im.api.constants.ImNoticePublishStateCodeEnum;
import pub.module.im.api.constants.ImNoticeTargetTypeCodeEnum;
import pub.module.im.crud.entity.ImNotice;
import pub.module.im.crud.mapper.ImNoticeMapper;
import pub.module.im.crud.service.ImNoticeService;

import java.io.Serializable;
import java.lang.reflect.Field;

@Slf4j
@Service
public class ImNoticeServiceImpl extends ServiceImpl<ImNoticeMapper, ImNotice> implements ImNoticeService {

    private static final String BIZ_CODE = "imNoticeCode";

    @Override
    public ImNotice getByCode(String imNoticeCode) {
        if (StrUtil.isBlank(imNoticeCode)) {
            return null;
        }
        return getBaseMapper().selectOne(new QueryWrapper<ImNotice>()
                .eq("im_notice_code", imNoticeCode.trim())
                .eq("deleted", 0), false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(ImNotice entity) {
        Assert.notNull(entity, "通知不能为空");
        setDefaultCode(entity);
        if (entity.getImNoticePublishStateCode() == null) {
            entity.setImNoticePublishStateCode(ImNoticePublishStateCodeEnum.DRAFT);
        }
        if (entity.getImNoticeTargetTypeCode() == null) {
            entity.setImNoticeTargetTypeCode(ImNoticeTargetTypeCodeEnum.ALL);
        }
        if (entity.getImNoticeSendCount() == null) {
            entity.setImNoticeSendCount(0);
        }
        if (entity.getImNoticeFailCount() == null) {
            entity.setImNoticeFailCount(0);
        }
        return getBaseMapper().insert(entity) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(ImNotice entity) {
        Assert.notNull(entity.getId(), "ID不能为空");
        ImNotice existing = getBaseMapper().selectById(entity.getId());
        Assert.notNull(existing, "通知不存在");
        Assert.isTrue(ImNoticePublishStateCodeEnum.DRAFT.equals(existing.getImNoticePublishStateCode())
                        || existing.getImNoticePublishStateCode() == null,
                "已发送的通知不可编辑");
        return getBaseMapper().updateById(entity) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable id) {
        ImNotice row = getBaseMapper().selectById(id);
        Assert.notNull(row, "通知不存在");
        row.setDeleted(1);
        return getBaseMapper().updateById(row) > 0;
    }

    private void setDefaultCode(ImNotice entity) {
        Field field = ReflectUtil.getField(ImNotice.class, BIZ_CODE);
        Assert.notNull(field, "imNoticeCode 字段未配置");
        Object val = ReflectUtil.getFieldValue(entity, field);
        if (val == null || StrUtil.isBlank(String.valueOf(val))) {
            ReflectUtil.setFieldValue(entity, field, "IMN" + IdUtil.getSnowflakeNextIdStr());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markPublished(String id, int successCount, int failCount) {
        Assert.notBlank(id, "id is null");
        ImNotice row = new ImNotice();
        row.setId(id);
        row.setImNoticePublishStateCode(ImNoticePublishStateCodeEnum.SENT);
        row.setImNoticeSendCount(successCount);
        row.setImNoticeFailCount(failCount);
        getBaseMapper().updateById(row);
    }
}
