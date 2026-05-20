package pub.module.distribution.biz.controller.mgt;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;
import pub.module.distribution.api.constants.WalWithdrawStatusCodeEnum;
import pub.module.distribution.biz.service.internal.BizWalAccountInternalService;
import pub.module.distribution.curd.entity.WalAccount;
import pub.module.distribution.curd.entity.WalWithdraw;
import pub.module.distribution.curd.mapper.WalWithdrawMapper;

import java.time.LocalDateTime;
import java.util.Map;

@Tag(name = "管理端-提现审核")
@RestController
@RequestMapping("/mgt/distribution/walWithdraw")
public class MgtWalWithdrawController {

    @Resource
    private WalWithdrawMapper walWithdrawMapper;
    @Resource
    private BizWalAccountInternalService bizWalAccountInternalService;

    @GetMapping("/list")
    public Result<IPage<WalWithdraw>> list(WalWithdraw query,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<WalWithdraw> wrapper = WebQueryUtil.buildQuery(query);
        wrapper.lambda().orderByDesc(WalWithdraw::getCreateTime);
        return Result.ok(walWithdrawMapper.selectPage(new Page<>(pageNo, pageSize), wrapper));
    }

    @PostMapping("/approve")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> approve(@RequestBody Map<String, String> body) {
        String walWithdrawCode = body.get("walWithdrawCode");
        WalWithdraw withdraw = getPending(walWithdrawCode);
        withdraw.setWalWithdrawStatusCode(WalWithdrawStatusCodeEnum.SUCCESS.getCode());
        withdraw.setWalWithdrawArrivedTime(LocalDateTime.now());
        walWithdrawMapper.updateById(withdraw);
        WalAccount account = bizWalAccountInternalService.getOrCreate(withdraw.getDistBizLineCode(), withdraw.getWalUserCode());
        bizWalAccountInternalService.completeWithdraw(account, withdraw.getWalWithdrawAmount(), walWithdrawCode);
        return Result.ok("已标记到账");
    }

    @PostMapping("/reject")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> reject(@RequestBody Map<String, String> body) {
        String walWithdrawCode = body.get("walWithdrawCode");
        WalWithdraw withdraw = getPending(walWithdrawCode);
        withdraw.setWalWithdrawStatusCode(WalWithdrawStatusCodeEnum.REJECTED.getCode());
        walWithdrawMapper.updateById(withdraw);
        WalAccount account = bizWalAccountInternalService.getOrCreate(withdraw.getDistBizLineCode(), withdraw.getWalUserCode());
        bizWalAccountInternalService.rejectWithdraw(account, withdraw.getWalWithdrawAmount(), walWithdrawCode);
        return Result.ok("已驳回");
    }

    private WalWithdraw getPending(String walWithdrawCode) {
        Assert.notBlank(walWithdrawCode, "walWithdrawCode 不能为空");
        WalWithdraw withdraw = walWithdrawMapper.selectOne(new QueryWrapper<WalWithdraw>().lambda()
                .eq(WalWithdraw::getWalWithdrawCode, walWithdrawCode), false);
        Assert.notNull(withdraw, "提现单不存在");
        Assert.isTrue(WalWithdrawStatusCodeEnum.PENDING.getCode().equals(withdraw.getWalWithdrawStatusCode()), "仅待处理单可操作");
        return withdraw;
    }
}
