package pub.module.cms.curd.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import lombok.extern.slf4j.Slf4j;

import pub.module.cms.curd.entity.CmsChannel;
import pub.module.cms.curd.mapper.CmsChannelMapper;
import pub.module.cms.curd.service.ICmsChannelService;
import cn.hutool.core.util.IdUtil;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import jakarta.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.lang.reflect.Field;

import cn.hutool.core.util.ReflectUtil;


/**
 * CMS栏目
 *
 * @author tg
 * @version V1.0
 * @since 2025-09-29
 */
@Slf4j
@Service
public class CmsChannelServiceImpl extends ServiceImpl<CmsChannelMapper, CmsChannel> implements ICmsChannelService {

    @Resource
    ApplicationEventPublisher applicationEventPublisher;

    public void setDefaultValue(CmsChannel entity) {
        Field declaredField = ReflectUtil.getField(entity.getClass(), "ccCode");
        Assert.notNull(declaredField, "CODE 字段名称未設置");
        if (ReflectUtil.getFieldValue(entity, declaredField) == null) {
            ReflectUtil.setFieldValue(entity, declaredField, IdUtil.getSnowflakeNextIdStr());
        }

        if (StrUtil.isNotEmpty(entity.getCcParentCode())) {
            CmsChannel cmsChannel = this.getOne(new QueryWrapper<CmsChannel>().lambda().eq(CmsChannel::getCcCode, entity.getCcParentCode()), false);
            Assert.notNull(cmsChannel, "cms channel can not be null,please check if pccCode is exist");
            entity.setCcParentName(cmsChannel.getCcName());
        }

    }


    @Override
    @Transactional
    public boolean save(CmsChannel entity) {
        this.setDefaultValue(entity);
        this.getBaseMapper().insert(entity);

        return true;
    }

    @Override
    @Transactional
    public boolean saveBatch(Collection<CmsChannel> entityList) {
        for (CmsChannel entity : entityList) {
            this.save(entity);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
        CmsChannel entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "CmsChannel不存在");
        this.getBaseMapper().deleteById(id);
        //级联删除
        this.remove(new QueryWrapper<CmsChannel>().lambda().eq(CmsChannel::getCcParentCode, entity.getCcCode()));
        
        return true;
    }

    @Transactional
    @Override
    public boolean removeByIds(Collection<?> ids) {
        ids.forEach(entity -> {
            this.removeById((Serializable) entity);
        });
        return true;
    }

    @Override
    @Transactional
    public boolean updateById(CmsChannel entity) {
        this.getBaseMapper().updateById(entity);
        
        return true;
    }

    @Override
    public CmsChannel getById(Serializable id) {
        CmsChannel entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "CmsChannel不存在");
        this.setDefaultValue(entity);
        return entity;
    }

    @Override
    public CmsChannel getOne(Wrapper<CmsChannel> queryWrapper,
                             boolean throwEx) {
        CmsChannel entity = null;
        List<CmsChannel> list = this.list(queryWrapper);
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
