package pub.module.im.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.im.crud.entity.ImUserSession;

import java.util.List;

public interface ImUserSessionService extends IService<ImUserSession> {

    List<ImUserSession> listByUserCode(String userCode);

    ImUserSession getByUserAndClient(String userCode, String clientId);
}
