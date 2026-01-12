package pub.module.cms.biz.controller.pub;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.cms.curd.constants.CdPublishStatusCodeEnum;
import pub.module.cms.curd.entity.CmsDocument;
import pub.module.cms.curd.service.ICmsDocumentService;
import pub.module.web.util.WebQueryUtil;
import pub.module.web.vo.Result;

import jakarta.annotation.Resource;

/**
 * CMS文档
 * @author tg
 * @since 2025-09-29
 * @version V1.0
 */

@Tag(name="CMS门户端接口")
@RestController
@RequestMapping("/pub/cms/cmsDocument")
@Slf4j
public class PubCmsDocumentController {
	@Resource
	private ICmsDocumentService cmsDocumentService;
	
	@Operation(summary="CMS文档-移动端-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<CmsDocument>> queryPageList(CmsDocument cmsDocument,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		QueryWrapper<CmsDocument> queryWrapper = WebQueryUtil.buildQuery(cmsDocument);
        queryWrapper.lambda().eq(CmsDocument::getCdPublishStatusCode, CdPublishStatusCodeEnum.PUBLISHED.getCode());
		Page<CmsDocument> page = new Page<>(pageNo, pageSize);
		IPage<CmsDocument> pageList = cmsDocumentService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	


}
