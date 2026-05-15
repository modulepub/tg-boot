package pub.module.dating.biz.controller.cus;

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
import pub.module.customer.api.service.ApiCustomerService;
import pub.module.customer.api.service.dto.CustomerDTO;
import pub.module.dating.api.service.ApiDtCusMatchmakerRelService;
import pub.module.dating.api.service.dto.CusMkRelShowStatusUpdateDTO;
import pub.module.dating.curd.entity.DtCusMatchmakerRel;
import pub.module.dating.curd.service.DtCusMatchmakerRelService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;

import java.util.Collection;


/**
 * 用户端-客户红娘关系
 *
 * @author tg
 *  2026-03-25 00:36:20
 */
@Tag(name="用户端-客户红娘关系")
@RestController
@RequestMapping("/cus/dating/dtCusMatchmakerRel")
@Slf4j
public class CusDtCusMatchmakerRelController {
        @Resource
        private DtCusMatchmakerRelService dtCusMatchmakerRelService;
        @Resource
        private ApiCustomerService apiCustomerService;
        @Resource
        private ApiDtCusMatchmakerRelService apiDtCusMatchmakerRelService;


        @Operation(summary="用户端-我的顾问列表接口")
        @GetMapping(value = "/myMatchmakerList")
        public Result<IPage<DtCusMatchmakerRel>> queryPageList(DtCusMatchmakerRel dtCusMatchmakerRel,
                @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
            UserDTO userDTO = UserUtil.getCurrentSysUser();
            CustomerDTO customerDTO = apiCustomerService.getCusByUserCode(userDTO.getUserCode());
            QueryWrapper<DtCusMatchmakerRel> queryWrapper = WebQueryUtil.buildQuery(dtCusMatchmakerRel);
            queryWrapper.lambda().eq(DtCusMatchmakerRel::getCusCode,customerDTO.getCusCode());
            Page<DtCusMatchmakerRel> page = new Page<>(pageNo, pageSize);
            IPage<DtCusMatchmakerRel> pageList = dtCusMatchmakerRelService.page(page, queryWrapper);
            return Result.ok(pageList);
        }

        @Operation(summary="用户端-更新客户红娘关系主页展示开关")
        @PostMapping(value = "/updateCusMkRelShowStatus")
        public Result<String> updateCusMkRelShowStatus(@RequestBody CusMkRelShowStatusUpdateDTO dto) {
            apiDtCusMatchmakerRelService.updateCusMkRelShowStatus(dto);
            return Result.ok("已更新");
        }


}