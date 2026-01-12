package pub.module.finance.biz.config;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pub.module.finance.api.constants.FcProductTypeCodeEnum;
import pub.module.finance.curd.entity.FcProduct;
import pub.module.finance.curd.service.IFcProductService;

import jakarta.annotation.Resource;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 微信支付配置
 */
@Configuration
@ConditionalOnClass(WxPayService.class)
public class WxPayConfiguration {
    @Resource
    IFcProductService fcProductService;
    String privateKeyPath =  "classpath:apiclient_key.pem";
    String privateCertPath = "classpath:apiclient_cert.pem";

    @Bean
    public WxPayService wxPayService() {
        return new WxPayServiceImpl();
    }

    public WxPayService getWxPayService() {
        WxPayService wxPayService = SpringUtil.getBean(WxPayService.class);
        if(wxPayService.getConfig() == null){
            List<FcProduct> configs = fcProductService.list(new QueryWrapper<FcProduct>().lambda().eq(FcProduct::getFcProductTypeCode, FcProductTypeCodeEnum.WECHAT.getCode()));
            if (!configs.isEmpty()) {
                wxPayService.setMultiConfig(
                        configs.stream()
                                .map(fcProduct -> {
                                    JSONObject jsonObject = JSONUtil.parseObj(fcProduct.getFcProductConfigJson());
                                    WxPayConfig payConfig = new WxPayConfig();
                                    payConfig.setAppId(jsonObject.getStr("appId"));
                                    payConfig.setMchId(jsonObject.getStr("mchId"));
                                    payConfig.setNotifyUrl(jsonObject.getStr("notifyUrl"));
                                    payConfig.setPrivateCertPath(privateCertPath);
                                    payConfig.setPrivateKeyPath(privateKeyPath);
                                    payConfig.setApiV3Key(jsonObject.getStr("mchKey"));
                                    // 可以指定是否使用沙箱环境
                                    payConfig.setUseSandboxEnv(false);
                                    // 使用上面的配置时，需要同时引入jedis-lock的依赖，否则会报类无法找到的异常
                                    return payConfig;
                                }).collect(Collectors.toMap(WxPayConfig::getAppId, a -> a, (o, n) -> o)));
            }
        }
        return wxPayService;
    }
}