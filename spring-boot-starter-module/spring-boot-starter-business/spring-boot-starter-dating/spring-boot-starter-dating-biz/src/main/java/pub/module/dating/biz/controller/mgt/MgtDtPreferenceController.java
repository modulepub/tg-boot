package pub.module.dating.biz.controller.mgt;

import java.util.Collection;

import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;

import pub.module.dating.curd.entity.DtPreference;
import pub.module.dating.curd.service.DtPreferenceService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;



/**
 * 管理端-偏好
 *
 * @author tg
 *  2026-03-31 02:10:33
 */
@Tag(name="管理端-偏好")
@RestController
@RequestMapping("/mgt/dating/dtPreference")
@Slf4j
public class MgtDtPreferenceController{
        @Resource
        private DtPreferenceService dtPreferenceService;


        @Operation(summary="管理端-偏好分页列表查询")
        @GetMapping(value = "/list")
        public Result<IPage<DtPreference>> queryPageList(DtPreference dtPreference,
                @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
            QueryWrapper<DtPreference> queryWrapper = WebQueryUtil.buildQuery(dtPreference);
            Page<DtPreference> page = new Page<>(pageNo, pageSize);
            IPage<DtPreference> pageList = dtPreferenceService.page(page, queryWrapper);
            return Result.ok(pageList);
        }

        @Operation(summary="管理端-偏好添加")
        @PostMapping(value = "/add")
        public Result<String> add(@RequestBody DtPreference dtPreference) {

                dtPreferenceService.save(dtPreference);
            return Result.ok("添加成功！");
        }

        @Operation(summary="管理端-偏好编辑")
        @PostMapping(value = "/edit")
        public Result<String> edit(@RequestBody DtPreference dtPreference) {
                dtPreferenceService.updateById(dtPreference);
            return Result.ok("编辑成功!");
        }


        @Operation(summary="管理端-偏好批量删除")
        @PostMapping(value = "/delete")
        public Result<String> deleteBatch(@RequestBody Collection<String> list) {
            this.dtPreferenceService.removeByIds(list);
            return Result.ok("批量删除成功!");
        }

        @Operation(summary="管理端-偏好通过id查询")
        @GetMapping(value = "/queryById")
        public Result<DtPreference> queryById(@RequestParam(name="id") String id) {
            DtPreference dtPreference = dtPreferenceService.getById(id);
            return Result.ok(dtPreference);
        }

}