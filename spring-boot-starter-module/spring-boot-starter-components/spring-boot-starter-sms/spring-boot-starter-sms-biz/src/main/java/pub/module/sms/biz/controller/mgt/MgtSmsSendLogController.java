package pub.module.sms.biz.controller.mgt;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;
import pub.module.sms.crud.entity.SmsSendLog;
import pub.module.sms.crud.service.ISmsSendLogService;

import java.util.Collection;

@Tag(name = "管理端-sms_send_log")
@RestController
@RequestMapping("/mgt/sms/smsSendLog")
public class MgtSmsSendLogController {

    @Resource
    private ISmsSendLogService smsSendLogService;

    @Operation(summary = "管理端-sms_send_log-分页列表查询")
    @GetMapping("/list")
    public Result<IPage<SmsSendLog>> queryPageList(SmsSendLog smsSendLog,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<SmsSendLog> queryWrapper = WebQueryUtil.buildQuery(smsSendLog);
        Page<SmsSendLog> page = new Page<>(pageNo, pageSize);
        IPage<SmsSendLog> pageList = smsSendLogService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @Operation(summary = "管理端-sms_send_log-按主键查询")
    @GetMapping("/queryById")
    public Result<SmsSendLog> queryById(@RequestParam(name = "id") String id) {
        SmsSendLog entity = smsSendLogService.getByCode(id);
        return Result.ok(entity);
    }
}
