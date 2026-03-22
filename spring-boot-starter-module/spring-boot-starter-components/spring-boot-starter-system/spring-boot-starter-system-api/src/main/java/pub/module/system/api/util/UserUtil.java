package pub.module.system.api.util;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.service.dto.UserDTO;

@Slf4j
public class UserUtil {
    public static UserDTO getCurrentSysUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return SpringUtil.getBean(ApiSysUserService.class).getUserByUserName(auth.getName());
    }




}