package pub.module.dating.biz.controller.pub;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.dating.curd.entity.DtCusMatchmakerRel;
import pub.module.dating.curd.service.DtCusMatchmakerRelService;
import pub.module.common.util.WebQueryUtil;
import pub.module.common.model.vo.Result;

import java.util.Collection;


/**
 * 公开-客户红娘关系
 *
 * @author tg
 *  2026-03-25 00:36:20
 */
@Tag(name="公开-客户红娘关系")
@RestController
@RequestMapping("/pub/dating/dtCusMatchmakerRel")
@Slf4j
public class PubDtCusMatchmakerRelController {
        @Resource
        private DtCusMatchmakerRelService dtCusMatchmakerRelService;


        @Operation(summary="公开-红娘主页-ta推荐的男女嘉宾")
        @GetMapping(value = "/listByMkCode")
        public Result<IPage<DtCusMatchmakerRel>> listByMkCode(DtCusMatchmakerRel dtCusMatchmakerRel,
                @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
            QueryWrapper<DtCusMatchmakerRel> queryWrapper = WebQueryUtil.buildQuery(dtCusMatchmakerRel);
            Assert.notEmpty(dtCusMatchmakerRel.getMkCode(),"警报：mkCode is null");
            Page<DtCusMatchmakerRel> page = new Page<>(pageNo, pageSize);
            IPage<DtCusMatchmakerRel> pageList = dtCusMatchmakerRelService.page(page, queryWrapper);
            return Result.ok(pageList);
        }


    @Operation(summary="公开-嘉宾主页Ta的专属情感顾问")
    @GetMapping(value = "/listByCusCode")
    public Result<IPage<DtCusMatchmakerRel>> listByCusCode(DtCusMatchmakerRel dtCusMatchmakerRel,
                                                           @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                           @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
        QueryWrapper<DtCusMatchmakerRel> queryWrapper = WebQueryUtil.buildQuery(dtCusMatchmakerRel);
        Assert.notEmpty(dtCusMatchmakerRel.getCusCode(),"警报：cusCode is null");
        Page<DtCusMatchmakerRel> page = new Page<>(pageNo, pageSize);
        IPage<DtCusMatchmakerRel> pageList = dtCusMatchmakerRelService.page(page, queryWrapper);
        return Result.ok(pageList);
    }
}