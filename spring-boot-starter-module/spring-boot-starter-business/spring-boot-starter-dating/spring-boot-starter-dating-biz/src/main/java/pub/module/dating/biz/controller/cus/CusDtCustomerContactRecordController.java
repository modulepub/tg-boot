package pub.module.dating.biz.controller.cus;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.dating.biz.controller.cus.vo.DtCustomerContactRecordReqVO;
import pub.module.dating.biz.service.SpiDtCustomerContactRecordService;
import pub.module.dating.crud.entity.DtCustomerContactRecord;
import pub.module.dating.crud.service.DtCustomerContactRecordService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;
import pub.module.common.util.WebQueryUtil;
import pub.module.common.model.vo.Result;


/**
 * 用户端-联络记录
 *
 * @author tg
 * 2026-02-01 10:25:44
 */
@Tag(name = "用户端-客户端-联络记录")
@RestController
@RequestMapping("/cus/customer/customerContactRecord")
@Slf4j
public class CusDtCustomerContactRecordController {
    @Resource
    private DtCustomerContactRecordService customerContactRecordService;
    @Resource
    private SpiDtCustomerContactRecordService spiDtCustomerContactRecordService;


    @Operation(summary = "用户端-员工联络记录分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<DtCustomerContactRecord>> queryPageList(DtCustomerContactRecord customerContactRecord,
                                                              @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<DtCustomerContactRecord> queryWrapper = WebQueryUtil.buildQuery(customerContactRecord);
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        queryWrapper.lambda().eq(DtCustomerContactRecord::getUserCode, userDTO.getUserCode());
        Page<DtCustomerContactRecord> page = new Page<>(pageNo, pageSize);
        IPage<DtCustomerContactRecord> pageList = customerContactRecordService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @Operation(summary = "用户端-员工联络记录添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody DtCustomerContactRecordReqVO customerContactRecordReqVO) {
        DtCustomerContactRecord customerContactRecord = BeanUtil.copyProperties(customerContactRecordReqVO, DtCustomerContactRecord.class);
        spiDtCustomerContactRecordService.doRecord(customerContactRecord);
        return Result.ok("添加成功！");
    }

    @Operation(summary = "用户端-员工联络记录编辑")
    @PostMapping(value = "/edit")
    public Result<String> edit(@RequestBody DtCustomerContactRecord customerContactRecord) {
        customerContactRecordService.updateById(customerContactRecord);
        return Result.ok("编辑成功!");
    }

    @Operation(summary = "用户端-联络记录通过id查询")
    @GetMapping(value = "/queryById")
    public Result<DtCustomerContactRecord> queryById(@RequestParam(name = "id") String id) {
        DtCustomerContactRecord customerContactRecord = customerContactRecordService.getById(id);
        return Result.ok(customerContactRecord);
    }

}