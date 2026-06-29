package pub.module.dating.biz.controller.cus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.dating.api.service.ApiDtCustomerService;
import pub.module.dating.api.service.dto.DtCustomerDTO;
import pub.module.dating.biz.service.impl.ApiDtRecommendedServiceImpl;
import pub.module.dating.crud.entity.DtRecommended;
import pub.module.dating.crud.service.DtRecommendedService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;
import pub.module.common.util.WebQueryUtil;
import pub.module.common.model.vo.Result;
import cn.hutool.core.util.StrUtil;


/**
 * 用户端-对象推荐
 *
 * @author tg
 *  2026-03-30 00:52:26
 */
@Tag(name="用户端-对象推荐")
@RestController
@RequestMapping("/cus/dating/dtRecommended")
@Slf4j
public class CusDtRecommendedController {
        @Resource
        private DtRecommendedService dtRecommendedService;
        @Resource
        private ApiDtCustomerService apiDtCustomerService;
        @Resource
        private ApiDtRecommendedServiceImpl apiDtRecommendedService;


        @Operation(summary="用户端-对象推荐-分页列表查询")
        @GetMapping(value = "/list")
        public Result<IPage<DtRecommended>> queryPageList(DtRecommended dtRecommended,
                @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                @RequestParam(name = "leadCusCode", required = false) String leadCusCode) {
            UserDTO userDTO = UserUtil.getCurrentSysUser();
            DtCustomerDTO customerDto = apiDtCustomerService.getCusByUserCode(userDTO.getUserCode());
            QueryWrapper<DtRecommended> queryWrapper = WebQueryUtil.buildQuery(dtRecommended);
            queryWrapper.lambda().eq(DtRecommended::getUserCode, userDTO.getUserCode());
            String sql = "select preference_target_cus_code from dt_preference where preference_cus_code = '${cusCode}'";
            sql = sql.replace("${cusCode}",customerDto.getCusCode());
            queryWrapper.lambda().notInSql(DtRecommended::getCusCode,sql);
            queryWrapper.lambda().ne(DtRecommended::getCusCode,customerDto.getCusCode());
            ApiDtRecommendedServiceImpl.excludeHiddenRecommended(queryWrapper);
            queryWrapper.apply(
                    "exists (select 1 from dt_customer c where c.cus_code = dt_recommended.cus_code"
                            + " and c.cus_comlete_profile_status_code = {0} and c.deleted = {1})",
                    StatusCodeEnum.YES.getCode(), StatusCodeEnum.NO.getCode());
            Page<DtRecommended> page = new Page<>(pageNo, pageSize);
            IPage<DtRecommended> pageList = dtRecommendedService.page(page, queryWrapper);
            apiDtRecommendedService.enrichRecommendPageFromCustomer(pageList);
            if (pageNo != null && pageNo == 1 && StrUtil.isNotBlank(leadCusCode)) {
                apiDtRecommendedService.promoteLeadCusOnFirstPage(
                        pageList, leadCusCode.trim(), userDTO.getUserCode());
            }
            return Result.ok(pageList);
        }

    @Operation(summary="用户端-对象推荐历史-分页列表查询")
    @GetMapping(value = "/historyList")
    public Result<IPage<DtRecommended>> historyList(DtRecommended dtRecommended,
                                                      @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                      @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        QueryWrapper<DtRecommended> queryWrapper = WebQueryUtil.buildQuery(dtRecommended);
        queryWrapper.lambda().eq(DtRecommended::getUserCode, userDTO.getUserCode());
        ApiDtRecommendedServiceImpl.excludeHiddenRecommended(queryWrapper);
        ApiDtRecommendedServiceImpl.excludeCustomerDeletedRecommended(queryWrapper);
        Page<DtRecommended> page = new Page<>(pageNo, pageSize);
        IPage<DtRecommended> pageList = dtRecommendedService.page(page, queryWrapper);
        apiDtRecommendedService.enrichRecommendPageFromCustomer(pageList, userDTO.getUserCode());
        return Result.ok(pageList);
    }

    @Operation(summary = "用户端-从历史推荐列表移除")
    @PostMapping(value = "/hideFromList")
    public Result<String> hideFromList(@RequestParam(name = "id") String id) {
        if (StrUtil.isBlank(id)) {
            return Result.error("id不能为空");
        }
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        DtRecommended row = dtRecommendedService.getById(id.trim());
        if (row == null || !userDTO.getUserCode().equals(row.getUserCode())) {
            return Result.error("记录不存在或无权操作");
        }
        dtRecommendedService.update(new LambdaUpdateWrapper<DtRecommended>()
                .eq(DtRecommended::getId, id.trim())
                .eq(DtRecommended::getUserCode, userDTO.getUserCode())
                .set(DtRecommended::getRecommendedCusDelStatusCode, StatusCodeEnum.YES));
        return Result.ok("已删除");
    }
}