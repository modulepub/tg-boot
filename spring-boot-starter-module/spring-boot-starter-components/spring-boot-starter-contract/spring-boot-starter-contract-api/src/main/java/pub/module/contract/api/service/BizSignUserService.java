package pub.module.contract.api.service;

import lombok.Builder;
import lombok.Data;

public interface BizSignUserService {

    @Data
    @Builder
    class GetIdentityUrlDTO{
        @Data
        @Builder
        public static class Req{
            String realName;
            String idCardNo;
            String mobile;
            String bizCode;
            String redirectUrl;
            String notifyUrl;
            Boolean sendSms;
        }
        @Data
        @Builder
        public static class Res{
            String identityUrl;
        }

    }

    GetIdentityUrlDTO.Res getIdentifyUrl(GetIdentityUrlDTO.Req req);
    @Data
    @Builder
    class AddUserDTO{
        @Data
        @Builder
        public static class Req{
            String userCode;
            String realName;
            String idCardNo;
            String mobile;
            String serialNo;
        }
        @Data
        @Builder
        public static class Res{
            String identityUrl;
        }

    }

    AddUserDTO.Res addUser(AddUserDTO.Req req);
}
