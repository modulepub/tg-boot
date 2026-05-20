package pub.module.cms.biz.controller.pub;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.cms.api.constants.NodePublishStatusCodeEnum;
import pub.module.cms.api.dto.CmsNodeDTO;
import pub.module.cms.curd.entity.CmsNode;
import pub.module.cms.curd.service.CmsNodeService;
import pub.module.common.util.WebQueryUtil;
import pub.module.common.model.vo.Result;


/**
 * @author tg
 * 2026-03-08 16:04:07
 */
@Tag(name = "公开-CMS内容")
@RestController
@RequestMapping("/pub/cms/cmsNode")
@Slf4j
public class PubCmsNodeController {
    @Resource
    private CmsNodeService cmsNodeService;
    
    @Operation(summary = "公开-CMS-节点分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<CmsNodeDTO>> queryPageList(CmsNode cmsNode,
                                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<CmsNode> page = new Page<>(pageNo, pageSize);
        QueryWrapper<CmsNode> queryWrapper = WebQueryUtil.buildQuery(cmsNode);
        WebQueryUtil.setSelect(queryWrapper, CmsNodeDTO.class);
        queryWrapper.lambda().eq(CmsNode::getNodePublishStatusCode, NodePublishStatusCodeEnum.YES);
        IPage<CmsNode> pageList = cmsNodeService.page(page, queryWrapper);
        IPage<CmsNodeDTO> cmsNodeDTOIPage = pageList.convert(cmsNode1 -> BeanUtil.copyProperties(cmsNode1, CmsNodeDTO.class));
        return Result.ok(cmsNodeDTOIPage);
    }
    
    @Operation(summary = "公开-CMS-节点通过id查询")
    @GetMapping(value = "/queryById")
    public Result<CmsNode> queryById(@RequestParam(name = "id") String id) {
        CmsNode cmsNode = cmsNodeService.getById(id);
        return Result.ok(cmsNode);
    }

}