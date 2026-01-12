package pub.module.dating.biz.controller.cus;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.dating.curd.constants.DtLikeDegreeCodeEnum;
import pub.module.system.api.service.BizSysUserService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.web.vo.Result;
import pub.module.dating.curd.entity.DtLike;
import pub.module.dating.curd.service.DtLikeService;
import pub.module.system.api.util.UserUtil;

import jakarta.annotation.Resource;
import java.util.List;


/**
 * 喜欢
 *
 * @author tg
 * @version V1.0
 * @since 2025-07-20
 */
@Tag(name ="喜欢")
@RestController
@RequestMapping("/cus/dating/dtLike")
@Slf4j
public class CusDtLikeController {
    @Resource
    private DtLikeService dtLikeService;
    @Resource
    BizSysUserService bizSysUserService;

    @Operation(summary ="添加对象的喜好情况")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody DtLike dtLike) {
        Assert.notBlank(dtLike.getLikeOtherSysUserCode(), "喜欢的对象不能为空");
        UserDTO sysUser = UserUtil.getCurrentSysUser();
        dtLike.setLikeOwnSysUserCode(sysUser.getUserCode());
        DtLike exist = dtLikeService.getOne(new LambdaQueryWrapper<DtLike>().eq(DtLike::getLikeOwnSysUserCode, sysUser.getUserCode()).eq(DtLike::getLikeOtherSysUserCode, dtLike.getLikeOtherSysUserCode()),false);
        if (exist == null) {
            dtLikeService.save(dtLike);
        }else {
            exist.setLikeDegreeCode(dtLike.getLikeDegreeCode());
            dtLikeService.updateById(exist);
        }
        return Result.ok("添加成功！");
    }

    @Operation(summary = "喜欢/不喜欢-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<UserDTO>> likedList(UserDTO sysUser,
                                            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        List<DtLike> dtLikeList = dtLikeService.list(new QueryWrapper<DtLike>().lambda().eq(DtLike::getLikeDegreeCode, DtLikeDegreeCodeEnum.like.getCode()).eq(DtLike::getLikeOwnSysUserCode, UserUtil.getCurrentSysUser().getUserCode()));
        IPage<UserDTO> page = bizSysUserService.page(sysUser,pageNo, pageSize);
        return Result.ok(page);
    }


}
