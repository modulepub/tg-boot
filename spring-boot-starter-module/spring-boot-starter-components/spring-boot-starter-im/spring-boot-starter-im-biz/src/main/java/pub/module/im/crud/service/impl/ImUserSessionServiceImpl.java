package pub.module.im.crud.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pub.module.im.crud.entity.ImUserSession;
import pub.module.im.crud.mapper.ImUserSessionMapper;
import pub.module.im.crud.service.ImUserSessionService;

import java.util.List;

@Service
public class ImUserSessionServiceImpl extends ServiceImpl<ImUserSessionMapper, ImUserSession> implements ImUserSessionService {

    @Override
    public List<ImUserSession> listByUserCode(String userCode) {
        if (StrUtil.isBlank(userCode)) {
            return List.of();
        }
        return list(new QueryWrapper<ImUserSession>()
                .eq("im_user_session_user_code", userCode.trim())
                .eq("deleted", 0)
                .orderByDesc("create_time"));
    }

    @Override
    public ImUserSession getByUserAndClient(String userCode, String clientId) {
        if (StrUtil.isBlank(userCode) || StrUtil.isBlank(clientId)) {
            return null;
        }
        return getBaseMapper().selectOne(new QueryWrapper<ImUserSession>()
                .eq("im_user_session_user_code", userCode.trim())
                .eq("im_user_session_client_id", clientId.trim())
                .eq("deleted", 0), false);
    }
}
