package pub.module.system.biz.controller.mgt;

import pub.module.common.enums.StatusCodeEnum;
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
import pub.module.system.api.service.ApiSysUserOrganizationService;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.common.util.WebQueryUtil;
import pub.module.common.model.vo.Result;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.crud.entity.SysUser;
import pub.module.system.crud.entity.SysUserTag;
import pub.module.system.crud.service.SysUserService;
import pub.module.system.crud.service.SysUserTagService;

import jakarta.annotation.Resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
   @Resource
   private SysUserTagService sysUserTagService;


   @EqualsAndHashCode(callSuper = true)
   @Data
   public static class SysUserVO extends SysUser {
       @Schema(description = "管理端-用户所属机构名称")
       private String userOrgNames;
       @Schema(description = "管理端-用户标签名称列表")
       private List<String> userTagList;
   }
    @Operation(summary = "管理端-用户管理-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<SysUserVO>> queryPageList(SysUser sysUser,
                                                @RequestParam(name = "userTags", required = false) String userTags,
                                                @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
       String orgCode = sysUser.getOrgCode();
       sysUser.setOrgCode(null);
        QueryWrapper<SysUser> queryWrapper = WebQueryUtil.buildQuery(sysUser);
        if (StrUtil.isNotBlank(userTags)) {
            List<String> taggedUserCodes = sysUserTagService.lambdaQuery()
                    .like(SysUserTag::getTagName, userTags.trim())
                    .list()
                    .stream()
                    .map(SysUserTag::getUserCode)
                    .filter(StrUtil::isNotBlank)
                    .distinct()
                    .collect(Collectors.toList());
            taggedUserCodes.add("-");
            queryWrapper.lambda().in(SysUser::getUserCode, taggedUserCodes);
        }
        if(StrUtil.isNotBlank(orgCode)){
            List<String> userCodes = apiSysUserOrganizationService.getUserCodes(orgCode);
            userCodes.add("-");
            queryWrapper.lambda().in(SysUser::getUserCode,userCodes);
        }
        IPage<SysUser> page = new Page<>(pageNo, pageSize);
        IPage<SysUser> pageList = sysUserService.page(page, queryWrapper);
        List<String> pageUserCodes = pageList.getRecords().stream()
                .map(SysUser::getUserCode)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toList());
        Map<String, List<String>> tagNameMap = sysUserTagService.mapTagNamesByUserCodes(pageUserCodes);
        IPage<SysUserVO> resultPage = pageList.convert(sysUserItem -> {
            SysUserVO sysUserVO = new SysUserVO();
            BeanUtil.copyProperties(sysUserItem, sysUserVO);
              List<String> orgNames = apiSysUserOrganizationService.getSysOrganizationNameByUserCode(sysUserVO.getUserCode());
                if(!orgNames.isEmpty()){
                    sysUserVO.setUserOrgNames(StrUtil.join(",",orgNames));
                }
            sysUserVO.setUserTagList(tagNameMap.getOrDefault(sysUserVO.getUserCode(), new ArrayList<>()));
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
        queryWrapper.lambda().eq(SysUser::getUserOlineStatusCode, StatusCodeEnum.YES.getCode());
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
        return Result.ok("添加成功！");
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class SysUserSaveVO extends SysUser{
    }
    @Operation(summary="管理端-用户编辑")
    @PostMapping(value = "/edit")
    public Result<String> edit(@RequestBody SysUserSaveVO sysUserSaveVO) {
        sysUserSaveVO.setUserOrgCode(null);
        sysUserService.updateById(sysUserSaveVO);
        return Result.ok("编辑成功!");
    }

    @Operation(summary="管理端-用户批量删除")
    @PostMapping(value = "/delete")
    public Result<String> deleteBatch(@RequestBody Collection<String> list) {
        this.sysUserService.removeByIds(list);
        return Result.ok("批量删除成功!");
    }

    @Data
    public static class ResetPasswordVO {
        @Schema(description = "用户 id")
        private String id;
    }

    @Operation(summary = "管理端-重置用户密码")
    @PostMapping(value = "/resetPassword")
    public Result<String> resetPassword(@RequestBody ResetPasswordVO resetPasswordVO) {
        String newPassword = apiSysUserService.resetPasswordById(resetPasswordVO.getId());
        return Result.ok(newPassword);
    }

    @Operation(summary = "管理端-未同步 IM 的系统用户分页")
    @GetMapping(value = "/listImUnsynced")
    public Result<IPage<SysUser>> listImUnsynced(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        IPage<UserDTO> page = apiSysUserService.pageImUnsynced(keyword, pageNo, pageSize);
        IPage<SysUser> result = page.convert(dto -> BeanUtil.copyProperties(dto, SysUser.class));
        return Result.ok(result);
    }

    @Operation(summary="管理端-用户通过id查询")
    @GetMapping(value = "/queryById")
    public Result<SysUserVO> queryById(@RequestParam(name="id") String id) {
        SysUser sysUser = sysUserService.getById(id);
        SysUserVO result = BeanUtil.copyProperties(sysUser, SysUserVO.class);
        return Result.ok(result);


    }
}
