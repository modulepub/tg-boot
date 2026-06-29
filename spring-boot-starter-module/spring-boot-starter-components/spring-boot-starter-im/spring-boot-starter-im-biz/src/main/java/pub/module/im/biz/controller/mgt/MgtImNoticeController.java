package pub.module.im.biz.controller.mgt;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;
import pub.module.im.api.service.ApiImNoticeService;
import pub.module.im.crud.entity.ImNotice;
import pub.module.im.crud.service.ImNoticeService;

import java.util.Collection;

@Tag(name = "管理端-IM全员通知")
@RestController
@RequestMapping("/mgt/im/imNotice")
@Slf4j
public class MgtImNoticeController {

    @Resource
    private ImNoticeService imNoticeService;
    @Resource
    private ApiImNoticeService apiImNoticeService;

    @Operation(summary = "管理端-IM通知分页列表")
    @GetMapping("/list")
    public Result<IPage<ImNotice>> list(ImNotice query,
                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<ImNotice> wrapper = WebQueryUtil.buildQuery(query);
        wrapper.eq("deleted", 0).orderByDesc("create_time");
        Page<ImNotice> page = new Page<>(pageNo, pageSize);
        return Result.ok(imNoticeService.page(page, wrapper));
    }

    @Operation(summary = "管理端-IM通知详情")
    @GetMapping("/queryById")
    public Result<ImNotice> queryById(@RequestParam(name = "id") String id) {
        return Result.ok(imNoticeService.getById(id));
    }

    @Operation(summary = "管理端-IM通知新增")
    @PostMapping("/add")
    public Result<String> add(@RequestBody ImNotice imNotice) {
        imNoticeService.save(imNotice);
        return Result.ok("添加成功！");
    }

    @Operation(summary = "管理端-IM通知编辑")
    @PostMapping("/edit")
    public Result<String> edit(@RequestBody ImNotice imNotice) {
        imNoticeService.updateById(imNotice);
        return Result.ok("编辑成功!");
    }

    @Operation(summary = "管理端-IM通知删除")
    @PostMapping("/delete")
    public Result<String> deleteBatch(@RequestBody Collection<String> ids) {
        imNoticeService.removeByIds(ids);
        return Result.ok("删除成功!");
    }

    @Operation(summary = "管理端-IM通知全员发送")
    @PostMapping("/publish")
    public Result<Integer> publish(@RequestParam(name = "id") String id) {
        int count = apiImNoticeService.publishAndBroadcast(id);
        return Result.ok("发送完成，成功 " + count + " 人", count);
    }
}
