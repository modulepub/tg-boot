package pub.module.im.biz.controller.mgt;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.metadata.IPage;
import pub.module.common.enums.StatusCodeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.common.model.vo.Result;
import pub.module.dating.api.constants.ContactSourceCodeEnum;
import pub.module.dating.api.service.ApiDtContactApplyService;
import pub.module.dating.api.service.ApiDtContactService;
import pub.module.im.api.constants.ImSpecialUserConstants;
import pub.module.im.api.service.ApiImUserService;
import pub.module.im.crud.entity.ImUser;
import pub.module.im.crud.service.ImUserService;
import pub.module.system.api.service.ApiSysUserService;

import java.util.Collection;
import java.util.List;

@Tag(name = "管理端-IM用户")
@RestController
@RequestMapping("/mgt/im/imUser")
@Slf4j
public class MgtImUserController {

    @Resource
    private ImUserService imUserService;
    @Resource
    private ApiImUserService apiImUserService;
    @Resource
    private ApiSysUserService apiSysUserService;
    @Resource
    private ApiDtContactApplyService apiDtContactApplyService;
    @Resource
    private ApiDtContactService apiDtContactService;

    @Operation(summary = "管理端-IM用户分页（keyword 搜昵称/姓名/编码）")
    @GetMapping("/list")
    public Result<IPage<ImUser>> list(ImUser query,
                                      @RequestParam(name = "keyword", required = false) String keyword,
                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        IPage<ImUser> page = imUserService.pageForMgt(keyword, query, pageNo, pageSize);
        String system = ImSpecialUserConstants.MGT_SYSTEM_USER_CODE;
        for (ImUser user : page.getRecords()) {
            user.setImUserSystemFriend(apiDtContactService.isMutualContact(system, user.getImUserUserCode()));
        }
        return Result.ok(page);
    }

    @Operation(summary = "管理端-IM用户详情")
    @GetMapping("/queryById")
    public Result<ImUser> queryById(@RequestParam(name = "id") String id) {
        return Result.ok(imUserService.getById(id));
    }

    @Operation(summary = "管理端-IM用户删除")
    @PostMapping("/delete")
    public Result<String> deleteBatch(@RequestBody Collection<String> ids) {
        for (String id : ids) {
            ImUser imUser = imUserService.getById(id);
            if (imUser != null && imUser.getImUserUserCode() != null) {
                apiSysUserService.updateUserImSynStatusByUserCode(
                        imUser.getImUserUserCode(), StatusCodeEnum.NO);
            }
        }
        imUserService.removeByIds(ids);
        return Result.ok("删除成功!");
    }

    @Operation(summary = "管理端-从系统用户拉取资料并更新 IM 用户")
    @PostMapping("/refresh")
    public Result<String> refresh(@RequestParam(name = "userCode") String userCode) {
        apiImUserService.refreshFromSysUser(userCode);
        return Result.ok("更新成功");
    }

    @Operation(summary = "管理端-刷新全部已存在的 IM 用户资料")
    @PostMapping("/refreshAll")
    public Result<Integer> refreshAll() {
        int count = apiImUserService.refreshAllFromSysUsers();
        if (count == 0) {
            return Result.ok("暂无 IM 用户", 0);
        }
        return Result.ok("刷新成功 " + count + " 人", count);
    }

    @Operation(summary = "管理端-批量同步系统用户为 IM 用户")
    @PostMapping("/sync")
    public Result<Integer> sync(@RequestBody List<String> userCodes) {
        int count = apiImUserService.batchSyncFromSysUsers(userCodes);
        return Result.ok("同步成功 " + count + " 人", count);
    }

    @Operation(summary = "管理端-设置用户标签")
    @PostMapping("/tag")
    public Result<String> setTag(@RequestParam(name = "userCode") String userCode,
                                 @RequestParam(name = "tag") String tag) {
        ImUser user = imUserService.getByUserCode(userCode);
        Assert.notNull(user, "用户不存在");
        user.setImUserTag(tag);
        imUserService.updateById(user);
        return Result.ok("设置成功");
    }

    @Operation(summary = "管理端-添加系统账号好友")
    @PostMapping("/addFriend")
    public Result<String> addFriend(@RequestParam(name = "userCode") String userCode) {
        ImUser user = imUserService.getByUserCode(userCode);
        Assert.notNull(user, "用户不存在");
        apiDtContactApplyService.ensureMutualContactsByUserCode(
                ImSpecialUserConstants.MGT_SYSTEM_USER_CODE,
                user.getImUserUserCode(),
                ContactSourceCodeEnum.MATCHMAKER_MATCHING,
                "管理员添加好友");
        return Result.ok("添加好友成功");
    }
}
