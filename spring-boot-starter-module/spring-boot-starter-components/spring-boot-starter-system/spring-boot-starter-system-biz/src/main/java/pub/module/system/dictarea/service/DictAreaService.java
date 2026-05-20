package pub.module.system.dictarea.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.system.dictarea.entity.DictArea;

public interface DictAreaService extends IService<DictArea> {

    IPage<DictArea> searchArea(String keyword, String parentCode, Integer pageNo, Integer pageSize);
}
