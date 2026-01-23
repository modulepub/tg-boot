package pub.module.contract.biz.service;

import cn.hutool.core.lang.Assert;
import com.ancun.netsign.client.NetSignClient;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import pub.module.contract.biz.config.App;
import pub.module.contract.biz.config.AqConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NetSignClientService {
    @Resource
    AqConfig aqConfig;
    Map<String, NetSignClient> cache = new HashMap<>();

    /**
     * Retrieves or initializes NetSignClient for given appId; ensures nonempty appId
     */
    public NetSignClient getNetSignClient(String appId) {
        Assert.notEmpty(appId, "appId is not null");
        if (cache.isEmpty()) {
            this.init();
        }
        return cache.get(appId);
    }

    public void init() {
        List<App> apps = aqConfig.getApps();
        for (App item : apps) {
            cache.put(item.getAppId(),new NetSignClient(aqConfig.getUrl(), item.getAppId(), item.getPrivateKey()));
        }
    }
}