package pub.module.cms.biz.controller.mgt;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.cms.api.dto.CmsShortUrlDTO;
import pub.module.cms.crud.entity.CmsShortUrl;
import pub.module.cms.crud.service.CmsShortUrlService;
import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;

@Tag(name = "管理-CMS短链")
@RestController
@RequestMapping("/mgt/cms/cmsShortUrl")
@Slf4j
public class MgtCmsShortUrlController {

    @Resource
    private CmsShortUrlService cmsShortUrlService;

    @Operation(summary = "管理-CMS短链分页列表")
    @GetMapping("/list")
    public Result<IPage<CmsShortUrlDTO>> list(CmsShortUrl query,
                                              @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<CmsShortUrl> page = new Page<>(pageNo, pageSize);
        QueryWrapper<CmsShortUrl> wrapper = WebQueryUtil.buildQuery(query);
        WebQueryUtil.setSelect(wrapper, CmsShortUrlDTO.class);
        wrapper.lambda().eq(CmsShortUrl::getDeleted, "0");
        IPage<CmsShortUrl> pageList = cmsShortUrlService.page(page, wrapper);
        return Result.ok(pageList.convert(r -> BeanUtil.copyProperties(r, CmsShortUrlDTO.class)));
    }

    @Operation(summary = "管理-CMS短链详情")
    @GetMapping("/queryById")
    public Result<CmsShortUrl> queryById(@RequestParam String id) {
        return Result.ok(cmsShortUrlService.getById(id));
    }

    @Operation(summary = "管理-CMS短链删除")
    @PostMapping("/delete")
    public Result<Boolean> delete(@RequestParam String id) {
        return Result.ok(cmsShortUrlService.removeById(id));
    }
}
