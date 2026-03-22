package pub.module.contract.biz.controller.pub;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pub.module.contract.api.service.BizSignContractService;
import pub.module.contract.api.service.dto.ContractUserInputDTO;
import pub.module.contract.api.service.dto.UserSignStrategyInputDTO;
import pub.module.contract.api.service.dto.UserSignStrikeInputDTO;
import pub.module.web.vo.Result;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 合同模板
 *
 * @author tg
 * @version V1.0
 * @since 2025-12-04
 */

@Tag(name = "合同")
@RestController
@RequestMapping("/pub/contract")
@Slf4j
public class SignController {

    @Resource
    BizSignContractService contractService;


    @Data
    public static class BeginSignVO {
        String appId;
        List<ContractUserInputDTO> contractUserInputDTOList;
        public String contractTemplateCode;
        public String contractName;
    }

    /**
     * Executes contract signing; downloads and prints file size
     */
    @Operation(summary = "合同-签约")
    @PostMapping(value = "/sign")
    public Result<?> sign(@RequestBody BeginSignVO beginSignVO) {
        String contractNo = IdUtil.nanoId();
        BizSignContractService.ContractDTO contractDTO = contractService.createContract(
                beginSignVO.getAppId(),
                contractNo,
                beginSignVO.getContractTemplateCode(),
                beginSignVO.getContractName(), new HashMap<>()
        );
        for (ContractUserInputDTO contractUserInput : beginSignVO.getContractUserInputDTOList()) {
            contractUserInput.setContractNo(contractNo);
        }
        contractService.sign(beginSignVO.getAppId(), beginSignVO.contractUserInputDTOList);
        File file = contractService.downloadContractByCode(beginSignVO.getAppId(), contractNo, "E:\\temp\\" + contractDTO.getContractName() + ".pdf");
        System.err.println(FileUtil.readableFileSize(file));
        return Result.ok(contractDTO);
    }

    @Data
    public static class DownloadContractByCode {
        public String contractCode;
        public String contractName;
        public String appId;
    }

    /**
     * Downloads contract by code; confirms download success
     */
    @Operation(summary = "合同-下载")
    @PostMapping(value = "/downloadContractByCode")
    public Result<?> downloadContractByCode(@RequestBody DownloadContractByCode downloadContractByCode) {
        File file = contractService.downloadContractByCode(downloadContractByCode.getAppId(),
                downloadContractByCode.getContractCode(),
                "E:\\temp\\" + downloadContractByCode.getContractName() + ".pdf"
        );
        System.err.println(FileUtil.readableFileSize(file));
        return Result.ok("下载成功");
    }


    @Data
    public static class AddSignUser {
        String userCode;
        String realName;
        String phoneNum;
        String appId;
    }

    @PostMapping(value = "/addStranger")
    public AddSignUser addSignUser(@RequestBody AddSignUser signUser) {
        try {
            contractService.addStranger(signUser.getAppId(), BizSignContractService.AddStrangerDTO.Req.builder()
                    .userCode(signUser.getUserCode())
                    .name(signUser.getRealName())
                    .mobile(signUser.getPhoneNum())
                    .userTypeCode(BizSignContractService.UserTypeCodeEnum.P)
                    .build());
        }catch (Exception e){
            log.info(e.getMessage(),e);
        }
        // Adds signer to contract with provided details
        BizSignContractService.MakePersonSealDTO.Req req =  BizSignContractService.MakePersonSealDTO.Req.builder()
                .userCode(signUser.getUserCode()).build();
        contractService.makePersonSeal(signUser.getAppId(),req);
        return signUser;
    }

    @Data
    public  static class PersonalIdentifyH5{
        String appId;
        String userCode;
        String realName;
        String idCardNo;
        String redirectUrl;
    }

    @PostMapping(value = "/personalIdentifyH5")
    public PersonalIdentifyH5 getParam(@RequestBody PersonalIdentifyH5 personalIdentifyH5) {
        BizSignContractService.PersonalIdentifyH5DTO.Req req = BizSignContractService.PersonalIdentifyH5DTO.Req.builder()
                .userCode(personalIdentifyH5.userCode)
                .realName(personalIdentifyH5.realName)
                .idCardNo(personalIdentifyH5.idCardNo)
                .build();
        PersonalIdentifyH5 result = new PersonalIdentifyH5();
        result.setRedirectUrl(contractService.personalIdentifyH5(personalIdentifyH5.getAppId(),req).getRedirectUrl());
        return result;
    }

    @Data
    public static class GetContractInfoVO{
        String contractNo;
        String appId;
    }
    @PostMapping(value = "/getContractInfo")
    public GetContractInfoVO getContractInfo(@RequestBody GetContractInfoVO getContractInfoVO) {
        contractService.getContractInfo(getContractInfoVO.getAppId(),getContractInfoVO.getContractNo());
        return getContractInfoVO;
    }

    /**
     * Constructs and returns a detailed contract user input configuration
     */
    @PostMapping(value = "/getParam")
    public ContractUserInputDTO getParam() {
        ContractUserInputDTO result = new ContractUserInputDTO();
        result.setAccount("ASIGN914403007451853021");
        result.setSignOrder(1);
        //  2-无感知签章 3-有感知签章
        result.setSignType(2);
        // 1:短信验证码签约 2：签约密码签约
        result.setValidateType(1);
        result.setIsNotice(1);
        result.setCustomSignFlag(0);
        List<UserSignStrikeInputDTO> strikeInputList = new ArrayList<>(5);
        UserSignStrikeInputDTO userSignStrikeInput = new UserSignStrikeInputDTO();
        userSignStrikeInput.setAttachNo(1);
        userSignStrikeInput.setCrossStyle(6);
        userSignStrikeInput.setSignPage("1-0");
        result.setSignStrikeList(strikeInputList);//骑缝章策略列表
        strikeInputList.add(userSignStrikeInput);
        UserSignStrategyInputDTO userSignStrategyInput = new UserSignStrategyInputDTO();
        userSignStrategyInput.setAttachNo(1);
        userSignStrategyInput.setLocationMode(2);
        userSignStrategyInput.setSignX(0.2);
        userSignStrategyInput.setSignY(0.2);
        userSignStrategyInput.setSignPage(1);
        List<UserSignStrategyInputDTO> strategyInputList = new ArrayList<>(5);
        result.setSignStrategyList(strategyInputList);//签章策略列表
        strategyInputList.add(userSignStrategyInput);
        return result;
    }

}
