package pub.module.finance.api.service;

import pub.module.finance.curd.entity.FcAccountLog;

/**
 * 金融账户变动日志
 * @author tg
 * @since 2025-09-30
 * @version V1.0
 */
public interface BizFcAccountLogService {

    boolean getIsPaid(String tradeNo);

    void savePayReqLog(BizPayService.PrePayDTO.Req req);

    FcAccountLog savePaidLog(String tradeNo);

    String getFcProductCodeBy(String tradeNo);
}
