package pub.module.contract.biz.controller.cus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.contract.curd.entity.CtTemplate;
import pub.module.contract.curd.service.ICtTemplateService;
import pub.module.web.util.WebQueryUtil;
import pub.module.web.vo.Result;

import jakarta.annotation.Resource;

/**
 * 合同模板
 * @author tg
 * @since 2025-12-04
 * @version V1.0
 */

@Tag(name="合同模板")
@RestController
@RequestMapping("/cus/contract/ctTemplate")
@Slf4j
public class CusContractTemplateController {
	@Resource
	private ICtTemplateService contractTemplateService;

	@Operation(summary="合同模板-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<CtTemplate>> queryPageList(CtTemplate contractTemplate,
                                                   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		QueryWrapper<CtTemplate> queryWrapper = WebQueryUtil.buildQuery(contractTemplate);
		Page<CtTemplate> page = new Page<>(pageNo, pageSize);
		IPage<CtTemplate> pageList = contractTemplateService.page(page, queryWrapper);
		return Result.ok(pageList);
	}



}
