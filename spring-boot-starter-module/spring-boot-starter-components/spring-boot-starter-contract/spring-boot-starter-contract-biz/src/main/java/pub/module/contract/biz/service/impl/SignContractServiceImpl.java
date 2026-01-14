package pub.module.contract.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.ancun.netsign.client.NetSignClient;
import com.ancun.netsign.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pub.module.contract.api.service.BizSignContractService;
import pub.module.contract.api.service.dto.ContractUserInputDTO;
import pub.module.contract.biz.service.NetSignClientService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SignContractServiceImpl implements BizSignContractService {

    /**
     * Creates and initializes contract; sends for creation; returns contract details
     */
    @Override
    public BizSignContractService.ContractDTO createContract(String appId,String contractNo,String templateNo, String contractName, Map<String, String> fillData) {
        String notifyUrl = "";
        ContractInput contractInput = new ContractInput();
        contractInput.setNotifyUrl(notifyUrl);
        contractInput.setContractNo(contractNo);
        contractInput.setContractName(contractName);
        contractInput.setSignOrder(1);
        contractInput.setRefuseOn(1);
        contractInput.setValidityTime(10);
        List<ContractInput.Template> templates = new ArrayList<>();
        ContractInput.Template template = new ContractInput.Template();
        template.setTemplateNo(templateNo);
        template.setFillData(fillData);
        templates.add(template);
        contractInput.setTemplates(templates);
        ApiRespBody<ContractOutput> respBody = SpringUtil.getBean(NetSignClientService.class).getNetSignClient(appId).createContract(contractInput);
        if(!respBody.success()){
            throw new RuntimeException("创建合同失败！");
        }
        BizSignContractService.ContractDTO result = new BizSignContractService.ContractDTO();
        result.setContractCode(contractInput.getContractNo());
        result.setContractName(contractInput.getContractName());
        return result;
    }

    @Override
    public void getContractInfo(String appId,String contractCode) {
        ApiRespBody<?> respBody = SpringUtil.getBean(NetSignClientService.class).getNetSignClient(appId).getContractInfo(contractCode);
        Assert.isTrue(respBody.success(),"获取合同信息请求失败:{}",respBody.getMsg());
        log.info("获取合同信息：{}", JSONUtil.toJsonStr(respBody.getData()));
    }

    @Override
    public File downloadContractByCode(String appId,String contractCode,String fileName) {
        //多文件下载zip，1: 合同pdf文件 2：合同单个png文件（保留pdf文件） 3：分页png压缩文件 （保留pdf文件）4：单张图片（不保留pdf文件）5：所有的分页图片（不保留pdf文件）
        ApiRespBody<DownloadContractOutput> apiRespBody = SpringUtil.getBean(NetSignClientService.class).getNetSignClient(appId).downloadContract(contractCode, 1, fileName);
        if (!apiRespBody.success()){
            throw new RuntimeException("下载合同失败！");
        }
        return FileUtil.file(fileName);
    }




    @Override
    public void sign(String appId,List<ContractUserInputDTO> signList) {
        List<ContractUserInput> contractUserInputList = new ArrayList<>();
        // Transforms and aggregates contract user inputs for signing
        for (ContractUserInputDTO item : signList) {
            ContractUserInput contractUserInput = BeanUtil.copyProperties(item, ContractUserInput.class);
            //签章位置
            List<UserSignStrategyInput> userSignStrategyInputList = BeanUtil.copyToList(item.getSignStrategyList(), UserSignStrategyInput.class);
            contractUserInput.setSignStrategyList(userSignStrategyInputList);
            //骑缝章位置
            List<UserSignStrikeInput> userSignStrikeInputs = BeanUtil.copyToList(item.getSignStrikeList(), UserSignStrikeInput.class);
            contractUserInput.setSignStrikeList(userSignStrikeInputs);

            contractUserInputList.add(contractUserInput);
        }
        ApiRespBody<ContractOutput> apiRespBody  = SpringUtil.getBean(NetSignClientService.class).getNetSignClient(appId).addSigner(contractUserInputList);
        log.info("请求签名结果：{}",apiRespBody);
    }

    /**
     * Adds signer with details; creates and sets default seal for user
     */
    @Override
    public void addStranger(String appId,AddStrangerDTO.Req req) {
        NetSignClientService netSignClientService = SpringUtil.getBean(NetSignClientService.class);
        NetSignClient netSignClient = netSignClientService.getNetSignClient(appId);
        AddStrangerInput addStrangerInput = new AddStrangerInput();
        addStrangerInput.setAccount(req.getUserCode());
        addStrangerInput.setUserType(Integer.valueOf(req.getUserTypeCode().getCode()));
        addStrangerInput.setMobile(req.getMobile());
        addStrangerInput.setName(req.getName());
        addStrangerInput.setCompanyName(req.getName());
        ApiRespBody<?> apiRespBody =  netSignClient.addStrangerV2(addStrangerInput);
        if(!apiRespBody.success()){
            throw new RuntimeException("添加个人签约用户失败:"+apiRespBody.getMsg());
        }
    }

    /**
     * Adds personal user and handles response; throws exception on failure
     */
    @Override
    public void addPersonalUser(String appId, AddPersonalUserDTO.Req req) {
        NetSignClientService netSignClientService = SpringUtil.getBean(NetSignClientService.class);
        NetSignClient netSignClient = netSignClientService.getNetSignClient(appId);
        UserInput userInput = new UserInput();
        userInput.setAccount(req.getUserCode());
        userInput.setUserType(Integer.valueOf(req.getUserTypeCode().getCode()));
        userInput.setMobile(req.getMobile());
        userInput.setName(req.getName());
        userInput.setCompanyName(req.getName());
        ApiRespBody<?> apiRespBody = netSignClient.addPersonalUser(userInput);
        if(!apiRespBody.success()){
            throw new RuntimeException("添加个人签约用户失败:"+apiRespBody.getMsg());
        }
    }

    /**
     * Adds personal user and handles response; throws exception on failure
     */
    @Override
    public PersonalIdentifyH5DTO.Res personalIdentifyH5(String appId, PersonalIdentifyH5DTO.Req req) {
        NetSignClient netSignClient = SpringUtil.getBean(NetSignClientService.class).getNetSignClient(appId);
        UserInput userInput = new UserInput();
        userInput.setName("");
        userInput.setIdCard("");
        userInput.setNeedIdentifyTwo(0);
        userInput.setIdentifyType(4);
        userInput.setFaceAuthMode(2);
        ApiRespBody<?> apiRespBody = netSignClient.personalIdentifyH5(userInput);
        if(!apiRespBody.success()){
            throw new RuntimeException("发起实名认证失败:"+apiRespBody.getMsg());
        }
        return PersonalIdentifyH5DTO.Res.builder().redirectUrl(apiRespBody.getData().toString()).build();
    }

    /**
     * Creates and sets default personal seal; handles failures
     */
    @Override
    public void makePersonSeal(String appId, MakePersonSealDTO.Req req) {
        NetSignClientService netSignClientService = SpringUtil.getBean(NetSignClientService.class);
        NetSignClient netSignClient = netSignClientService.getNetSignClient(appId);
        String sealNo = System.currentTimeMillis()+"";
        MakeSealInput makeSealInput = new MakeSealInput();
        makeSealInput.setSealNo(sealNo);
        makeSealInput.setAccount(req.getUserCode());
        makeSealInput.setSealName("个人章");
        makeSealInput.setColor(1);
        makeSealInput.setStyle(2);
        makeSealInput.setHasBorder(1);
        ApiRespBody<SealOutput> sealOutputApiRespBody = netSignClient.makePersonSeal(makeSealInput);
        if (!sealOutputApiRespBody.success()){
            throw new RuntimeException("制作个人签章失败:"+sealOutputApiRespBody.getMsg());
        }
        UserSealInput userSealInput = new UserSealInput();
        userSealInput.setSealNo(sealNo);
        userSealInput.setAccount(req.getUserCode());
        ApiRespBody<?> apiRespBody = netSignClient.setDefaultSeal(userSealInput);
        if (!apiRespBody.success()){
            throw new RuntimeException("设置默认签章失败:"+apiRespBody.getMsg());
        }
    }

}
