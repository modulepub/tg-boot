package pub.module.trade.curd.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.trade.curd.entity.TdWxPayConfig;
import pub.module.trade.curd.mapper.TdWxPayConfigMapper;
import pub.module.trade.curd.service.ITdWxPayConfigService;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 微信支付配置 CRUD 实现。
 */
@Slf4j
@Service
public class TdWxPayConfigServiceImpl extends ServiceImpl<TdWxPayConfigMapper, TdWxPayConfig> implements ITdWxPayConfigService {

    @Override
    @Transactional
    public boolean save(TdWxPayConfig entity) {
        Assert.notBlank(entity.getWxPayConfigCode(), "wx_pay_config_code 不能为空");
        return super.save(entity);
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
        TdWxPayConfig entity = getBaseMapper().selectById(id);
        Assert.notNull(entity, "微信支付配置不存在");
        return super.removeById(id);
    }

    @Transactional
    @Override
    public boolean removeByIds(Collection<?> ids) {
        ids.forEach(entity -> removeById((Serializable) entity));
        return true;
    }

    @Override
    public TdWxPayConfig getById(Serializable id) {
        TdWxPayConfig entity = super.getById(id);
        Assert.notNull(entity, "微信支付配置不存在");
        return entity;
    }

    @Override
    public TdWxPayConfig getOne(Wrapper<TdWxPayConfig> queryWrapper, boolean throwEx) {
        TdWxPayConfig entity = null;
        List<TdWxPayConfig> list = list(queryWrapper);
        if (!list.isEmpty()) {
            entity = list.get(0);
        }
        if (throwEx && list.size() > 1) {
            throw new IllegalArgumentException("查询条件有误，查询到多条数据");
        }
        return entity;
    }
}
