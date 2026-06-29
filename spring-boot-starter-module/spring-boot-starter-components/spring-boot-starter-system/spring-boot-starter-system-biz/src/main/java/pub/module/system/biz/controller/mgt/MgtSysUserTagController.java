package pub.module.system.biz.controller.mgt;

import cn.hutool.core.lang.Assert;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.common.model.vo.Result;
import pub.module.system.crud.entity.SysUserTag;
import pub.module.system.crud.service.SysUserTagService;

import java.util.Collection;
import java.util.List;

/**
 * 管理端-用户标签
 *
 * @author tg
 */
@Tag(name = "管理端-用户标签")
@RestController
@RequestMapping("/mgt/sysUserTag")
@Slf4j
public class MgtSysUserTagController {

    @Resource
    private SysUserTagService sysUserTagService;

    @Operation(summary = "管理端-用户标签列表")
    @GetMapping(value = "/listByUserCode")
    public Result<List<SysUserTag>> listByUserCode(@RequestParam(name = "userCode") String userCode) {
        Assert.notBlank(userCode, "用户编码不能为空");
        return Result.ok(sysUserTagService.listByUserCode(userCode));
    }

    @Data
    public static class AddTagVO {
        @Schema(description = "用户编码")
        private String userCode;
        @Schema(description = "标签编码（不传则使用标签名称作为编码）")
        private String tagCode;
        @Schema(description = "标签名称")
        private String tagName;
    }

    @Operation(summary = "管理端-用户标签新增")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody AddTagVO addTagVO) {
        Assert.notBlank(addTagVO.getUserCode(), "用户编码不能为空");
        Assert.notBlank(addTagVO.getTagName(), "标签名称不能为空");
        String tagCode = cn.hutool.core.util.StrUtil.blankToDefault(addTagVO.getTagCode(), addTagVO.getTagName());
        sysUserTagService.addTag(addTagVO.getUserCode(), tagCode, addTagVO.getTagName());
        return Result.ok("添加成功！");
    }

    @Operation(summary = "管理端-用户标签批量删除")
    @PostMapping(value = "/delete")
    public Result<String> deleteBatch(@RequestBody Collection<String> list) {
        Assert.notEmpty(list, "主键集合不能为空");
        sysUserTagService.removeByIds(list);
        return Result.ok("删除成功！");
    }
}
