package pub.module.im.biz.controller.cus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pub.module.common.model.vo.Result;
import pub.module.im.api.service.ApiImService;
import pub.module.im.api.service.dto.ImAddFriendDTO;
import pub.module.im.api.service.dto.ImAccountDTO;
import pub.module.im.biz.constants.ImSpecialUserConstants;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;

@Tag(name = "用户端-IM服务")
@RestController
@RequestMapping("/cus/im")
public class CusImController {
    private static final String WELCOME_IMAGE_URL = "https://pubpicture.oss-cn-shenzhen.aliyuncs.com/pub/20260506210643237.png";
    private static final String WELCOME_LINK_URL = "https://h5.iqingqing.net";


    @Resource
    private ApiImService apiImService;

    @Operation(summary = "用户端-初始化IM用户")
    @PostMapping("/initTencentUser")
    public Result<String> initTencentUser() {
        UserDTO currentUser = UserUtil.getCurrentSysUser();
        ImAccountDTO actualAccount = new ImAccountDTO();
        actualAccount.setUserCode(currentUser.getUserCode());
        actualAccount.setNickName(currentUser.getUserNickName());
        actualAccount.setAvatar(currentUser.getUserAvatar());
        apiImService.saveOrUpdateAccount(actualAccount);
        ImAddFriendDTO addFriendDTO = new ImAddFriendDTO();
        addFriendDTO.setFromUserCode(actualAccount.getUserCode());
        addFriendDTO.setToUserCode(ImSpecialUserConstants.SYSTEM_NOTICE_USER_CODE);
        addFriendDTO.setAddSource("AddSource_Type_Android");
        addFriendDTO.setAddWording("系统通知");
        apiImService.addFriend(addFriendDTO);
        apiImService.sendC2CRichMessage(
                ImSpecialUserConstants.SYSTEM_NOTICE_USER_CODE,
                actualAccount.getUserCode(),
                "欢迎回来！",
                "https://pubpicture.oss-cn-shenzhen.aliyuncs.com/pub/20260506211303373.png",
                WELCOME_LINK_URL
        );
        return Result.ok(apiImService.generateUserSig(actualAccount.getUserCode()));
    }
}
