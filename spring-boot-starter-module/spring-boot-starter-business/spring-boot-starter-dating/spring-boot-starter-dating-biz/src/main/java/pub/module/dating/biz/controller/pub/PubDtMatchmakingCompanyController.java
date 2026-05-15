package pub.module.dating.biz.controller.pub;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.dating.curd.entity.DtMatchmakingCompany;
import pub.module.dating.curd.service.DtMatchmakingCompanyService;
import pub.module.common.util.WebQueryUtil;
import pub.module.common.model.vo.Result;


/**
 * 公开-公司信息
 *
 * @author tg
 *  2026-03-22 13:32:45
 */
@Tag(name="公开-公司信息")
@RestController
@RequestMapping("/pub/dating/dtMatchmakingCompany")
@Slf4j
public class PubDtMatchmakingCompanyController {
        @Resource
        private DtMatchmakingCompanyService dtMatchmakingCompanyService;


        @Operation(summary="公开-公司信息分页列表查询")
        @GetMapping(value = "/list")
        public Result<IPage<DtMatchmakingCompany>> queryPageList(DtMatchmakingCompany dtMatchmakingCompany,
                @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
            QueryWrapper<DtMatchmakingCompany> queryWrapper = WebQueryUtil.buildQuery(dtMatchmakingCompany);
            Page<DtMatchmakingCompany> page = new Page<>(pageNo, pageSize);
            IPage<DtMatchmakingCompany> pageList = dtMatchmakingCompanyService.page(page, queryWrapper);
            return Result.ok(pageList);
        }
        
        @Operation(summary="公开-公司信息通过Code查询")
        @GetMapping(value = "/queryByCode")
        public Result<DtMatchmakingCompany> queryById(@RequestParam(name="mkCompanyCode") String mkCompanyCode) {
            DtMatchmakingCompany dtMatchmakingCompany = dtMatchmakingCompanyService.getByCode(mkCompanyCode);
            return Result.ok(dtMatchmakingCompany);
        }

}