package pub.module.system.biz.controller.cus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pub.module.system.api.constants.PerTypeCodeEnum;
import pub.module.system.api.service.ApiSysPermissionService;
import pub.module.system.api.service.dto.PermissionDTO;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;
import pub.module.system.curd.entity.SysPermission;
import pub.module.system.curd.service.SysPermissionService;
import pub.module.common.util.WebQueryUtil;
import pub.module.common.model.vo.Result;

import java.util.*;


/**
 * 用户菜单管理
 *
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@RestController
@RequestMapping("/cus/sysPermission")
@Tag(name = "用户端-菜单管理")
@AllArgsConstructor
public class CusPermissionController {
    @Resource
    ApiSysPermissionService apiSysPermissionService;
    @Resource
    SysPermissionService sysPermissionService;

    @GetMapping("/getByCode")
    @Operation(summary = "用户端-菜单导航")
    public Result<PermissionDTO> listAll(@RequestParam(name="code") String perCode) {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        List<PermissionDTO> permissionDTOList = apiSysPermissionService.getPermissionsByUserCode(userDTO.getUserCode()).stream().filter(permissionDTO -> PerTypeCodeEnum.MENU.getCode().equals(permissionDTO.getPerTypeCode())).toList();
        return Result.ok(apiSysPermissionService.buildTree(perCode,permissionDTOList));
    }

    @Operation(summary="用户端-菜单分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<SysPermission>> queryPageList(SysPermission permission,
                                                        @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                        @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
        QueryWrapper<SysPermission> queryWrapper = WebQueryUtil.buildQuery(permission);
        queryWrapper.lambda().orderByAsc(SysPermission::getSeqNo);
        Page<SysPermission> page = new Page<>(pageNo, pageSize);
        IPage<SysPermission> pageList = sysPermissionService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @GetMapping("authority")
    @Operation(summary = "用户端-权限标识")
    public Result<List<String>> authority() {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        List<PermissionDTO> permissionDTOList = apiSysPermissionService.getPermissionsByUserCode(userDTO.getUserCode());
        return Result.ok(permissionDTOList.stream().map(PermissionDTO::getPerCode).toList());
    }



}