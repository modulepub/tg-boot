package pub.module.dating.biz.controller.pub;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.dating.api.service.dto.MatchmakingCompanyPublicDTO;
import pub.module.dating.crud.entity.DtMatchmakingCompany;
import pub.module.dating.crud.service.DtMatchmakingCompanyService;
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

        @Operation(summary = "公开-已认证企业分页列表（支持名称搜索）")
        @GetMapping(value = "/listCertified")
        public Result<IPage<MatchmakingCompanyPublicDTO>> listCertified(
                @RequestParam(value = "keyword", required = false) String keyword,
                @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                @RequestParam(name = "pageSize", defaultValue = "12") Integer pageSize) {
            QueryWrapper<DtMatchmakingCompany> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("mk_company_identity_status_code", StatusCodeEnum.YES.getCode());
            if (StrUtil.isNotBlank(keyword)) {
                queryWrapper.like("mk_company_name", keyword.trim());
            }
            queryWrapper.orderByAsc("mk_company_name");
            Page<DtMatchmakingCompany> page = new Page<>(pageNo, pageSize);
            IPage<DtMatchmakingCompany> pageList = dtMatchmakingCompanyService.page(page, queryWrapper);
            return Result.ok(pageList.convert(this::toPublicDto));
        }

        private MatchmakingCompanyPublicDTO toPublicDto(DtMatchmakingCompany company) {
            MatchmakingCompanyPublicDTO dto = new MatchmakingCompanyPublicDTO();
            dto.setMkCompanyCode(company.getMkCompanyCode());
            dto.setMkCompanyName(company.getMkCompanyName());
            dto.setMkCompanyTel(company.getMkCompanyTel());
            dto.setMkCompanyAddressDetail(company.getMkCompanyAddressDetail());
            dto.setMkCompanyPhotos(company.getMkCompanyPhotos());
            dto.setMkCompanyAuditAt(company.getMkCompanyAuditAt());
            return dto;
        }

}