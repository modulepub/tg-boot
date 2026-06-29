package pub.module.cms.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import org.springframework.util.Assert;
import pub.module.cms.api.dto.CmsNodeTreeDTO;
import pub.module.cms.crud.entity.CmsNode;
import pub.module.cms.crud.mapper.CmsNodeMapper;
import pub.module.cms.crud.service.CmsNodeService;
import pub.module.cms.biz.service.*;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Api CMS-节点 Service
 *
 * @author tg
 * 2026-03-08 16:04:07
 */
@Service
public class SpiCmsNodeServiceImpl implements SpiCmsNodeService {


    @Override
    public CmsNodeTreeDTO buildTree(String nodeCode, List<CmsNodeTreeDTO> list) {
        Assert.notNull(nodeCode, "nodeCode 不能为空");
        CmsNodeTreeDTO top = null;
        Map<String, CmsNodeTreeDTO> cmsNodeTreeDTOMap = new HashMap<>();
        for (CmsNodeTreeDTO dto : list) {
            cmsNodeTreeDTOMap.put(dto.getNodeCode(), dto);
        }
        // 组装树状结构
        for (CmsNodeTreeDTO dto : list) {
            if (dto.getNodeCode().equals(nodeCode)) {
                top = dto;
            }
            String parentCode = dto.getNodeParentCode();
            // 如果找到父节点，并且父节点在权限列表中
            if (cmsNodeTreeDTOMap.containsKey(parentCode)) {
                CmsNodeTreeDTO parentDto = cmsNodeTreeDTOMap.get(parentCode);
                if (parentDto.getChildren() == null) {
                    parentDto.setChildren(new ArrayList<>());
                }
                parentDto.getChildren().add(dto);
            }
        }
        return top;
    }

}
