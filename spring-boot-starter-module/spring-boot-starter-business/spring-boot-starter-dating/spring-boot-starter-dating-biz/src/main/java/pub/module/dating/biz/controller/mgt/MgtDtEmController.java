package pub.module.dating.biz.controller.mgt;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.common.model.vo.Result;


/**
 * 管理端-员工
 *
 * @author tg
 * 2026-02-01 10:25:44
 */
@Tag(name = "管理端-员工")
@RestController
@RequestMapping("/mgt/dating/staff")
@Slf4j
public class MgtDtEmController {

    @Resource
    ApiSysUserService sysUserService;

    @Operation(summary = "管理端-员工分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<UserDTO>> queryPageList(UserDTO userDTO, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        IPage<UserDTO> pageList = sysUserService.page(userDTO, pageNo, pageSize);
        return Result.ok(pageList);
    }
}
