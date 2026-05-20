package pub.module.distribution.api.service;

import pub.module.distribution.api.service.dto.WalWithdrawApplyDTO;
import pub.module.distribution.api.service.dto.WalWithdrawRecordDTO;

import java.util.List;

/**
 * 分销钱包与提现 API。
 */
public interface ApiDistWalletService {

    void applyWithdraw(String userCode, String distBizLineCode, WalWithdrawApplyDTO dto);

    List<WalWithdrawRecordDTO> listWithdrawRecords(String userCode, String distBizLineCode);
}
