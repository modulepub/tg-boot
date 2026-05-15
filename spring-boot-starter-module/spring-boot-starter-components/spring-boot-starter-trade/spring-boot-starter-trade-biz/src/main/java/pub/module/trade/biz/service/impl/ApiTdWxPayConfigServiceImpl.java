package pub.module.trade.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.trade.api.dto.TdWxPayConfigDTO;
import pub.module.trade.api.service.ApiTdWxPayConfigService;
import pub.module.trade.biz.config.TradeWxPayRuntimeRefresher;
import pub.module.trade.curd.entity.TdWxPayConfig;
import pub.module.trade.curd.service.ITdWxPayConfigService;

import java.util.Collection;

/**
 * 微信支付配置业务实现。
 */
@Service
public class ApiTdWxPayConfigServiceImpl implements ApiTdWxPayConfigService {

    @Resource
    private ITdWxPayConfigService tdWxPayConfigService;
    @Resource
    private TradeWxPayRuntimeRefresher tradeWxPayRuntimeRefresher;

    private void normalize(TdWxPayConfig entity) {
        if (entity.getWxPayConfigUseSandbox() == null) {
            entity.setWxPayConfigUseSandbox(0);
        }
        if (StrUtil.isBlank(entity.getWxPayConfigEnabledCode())) {
            entity.setWxPayConfigEnabledCode("1");
        }
    }

    private void validateForUpsert(TdWxPayConfig entity) {
        Assert.notBlank(entity.getWxPayConfigCode(), "wx_pay_config_code 不能为空");
        Assert.notBlank(entity.getWxPayConfigAppId(), "wx_pay_config_app_id 不能为空");
        Assert.notBlank(entity.getWxPayConfigMchId(), "wx_pay_config_mch_id 不能为空");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addAndRefreshRuntime(TdWxPayConfigDTO dto) {
        Assert.notNull(dto, "TdWxPayConfigDTO 不能为空");
        TdWxPayConfig entity = BeanUtil.copyProperties(dto, TdWxPayConfig.class);
        validateForUpsert(entity);
        normalize(entity);
        long exists = tdWxPayConfigService.lambdaQuery()
                .eq(TdWxPayConfig::getWxPayConfigCode, entity.getWxPayConfigCode())
                .count();
        Assert.isTrue(exists == 0, "微信支付配置编码已存在");
        tdWxPayConfigService.save(entity);
        tradeWxPayRuntimeRefresher.refreshFromDatabase();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAndRefreshRuntime(TdWxPayConfigDTO dto) {
        Assert.notNull(dto, "TdWxPayConfigDTO 不能为空");
        TdWxPayConfig entity = BeanUtil.copyProperties(dto, TdWxPayConfig.class);
        validateForUpsert(entity);
        normalize(entity);
        long exists = tdWxPayConfigService.lambdaQuery()
                .eq(TdWxPayConfig::getWxPayConfigCode, entity.getWxPayConfigCode())
                .count();
        Assert.isTrue(exists > 0, "微信支付配置不存在");
        tdWxPayConfigService.updateById(entity);
        tradeWxPayRuntimeRefresher.refreshFromDatabase();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeAndRefreshRuntime(Collection<String> wxPayConfigCodes) {
        Assert.notEmpty(wxPayConfigCodes, "请选择要删除的配置");
        tdWxPayConfigService.removeByIds(wxPayConfigCodes);
        tradeWxPayRuntimeRefresher.refreshFromDatabase();
    }

    @Override
    public void refreshWxPayRuntimeFromDatabase() {
        tradeWxPayRuntimeRefresher.refreshFromDatabase();
    }
}
