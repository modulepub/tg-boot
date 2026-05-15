package pub.module.dating.biz.controller.cus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.customer.api.service.ApiCustomerService;
import pub.module.customer.api.service.dto.CustomerDTO;
import pub.module.dating.api.service.ApiDtIntentionService;
import pub.module.dating.api.service.dto.DtIntentionDTO;
import pub.module.dating.curd.entity.DtRecommended;
import pub.module.dating.curd.service.DtRecommendedService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;
import pub.module.common.util.WebQueryUtil;
import pub.module.common.model.vo.Result;


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
        private ApiCustomerService apiCustomerService;
        @Resource
        private ApiDtIntentionService apiDtIntentionService;


        @Operation(summary="用户端-对象推荐-分页列表查询")
        @GetMapping(value = "/list")
        public Result<IPage<DtRecommended>> queryPageList(DtRecommended dtRecommended,
                @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
            UserDTO userDTO = UserUtil.getCurrentSysUser();
            CustomerDTO customerDTO = apiCustomerService.getCusByUserCode(userDTO.getUserCode());
            QueryWrapper<DtRecommended> queryWrapper = WebQueryUtil.buildQuery(dtRecommended);
            queryWrapper.lambda().eq(DtRecommended::getUserCode, userDTO.getUserCode());
            String sql = "select preference_target_cus_code from dt_preference";
            queryWrapper.lambda().notInSql(DtRecommended::getCusCode,sql);
            queryWrapper.lambda().ne(DtRecommended::getCusCode,customerDTO.getCusCode());
            Page<DtRecommended> page = new Page<>(pageNo, pageSize);
            IPage<DtRecommended> pageList = dtRecommendedService.page(page, queryWrapper);
            if(pageList.getTotal()==0){
                DtIntentionDTO dtIntentionDTO = apiDtIntentionService.getDtIntention(userDTO.getUserCode());
                queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(DtRecommended::getCusSexCode,dtIntentionDTO.getIntentionSexCode());
                queryWrapper.lambda().ne(DtRecommended::getCusCode,customerDTO.getCusCode());
                pageList = dtRecommendedService.page(page, queryWrapper);
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
        Page<DtRecommended> page = new Page<>(pageNo, pageSize);
        IPage<DtRecommended> pageList = dtRecommendedService.page(page, queryWrapper);
        return Result.ok(pageList);
    }
}