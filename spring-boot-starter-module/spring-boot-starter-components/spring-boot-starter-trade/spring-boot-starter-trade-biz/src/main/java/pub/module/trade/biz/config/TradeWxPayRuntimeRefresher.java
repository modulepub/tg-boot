package pub.module.trade.biz.config;

import cn.hutool.core.util.StrUtil;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import pub.module.trade.curd.entity.TdWxPayConfig;
import pub.module.trade.curd.service.ITdWxPayConfigService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 将库表 td_wx_pay_config 中启用的记录加载为 WxPayService 多商户配置。
 */
@Slf4j
@Component
@ConditionalOnClass(WxPayService.class)
public class TradeWxPayRuntimeRefresher {

    private static final String ENABLED_CODE = "1";

    @Resource
    private WxPayService wxPayService;
    @Resource
    private ITdWxPayConfigService tdWxPayConfigService;

    /**
     * 从数据库刷新 WxPay 运行时配置（线程安全由 WxPayService 保证配置替换语义）。
     */
    public synchronized void refreshFromDatabase() {
        List<TdWxPayConfig> list = tdWxPayConfigService.lambdaQuery()
                .eq(TdWxPayConfig::getWxPayConfigEnabledCode, ENABLED_CODE)
                .orderByAsc(TdWxPayConfig::getSeqNo)
                .orderByAsc(TdWxPayConfig::getWxPayConfigCode)
                .list();
        if (list.isEmpty()) {
            log.warn("td_wx_pay_config 无启用配置（wx_pay_config_enabled_code={}），WxPayService 将清空多商户配置", ENABLED_CODE);
            wxPayService.setMultiConfig(Collections.emptyMap());
            return;
        }
        Map<String, WxPayConfig> map = new LinkedHashMap<>();
        for (TdWxPayConfig row : list) {
            if (StrUtil.hasBlank(row.getWxPayConfigAppId(), row.getWxPayConfigMchId())) {
                log.warn("跳过无效微信支付配置 wx_pay_config_code={}（缺少 appId 或 mchId）", row.getWxPayConfigCode());
                continue;
            }
            WxPayConfig cfg = new WxPayConfig();
            cfg.setAppId(StrUtil.trim(row.getWxPayConfigAppId()));
            cfg.setMchId(StrUtil.trim(row.getWxPayConfigMchId()));
            cfg.setApiV3Key(StrUtil.blankToDefault(StrUtil.trim(row.getWxPayConfigApiV3Key()), ""));
            cfg.setNotifyUrl(StrUtil.blankToDefault(StrUtil.trim(row.getWxPayConfigNotifyUrl()), ""));
            configureKeyMaterial(cfg,
                    StrUtil.trim(row.getWxPayConfigPrivateKey()),
                    StrUtil.trim(row.getWxPayConfigPrivateCert()));
            boolean sandbox = row.getWxPayConfigUseSandbox() != null && row.getWxPayConfigUseSandbox() == 1;
            cfg.setUseSandboxEnv(sandbox);
            map.put(cfg.getAppId(), cfg);
        }
        wxPayService.setMultiConfig(map);
        log.info("已从 td_wx_pay_config 加载 {} 条微信支付配置到 WxPayService", map.size());
    }

    /**
     * 使用 PEM 正文配置 APIv3 私钥与证书。
     * <p>须通过 {@link WxPayConfig#setPrivateKeyContent}/{@link WxPayConfig#setPrivateCertContent} 写入 UTF-8 字节；
     * 仅用 {@link WxPayConfig#setPrivateKeyString} 时，WxJava 特定版本会先尝试 Base64 解码，遇上 PEM 头部的 {@code -}
     * 会抛出 {@code Illegal base64 character 2d}。</p>
     */
    private static void configureKeyMaterial(WxPayConfig cfg, String privateKeyPem, String privateCertPem) {
        cfg.setPrivateKeyPath(null);
        cfg.setPrivateCertPath(null);
        cfg.setPrivateKeyString(null);
        cfg.setPrivateCertString(null);

        String keyNorm = normalizePemText(privateKeyPem);
        String certNorm = normalizePemText(privateCertPem);
        cfg.setPrivateKeyContent(StrUtil.isBlank(keyNorm) ? null : keyNorm.getBytes(StandardCharsets.UTF_8));
        cfg.setPrivateCertContent(StrUtil.isBlank(certNorm) ? null : certNorm.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 去掉 BOM / 首尾空白 / 统一换行，减少对 WxJava PEM 识别的干扰。
     */
    private static String normalizePemText(String pem) {
        if (pem == null) {
            return null;
        }
        String t = StrUtil.trim(pem);
        if (t.startsWith("\uFEFF")) {
            t = t.substring(1).trim();
        }
        return t.replace("\r\n", "\n").trim();
    }
}
