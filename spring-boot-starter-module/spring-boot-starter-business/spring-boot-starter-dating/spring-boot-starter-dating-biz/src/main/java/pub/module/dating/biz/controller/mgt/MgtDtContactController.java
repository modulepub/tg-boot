package pub.module.dating.biz.controller.mgt;

import java.util.Collection;

import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;

import pub.module.dating.curd.entity.DtContact;
import pub.module.dating.curd.service.DtContactService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;



/**
 * 联系人
 *
 * @author tg
 *  2026-05-01 23:01:09
 */
@Tag(name="联系人")
@RestController
@RequestMapping("/mgt/dating/dtContact")
@Slf4j
public class MgtDtContactController{
        @Resource
        private DtContactService dtContactService;


        @Operation(summary="联系人分页列表查询")
        @GetMapping(value = "/list")
        public Result<IPage<DtContact>> queryPageList(DtContact dtContact,
                @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
            QueryWrapper<DtContact> queryWrapper = WebQueryUtil.buildQuery(dtContact);
            Page<DtContact> page = new Page<>(pageNo, pageSize);
            IPage<DtContact> pageList = dtContactService.page(page, queryWrapper);
            return Result.ok(pageList);
        }

        @Operation(summary="联系人添加")
        @PostMapping(value = "/add")
        public Result<String> add(@RequestBody DtContact dtContact) {

                dtContactService.save(dtContact);
            return Result.ok("添加成功！");
        }

        @Operation(summary="联系人编辑")
        @PostMapping(value = "/edit")
        public Result<String> edit(@RequestBody DtContact dtContact) {
                dtContactService.updateById(dtContact);
            return Result.ok("编辑成功!");
        }


        @Operation(summary="联系人批量删除")
        @PostMapping(value = "/delete")
        public Result<String> deleteBatch(@RequestBody Collection<String> list) {
            this.dtContactService.removeByIds(list);
            return Result.ok("批量删除成功!");
        }

        @Operation(summary="联系人通过id查询")
        @GetMapping(value = "/queryById")
        public Result<DtContact> queryById(@RequestParam(name="id") String id) {
            DtContact dtContact = dtContactService.getById(id);
            return Result.ok(dtContact);
        }

}