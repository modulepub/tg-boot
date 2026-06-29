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
import pub.module.sms.crud.entity.SmsTemplate;
import pub.module.sms.crud.service.ISmsTemplateService;

import java.util.Collection;

@Tag(name = "管理端-sms_template")
@RestController
@RequestMapping("/mgt/sms/smsTemplate")
public class MgtSmsTemplateController {

    @Resource
    private ISmsTemplateService smsTemplateService;

    @Operation(summary = "管理端-sms_template-分页列表查询")
    @GetMapping("/list")
    public Result<IPage<SmsTemplate>> queryPageList(SmsTemplate smsTemplate,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<SmsTemplate> queryWrapper = WebQueryUtil.buildQuery(smsTemplate);
        Page<SmsTemplate> page = new Page<>(pageNo, pageSize);
        IPage<SmsTemplate> pageList = smsTemplateService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @Operation(summary = "管理端-sms_template-新增")
    @PostMapping("/add")
    public Result<String> add(@RequestBody SmsTemplate entity) {
        smsTemplateService.save(entity);
        return Result.ok("添加成功！");
    }

    @Operation(summary = "管理端-sms_template-编辑")
    @RequestMapping(value = "/edit", method = { RequestMethod.PUT, RequestMethod.POST })
    public Result<String> edit(@RequestBody SmsTemplate entity) {
        smsTemplateService.updateById(entity);
        return Result.ok("编辑成功!");
    }

    @Operation(summary = "管理端-sms_template-批量删除")
    @PostMapping("/delete")
    public Result<String> deleteBatch(@RequestBody Collection<String> list) {
        smsTemplateService.removeByBizCodes(list);
        return Result.ok("批量删除成功!");
    }

    @Operation(summary = "管理端-sms_template-按业务编码查询")
    @GetMapping("/queryById")
    public Result<SmsTemplate> queryById(@RequestParam(name = "id") String id) {
        SmsTemplate entity = smsTemplateService.getByCode(id);
        return Result.ok(entity);
    }
}
