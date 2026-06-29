package pub.module.im.crud.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.im.api.service.dto.ImAccountDTO;
import pub.module.im.crud.entity.ImUser;

public interface ImUserService extends IService<ImUser> {

    ImUser getByUserCode(String userCode);

    ImUser getValidSigByUserCode(String userCode);

    void saveOrUpdateAccount(ImAccountDTO accountDTO, String sdkAppId);

    void refreshUserSig(String userCode, String sdkAppId, String userSig, long expireSeconds);

    /**
     * 退出登录时清空本地缓存的 UserSig
     */
    void clearUserSigByUserCode(String userCode);

    IPage<ImUser> pageForMgt(String keyword, ImUser query, long pageNo, long pageSize);

    void incrementUnreadCount(String userCode);

    void syncUnreadCount(String userCode);
}
