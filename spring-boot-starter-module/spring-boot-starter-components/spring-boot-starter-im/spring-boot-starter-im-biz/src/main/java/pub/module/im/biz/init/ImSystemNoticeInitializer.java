package pub.module.im.biz.init;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import pub.module.im.api.service.ApiImService;
import pub.module.im.api.service.dto.ImAccountDTO;
import pub.module.im.biz.constants.ImSpecialUserConstants;

import jakarta.annotation.Resource;

/**
 * IM 系统通知用户初始化器
 */
@Slf4j
@Component
public class ImSystemNoticeInitializer implements ApplicationRunner {

    @Resource
    private ApiImService apiImService;

    @Override
    public void run(ApplicationArguments args) {
        ImAccountDTO accountDTO = new ImAccountDTO();
        accountDTO.setUserCode(ImSpecialUserConstants.SYSTEM_NOTICE_USER_CODE);
        accountDTO.setNickName(ImSpecialUserConstants.SYSTEM_NOTICE_USER_NAME);
        accountDTO.setAvatar(ImSpecialUserConstants.SYSTEM_NOTICE_AVATAR);
        try {
            apiImService.saveOrUpdateAccount(accountDTO);
            log.info("IM系统通知用户初始化成功, userCode={}", ImSpecialUserConstants.SYSTEM_NOTICE_USER_CODE);
        } catch (Exception e) {
            log.error("IM系统通知用户初始化失败, userCode={}", ImSpecialUserConstants.SYSTEM_NOTICE_USER_CODE, e);
        }
    }
}
