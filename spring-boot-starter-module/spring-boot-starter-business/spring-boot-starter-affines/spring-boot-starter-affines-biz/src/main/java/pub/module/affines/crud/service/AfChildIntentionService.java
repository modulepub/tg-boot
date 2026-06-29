package pub.module.affines.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.affines.crud.entity.AfChildIntention;

import java.util.Collection;

public interface AfChildIntentionService extends IService<AfChildIntention> {

    AfChildIntention getByCode(String afChildIntentionCode);

    AfChildIntention getByChildProfileCode(String afChildProfileCode);

    boolean removeByBizCodes(Collection<String> afChildIntentionCodes);
}
