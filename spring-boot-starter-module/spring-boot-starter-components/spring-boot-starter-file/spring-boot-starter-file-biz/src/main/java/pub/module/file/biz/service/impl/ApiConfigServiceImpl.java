package pub.module.file.biz.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import pub.module.file.api.service.ApiConfigService;

import org.springframework.stereotype.Service;
import pub.module.file.crud.entity.BizConfig;
import pub.module.file.crud.service.BizConfigService;


/**
 * Api CMS-节点 Service
 *
 * @author tg
 * 2026-03-21 21:34:38
 */
@Service
public class ApiConfigServiceImpl implements ApiConfigService {

    @Resource
    BizConfigService bizConfigService;

    @Override
    public JSONObject getConfigByCode(String configCode) {
        BizConfig bizConfig = bizConfigService.getByCode(configCode);
        if (bizConfig == null) {
            bizConfig = new BizConfig();
            bizConfig.setConfigCode(configCode);
            bizConfig.setConfigName("初始化配置");
            bizConfigService.save(bizConfig);
        }
        JSONObject result = null;
        if (StrUtil.isNotBlank(bizConfig.getConfigContent())) {
            result = JSONUtil.parseObj(bizConfig.getConfigContent());
        }else {
            result = new JSONObject();
        }
        return result;
    }

    @Override
    public void updateConfigByCode(String configCode, JSONObject configContent) {
        UpdateWrapper<BizConfig> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(BizConfig::getConfigCode,configCode);
        updateWrapper.lambda().set(BizConfig::getConfigContent,configContent.toString());
        bizConfigService.update(updateWrapper);
    }
}
