package pub.module.dict.curd.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.dict.curd.entity.DictArea;

/**
 * 地区字典
 */
public interface DictAreaService extends IService<DictArea> {

    /**
     * 分页查询地区。
     * <ul>
     *   <li>keyword 非空：按中文名、英文名、全路径模糊检索（全局）</li>
     *   <li>keyword 为空且 parentCode 非空：列出该父级编码下的子地区</li>
     *   <li>二者皆空：列出根节点（通常为国家级）</li>
     * </ul>
     */
    IPage<DictArea> searchArea(String keyword, String parentCode, Integer pageNo, Integer pageSize);
}
