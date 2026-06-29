package pub.module.affines.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.affines.crud.entity.AfChildProfile;

import java.util.Collection;

public interface AfChildProfileService extends IService<AfChildProfile> {

    AfChildProfile getByCode(String afChildProfileCode);

    boolean removeByBizCodes(Collection<String> afChildProfileCodes);
}
