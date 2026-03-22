package pub.module.config.api.service;


import cn.hutool.json.JSONObject;

/**
 * Api CMS-节点 Service
 *
 * @author tg
 * 2026-03-21 21:34:38
 */
public interface ApiConfigService {

    JSONObject getConfigByCode(String configCode);
    void updateConfigByCode(String configCode,JSONObject configContent);

}
