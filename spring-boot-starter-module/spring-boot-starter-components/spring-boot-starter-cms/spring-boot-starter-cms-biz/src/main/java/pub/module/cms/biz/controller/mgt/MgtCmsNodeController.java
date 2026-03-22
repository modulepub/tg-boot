package pub.module.cms.biz.controller.mgt;

import java.util.Collection;
import java.util.List;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import pub.module.cms.api.constants.NodeTypeCodeEnum;
import pub.module.cms.api.dto.CmsNodeDTO;
import pub.module.cms.api.dto.CmsNodeTreeDTO;
import pub.module.cms.biz.service.SpiCmsNodeService;
import pub.module.web.vo.Result;
import pub.module.web.util.WebQueryUtil;

import pub.module.cms.curd.entity.CmsNode;
import pub.module.cms.curd.service.CmsNodeService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;



/**
 * CMS-节点 Controller
 *
 * @author tg
 *  2026-03-08 16:04:07
 */
@Tag(name="CMS-节点 CURD 处理器")
@RestController
@RequestMapping("/mgt/cms/cmsNode")
@Slf4j
public class MgtCmsNodeController{
        @Resource
        private CmsNodeService cmsNodeService;
        @Resource
        private SpiCmsNodeService spiCmsNodeService;


        @Operation(summary="CMS-文章 - 分页列表查询")
        @GetMapping(value = "/listDocument")
        public Result<IPage<CmsNodeDTO>> queryPageList(CmsNode cmsNode,
                                                       @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                       @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
            QueryWrapper<CmsNode> queryWrapper = WebQueryUtil.buildQuery(cmsNode);
            WebQueryUtil.setSelect(queryWrapper, CmsNodeDTO.class);
            queryWrapper.lambda().eq(CmsNode::getNodeTypeCode, NodeTypeCodeEnum.DOCUMENT.getCode());
            Page<CmsNode> page = new Page<>(pageNo, pageSize);
            IPage<CmsNode> pageList = cmsNodeService.page(page, queryWrapper);
            IPage<CmsNodeDTO> cmsNodeDTOIPage = pageList.convert(cmsNode1 -> BeanUtil.copyProperties(cmsNode1, CmsNodeDTO.class));
            return Result.ok(cmsNodeDTOIPage);
        }

    @Operation(summary="CMS-栏目 - 分页列表查询")
    @GetMapping(value = "/listCatalogTree")
    public Result<CmsNodeTreeDTO> listCatalogTree(String nodeCode) {
        QueryWrapper<CmsNode> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(CmsNode::getNodeTypeCode, NodeTypeCodeEnum.CATALOG.getCode());
        List<CmsNode> list = cmsNodeService.list(queryWrapper);
        return Result.ok(spiCmsNodeService.buildTree(nodeCode, BeanUtil.copyToList(list, CmsNodeTreeDTO.class)));
    }

        @Operation(summary="CMS-节点 - 添加")
        @PostMapping(value = "/add")
        public Result<String> add(@RequestBody CmsNode cmsNode) {

                cmsNodeService.save(cmsNode);
            return Result.ok("添加成功！");
        }

        @Operation(summary="CMS-节点 - 编辑")
        @PostMapping(value = "/edit")
        public Result<String> edit(@RequestBody CmsNode cmsNode) {
                cmsNodeService.updateById(cmsNode);
            return Result.ok("编辑成功!");
        }


        @Operation(summary="CMS-节点 - 批量删除")
        @PostMapping(value = "/delete")
        public Result<String> deleteBatch(@RequestBody Collection<String> list) {
            this.cmsNodeService.removeByIds(list);
            return Result.ok("批量删除成功!");
        }

        @Operation(summary="CMS-节点 - 通过id查询")
        @GetMapping(value = "/queryById")
        public Result<CmsNode> queryById(@RequestParam(name="id") String id) {
            CmsNode cmsNode = cmsNodeService.getById(id);
            return Result.ok(cmsNode);
        }

}