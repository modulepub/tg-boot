package pub.module.contract.api.service.dto;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ContractUserInputDTO implements Serializable {
    private String contractNo;
    private Integer isNoticeComplete;
    private String account;
    private String companyName;
    private String noticeEmail;
    private Integer autoSwitch;
    private String redirectUrl;
    private String noticeMobile;
    private Integer isNotice;
    private String signNoticeMobile;
    private Integer signType;
    private Integer signStatus;
    private String authAccount;
    private Integer validateType;
    private String validateTypeList;
    private Integer signOrder;
    private String sealNo;
    private Integer waterMark;
    private String position;
    private String denyReason;
    private String signPwd;
    private Integer needPwdSignH5;
    private Integer isWrite;
    private List<UserSignStrategyInputDTO> signStrategyList;
    private List<UserSignStrikeInputDTO> signStrikeList;
    private Integer faceAuthMode;
    private Integer autoSms;
    private String authSignAccount;
    private Integer customSignFlag;
    private Integer isIframe;
    private Integer isFixed;
    private String signMark;
    private String signKey;


}
