package pub.module.affines.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.affines.crud.entity.AfParentFollow;

import java.util.Collection;

public interface AfParentFollowService extends IService<AfParentFollow> {

    AfParentFollow getByCode(String afParentFollowCode);

    AfParentFollow getByFollowerAndTarget(String followerUserCode, String targetChildProfileCode);

    boolean removeByBizCodes(Collection<String> afParentFollowCodes);
}
