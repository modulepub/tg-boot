package pub.module.affines.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.affines.crud.entity.AfChildProfileView;

import java.util.Collection;

public interface AfChildProfileViewService extends IService<AfChildProfileView> {

    AfChildProfileView getByCode(String afChildProfileViewCode);

    boolean removeByBizCodes(Collection<String> afChildProfileViewCodes);
}
