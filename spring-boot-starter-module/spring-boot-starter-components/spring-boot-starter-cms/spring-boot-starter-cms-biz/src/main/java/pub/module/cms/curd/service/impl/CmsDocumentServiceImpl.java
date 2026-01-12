package pub.module.cms.curd.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import pub.module.cms.curd.entity.CmsChannel;
import pub.module.cms.curd.service.ICmsChannelService;

import pub.module.cms.curd.entity.CmsDocument;
import pub.module.cms.curd.mapper.CmsDocumentMapper;
import pub.module.cms.curd.service.ICmsDocumentService;
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
 * CMS文档
 * @author tg
 * @since 2025-09-29
 * @version V1.0
 */
@Slf4j
@Service
public class CmsDocumentServiceImpl extends ServiceImpl<CmsDocumentMapper, CmsDocument> implements ICmsDocumentService {

    @Resource
    ApplicationEventPublisher applicationEventPublisher;
    @Resource
    ICmsChannelService cmsChannelService;

    public void setDefaultValue(CmsDocument entity) {
        Field declaredField = ReflectUtil.getField(entity.getClass(), "cdCode");
           Assert.notNull(declaredField,"CODE 字段名称未設置");
        if (ReflectUtil.getFieldValue(entity, declaredField) == null) {
            ReflectUtil.setFieldValue(entity, declaredField, IdUtil.getSnowflakeNextIdStr());
        }
        if(StrUtil.isNotEmpty(entity.getCcCode())){
            CmsChannel cmsChannel = cmsChannelService.getOne(new QueryWrapper<CmsChannel>().lambda().eq(CmsChannel::getCcCode, entity.getCcCode()),false);
            Assert.notNull(cmsChannel,"cms channel can not be null");
            entity.setCcName(cmsChannel.getCcName());
        }
    }


    @Override
    @Transactional
    public boolean save(CmsDocument entity) {
        this.setDefaultValue(entity);
        this.getBaseMapper().insert(entity);
        
        return true;
    }

    @Override
    @Transactional
    public boolean saveBatch(Collection<CmsDocument> entityList) {
        for ( CmsDocument entity : entityList) {
            this.save(entity);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
         CmsDocument entity = this.getBaseMapper().selectById(id);
         Assert.notNull(entity, "CmsDocument不存在");
         this.getBaseMapper().deleteById(id);
         
         return true;
     }

    @Override
    @Transactional
    public boolean updateById(CmsDocument entity) {
        this.getBaseMapper().updateById(entity);
        
        return true;
    }

    @Override
    public CmsDocument getById(Serializable id) {
        CmsDocument entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "CmsDocument不存在");
        this.setDefaultValue(entity);
        return entity;
    }

    @Override
    public CmsDocument getOne(Wrapper<CmsDocument> queryWrapper,
                          boolean throwEx) {
        CmsDocument entity = null;
        List<CmsDocument> list = this.list(queryWrapper);
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
