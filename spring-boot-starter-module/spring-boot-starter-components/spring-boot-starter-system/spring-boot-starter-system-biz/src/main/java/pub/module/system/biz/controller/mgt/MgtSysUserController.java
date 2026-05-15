package pub.module.system.biz.controller.mgt;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.*;
import pub.module.system.api.constants.UserOlineStatusCodeEnum;
import pub.module.system.api.service.ApiSysUserOrganizationService;
import pub.module.common.util.WebQueryUtil;
import pub.module.common.model.vo.Result;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.curd.entity.SysUser;
import pub.module.system.curd.service.SysUserService;

import jakarta.annotation.Resource;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 管理端-用户管理
 *
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Tag(name = "管理端-用户管理")
@RestController
@RequestMapping("/mgt/sysUser")
@Slf4j
public class MgtSysUserController {
   @Resource
   private SysUserService sysUserService;
   @Resource
   private ApiSysUserOrganizationService apiSysUserOrganizationService;
   @Resource
   private ApiSysUserService apiSysUserService;


   @EqualsAndHashCode(callSuper = true)
   @Data
   public static class SysUserVO extends SysUser {
       @Schema(description = "管理端-用户所属机构名称")
       private String userOrgNames;
       private List<String> orgCodeList;
   }
    @Operation(summary = "管理端-用户管理-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<SysUserVO>> queryPageList(SysUser sysUser,
                                                @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
       String orgCode = sysUser.getOrgCode();
       sysUser.setOrgCode(null);
        QueryWrapper<SysUser> queryWrapper = WebQueryUtil.buildQuery(sysUser);
        if(StrUtil.isNotBlank(orgCode)){
            List<String> userCodes = apiSysUserOrganizationService.getUserCodes(orgCode);
            userCodes.add("-");
            queryWrapper.lambda().in(SysUser::getUserCode,userCodes);
        }
        IPage<SysUser> page = new Page<>(pageNo, pageSize);
        IPage<SysUser> pageList = sysUserService.page(page, queryWrapper);
        IPage<SysUserVO> resultPage = pageList.convert(sysUserItem -> {
            SysUserVO sysUserVO = new SysUserVO();
            BeanUtil.copyProperties(sysUserItem, sysUserVO);
              List<String> orgNames = apiSysUserOrganizationService.getSysOrganizationNameByUserCode(sysUserVO.getUserCode());
                if(!orgNames.isEmpty()){
                    sysUserVO.setUserOrgNames(StrUtil.join(",",orgNames));
                }
            return sysUserVO;
        });
        return Result.ok(resultPage);
    }

    @Operation(summary = "管理端-用户管理-在线用户列表查询")
    @GetMapping(value = "/listOnline")
    public Result<IPage<SysUser>> listOnline(SysUser sysUser,
                                                  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {

        QueryWrapper<SysUser> queryWrapper = WebQueryUtil.buildQuery(sysUser);
        queryWrapper.lambda().in(SysUser::getUserOlineStatusCode, UserOlineStatusCodeEnum.YES.getCode());
        IPage<SysUser> page = new Page<>(pageNo, pageSize);
        IPage<SysUser> pageList = sysUserService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @Data
    public static class TakeOffVO {
        private String userName;
    }

    @Operation(summary="管理端-用户下线")
    @PostMapping(value = "/takeOff")
    public Result<String> takeOff(@RequestBody TakeOffVO takeOffVO) {
        apiSysUserService.logoutByUserName(takeOffVO.getUserName());
        return Result.ok("下线成功！");
    }

    @Operation(summary="管理端-用户添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody SysUserSaveVO sysUserSaveVO) {
        sysUserService.save(sysUserSaveVO);
        apiSysUserOrganizationService.saveOrgCodes(sysUserSaveVO.getOrgCodeList(),sysUserSaveVO.getUserCode());
        return Result.ok("添加成功！");
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class SysUserSaveVO extends SysUser{
       List<String> orgCodeList;
    }
    @Operation(summary="管理端-用户编辑")
    @PostMapping(value = "/edit")
    public Result<String> edit(@RequestBody SysUserSaveVO sysUserSaveVO) {
        sysUserService.updateById(sysUserSaveVO);
        apiSysUserOrganizationService.saveOrgCodes(sysUserSaveVO.getOrgCodeList(),sysUserSaveVO.getUserCode());
        return Result.ok("编辑成功!");
    }

    @Operation(summary="管理端-用户批量删除")
    @PostMapping(value = "/delete")
    public Result<String> deleteBatch(@RequestBody Collection<String> list) {
        this.sysUserService.removeByIds(list);
        return Result.ok("批量删除成功!");
    }

    @Operation(summary="管理端-用户通过id查询")
    @GetMapping(value = "/queryById")
    public Result<SysUserVO> queryById(@RequestParam(name="id") String id) {
        SysUser sysUser = sysUserService.getById(id);
        SysUserVO result = BeanUtil.copyProperties(sysUser, SysUserVO.class);
        List<String> orgCodes = apiSysUserOrganizationService.getOrgCodes(result.getUserCode());
        result.setOrgCodeList(orgCodes);
        return Result.ok(result);


    }
}
