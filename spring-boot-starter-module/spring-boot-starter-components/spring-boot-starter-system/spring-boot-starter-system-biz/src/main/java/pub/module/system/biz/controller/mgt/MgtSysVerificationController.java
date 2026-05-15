package pub.module.system.biz.controller.mgt;

import java.util.Collection;

import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;

import pub.module.system.curd.entity.SysVerification;
import pub.module.system.curd.service.SysVerificationService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;



/**
 * 管理端-验证码
 *
 * @author tg
 *  2026-04-20 14:14:27
 */
@Tag(name="管理端-验证码")
@RestController
@RequestMapping("/mgt/system/sysVerification")
@Slf4j
public class MgtSysVerificationController{
        @Resource
        private SysVerificationService sysVerificationService;


        @Operation(summary="管理端-验证码分页列表查询")
        @GetMapping(value = "/list")
        public Result<IPage<SysVerification>> queryPageList(SysVerification sysVerification,
                @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
            QueryWrapper<SysVerification> queryWrapper = WebQueryUtil.buildQuery(sysVerification);
            Page<SysVerification> page = new Page<>(pageNo, pageSize);
            IPage<SysVerification> pageList = sysVerificationService.page(page, queryWrapper);
            return Result.ok(pageList);
        }

        @Operation(summary="管理端-验证码添加")
        @PostMapping(value = "/add")
        public Result<String> add(@RequestBody SysVerification sysVerification) {

                sysVerificationService.save(sysVerification);
            return Result.ok("添加成功！");
        }

        @Operation(summary="管理端-验证码编辑")
        @PostMapping(value = "/edit")
        public Result<String> edit(@RequestBody SysVerification sysVerification) {
                sysVerificationService.updateById(sysVerification);
            return Result.ok("编辑成功!");
        }


        @Operation(summary="管理端-验证码批量删除")
        @PostMapping(value = "/delete")
        public Result<String> deleteBatch(@RequestBody Collection<String> list) {
            this.sysVerificationService.removeByIds(list);
            return Result.ok("批量删除成功!");
        }

        @Operation(summary="管理端-验证码通过id查询")
        @GetMapping(value = "/queryById")
        public Result<SysVerification> queryById(@RequestParam(name="id") String id) {
            SysVerification sysVerification = sysVerificationService.getById(id);
            return Result.ok(sysVerification);
        }

}