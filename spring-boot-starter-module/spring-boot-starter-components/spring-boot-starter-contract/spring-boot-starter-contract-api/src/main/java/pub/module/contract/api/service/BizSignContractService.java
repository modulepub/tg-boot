package pub.module.contract.api.service;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pub.module.contract.api.service.dto.ContractUserInputDTO;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface BizSignContractService {
    @Data
    class ContractDTO{
        public String contractCode;
        public String contractName;
    }

    /**
     *
     * @param appId APP_ID
     * @param contractNo 合同编码
     * @param templateNo 合同模板编码
     * @param contractName 合同名称
     * @param fillData 填充数据
     * @return 返回合同实体
     */
    ContractDTO createContract(String appId,String contractNo,String templateNo, String contractName, Map<String, String> fillData);
    void getContractInfo(String appId,String contractCode);
    File downloadContractByCode(String appId,String contractCode,String fileName);
    /**
     * 开始签署流程的方法
     *
     * @param signerList 签署人列表，包含所有需要签署的人员信息
     */
    void sign(String appId, List<ContractUserInputDTO> signerList);

    @Data
    class AddStrangerDTO {
        @Data
        @Builder
        public static class Req {
            String userCode;
            String name;//企业名称或者个人名称
            private UserTypeCodeEnum userTypeCode;
            private String mobile;
        }

        @Data
        @Builder
        @NoArgsConstructor
        public static class Res {
        }

    }

    void addStranger(String appId,AddStrangerDTO.Req req);

    @Data
    class AddPersonalUserDTO {
        @Data
        @Builder
        public static class Req {
            String userCode;
            String name;//企业名称或者个人名称
            private UserTypeCodeEnum userTypeCode;
            private String mobile;
        }

        @Data
        @Builder
        @NoArgsConstructor
        public static class Res {
        }
    }
    @Getter
    enum UserTypeCodeEnum {
        P("2", "个人"),
        E("1", "企业"),
        ;

        UserTypeCodeEnum(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        final String code;
        final String desc;
    }
    void addPersonalUser(String appId,AddPersonalUserDTO.Req req);

    @Data
    class MakePersonSealDTO {
        @Data
        @Builder
        public static class Req {
            String userCode;
        }

        @Data
        @Builder
        @NoArgsConstructor
        public static class Res {
        }
    }

    void makePersonSeal(String appId, MakePersonSealDTO.Req req);

    @Data
    class PersonalIdentifyH5DTO {
        @Data
        @Builder
        public static class Req {
            String userCode;
            String realName;
            String idCardNo;
        }

        @Data
        @Builder
        public static class Res {
            String redirectUrl;
        }
    }
    PersonalIdentifyH5DTO.Res personalIdentifyH5(String appId, PersonalIdentifyH5DTO.Req req);
}
