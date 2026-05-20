package pub.module.verification.curd.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pub.module.verification.curd.entity.NpConfig;
import pub.module.verification.curd.mapper.NpConfigMapper;
import pub.module.verification.curd.service.NpConfigService;

/**
 * vt_np_config Service 实现
 */
@Service
public class NpConfigServiceImpl extends ServiceImpl<NpConfigMapper, NpConfig> implements NpConfigService {

    @Override
    public boolean save(NpConfig entity) {
        Assert.notBlank(entity.getNpConfigCode(), "np_config_code 不能为空");
        return super.save(entity);
    }
}
