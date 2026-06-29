package pub.module.system.biz.controller.mgt;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;
import pub.module.system.crud.entity.SysUser;
import pub.module.system.crud.entity.SysUserBadge;
import pub.module.system.crud.service.SysUserBadgeService;
import pub.module.system.crud.service.SysUserService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理端-用户角标
 */
@Tag(name = "管理端-用户角标")
@RestController
@RequestMapping("/mgt/system/sysUserBadge")
@Slf4j
public class MgtSysUserBadgeController {

    @Resource
    private SysUserBadgeService sysUserBadgeService;
    @Resource
    private SysUserService sysUserService;

    @Operation(summary = "管理端-用户角标分页列表")
    @GetMapping("/list")
    public Result<IPage<MgtSysUserBadgeVO>> queryPageList(
            SysUserBadge sysUserBadge,
            @RequestParam(name = "userPhone", required = false) String userPhone,
            @RequestParam(name = "userNickName", required = false) String userNickName,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<SysUserBadge> queryWrapper = WebQueryUtil.buildQuery(sysUserBadge);
        if (StrUtil.isNotBlank(userPhone) || StrUtil.isNotBlank(userNickName)) {
            QueryWrapper<SysUser> userQuery = new QueryWrapper<>();
            if (StrUtil.isNotBlank(userPhone)) {
                userQuery.like("user_phone", userPhone.trim());
            }
            if (StrUtil.isNotBlank(userNickName)) {
                userQuery.like("user_nick_name", userNickName.trim());
            }
            List<String> userCodes = sysUserService.list(userQuery).stream()
                    .map(SysUser::getUserCode)
                    .filter(StrUtil::isNotBlank)
                    .distinct()
                    .toList();
            if (userCodes.isEmpty()) {
                return Result.ok(new Page<>(pageNo, pageSize));
            }
            queryWrapper.in("user_code", userCodes);
        }
        queryWrapper.orderByDesc("update_time");
        Page<SysUserBadge> page = new Page<>(pageNo, pageSize);
        IPage<SysUserBadge> pageList = sysUserBadgeService.page(page, queryWrapper);
        return Result.ok(enrichPage(pageList));
    }

    private IPage<MgtSysUserBadgeVO> enrichPage(IPage<SysUserBadge> pageList) {
        List<String> userCodes = pageList.getRecords().stream()
                .map(SysUserBadge::getUserCode)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .toList();
        Map<String, SysUser> userMap = userCodes.isEmpty()
                ? Collections.emptyMap()
                : sysUserService.list(new QueryWrapper<SysUser>().lambda().in(SysUser::getUserCode, userCodes))
                .stream()
                .collect(Collectors.toMap(SysUser::getUserCode, user -> user, (left, right) -> left));

        Page<MgtSysUserBadgeVO> voPage = new Page<>(pageList.getCurrent(), pageList.getSize(), pageList.getTotal());
        voPage.setRecords(pageList.getRecords().stream().map(badge -> toVo(badge, userMap)).toList());
        return voPage;
    }

    private static MgtSysUserBadgeVO toVo(SysUserBadge badge, Map<String, SysUser> userMap) {
        MgtSysUserBadgeVO vo = BeanUtil.copyProperties(badge, MgtSysUserBadgeVO.class);
        SysUser user = userMap.get(badge.getUserCode());
        if (user != null) {
            vo.setUserPhone(user.getUserPhone());
            vo.setUserNickName(user.getUserNickName());
        }
        return vo;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @Schema(description = "管理端-用户角标")
    public static class MgtSysUserBadgeVO extends SysUserBadge {

        @Schema(description = "用户手机号")
        private String userPhone;

        @Schema(description = "用户昵称")
        private String userNickName;
    }
}
