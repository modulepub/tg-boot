package pub.module.dating.biz.controller.cus;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.dating.curd.entity.DtCusMatchmaker;
import pub.module.dating.curd.service.DtCusMatchmakerService;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.web.util.WebQueryUtil;
import pub.module.web.vo.Result;

import java.util.Collection;


/**
 * 我的红娘 Controller
 *
 * @author tg
 *  2026-01-07 23:30:24
 */
@Tag(name="我的红娘 CURD 处理器")
@RestController
@RequestMapping("/cus/dating/dtCusMatchmaker")
@Slf4j
public class CusDtCusMatchmakerController {
        @Resource
        private DtCusMatchmakerService dtCusMatchmakerService;
        @Resource
        private ApiSysUserService apiSysUserService;

        @EqualsAndHashCode(callSuper = true)
        @Data
        public static class DtCusMatchmakerRes extends DtCusMatchmaker {
            UserDTO cmMt;
        }

        @Operation(summary="我的红娘 - 分页列表查询")
        @GetMapping(value = "/list")
        public Result<IPage<DtCusMatchmakerRes>> queryPageList(DtCusMatchmaker dtCusMatchmaker,
                @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
            QueryWrapper<DtCusMatchmaker> queryWrapper = WebQueryUtil.buildQuery(dtCusMatchmaker);
            Page<DtCusMatchmaker> page = new Page<>(pageNo, pageSize);
            IPage<DtCusMatchmaker> pageList = dtCusMatchmakerService.page(page, queryWrapper);
            //TODO 实际业务场景客户端不会在循环中查询用户信息，因为会造成N+1问题，这里只是为了演示，所以这里直接将用户信息放到DtCusMatchmakerRes中
            IPage<DtCusMatchmakerRes> pageRes = pageList.convert(item -> {
                DtCusMatchmakerRes res = BeanUtil.copyProperties(item, DtCusMatchmakerRes.class);
                UserDTO cmMt = apiSysUserService.getUserByUserCode(item.getCmMtCode());
                res.setCmMt(cmMt);
                return res;
            });
            return Result.ok(pageRes);
        }

        @Operation(summary="我的红娘 - 添加")
        @PostMapping(value = "/add")
        public Result<String> add(@RequestBody DtCusMatchmaker dtCusMatchmaker) {

                dtCusMatchmakerService.save(dtCusMatchmaker);
            return Result.ok("添加成功！");
        }

        @Operation(summary="我的红娘 - 编辑")
        @PostMapping(value = "/edit")
        public Result<String> edit(@RequestBody DtCusMatchmaker dtCusMatchmaker) {
                dtCusMatchmakerService.updateById(dtCusMatchmaker);
            return Result.ok("编辑成功!");
        }


        @Operation(summary="我的红娘 - 批量删除")
        @PostMapping(value = "/delete")
        public Result<String> deleteBatch(@RequestBody Collection<String> list) {
            this.dtCusMatchmakerService.removeByIds(list);
            return Result.ok("批量删除成功!");
        }

        @Operation(summary="我的红娘 - 通过id查询")
        @GetMapping(value = "/queryById")
        public Result<DtCusMatchmaker> queryById(@RequestParam(name="id") String id) {
            DtCusMatchmaker dtCusMatchmaker = dtCusMatchmakerService.getById(id);
            return Result.ok(dtCusMatchmaker);
        }

}