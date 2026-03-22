package pub.module.cms.biz.service;

import pub.module.cms.api.dto.CmsNodeTreeDTO;
import pub.module.cms.curd.entity.CmsNode;
import pub.module.cms.curd.mapper.CmsNodeMapper;
import pub.module.cms.curd.service.CmsNodeService;

import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Spi CMS-节点 Service
 *
 * @author tg
 * 2026-03-08 16:04:07
 */
@Service
public interface SpiCmsNodeService  {

    CmsNodeTreeDTO buildTree(String code, List<CmsNodeTreeDTO> list);


}
