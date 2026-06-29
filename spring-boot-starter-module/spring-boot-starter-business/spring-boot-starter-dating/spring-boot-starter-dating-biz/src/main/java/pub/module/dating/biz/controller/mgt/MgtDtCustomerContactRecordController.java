package pub.module.dating.biz.controller.mgt;

import java.util.Collection;

import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;

import pub.module.dating.crud.entity.DtCustomerContactRecord;
import pub.module.dating.crud.service.DtCustomerContactRecordService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;



/**
 * 管理端-联络记录
 *
 * @author tg
 *  2026-02-01 10:25:44
 */
@Tag(name="管理端-联络记录")
@RestController
@RequestMapping("/mgt/customer/customerContactRecord")
@Slf4j
public class MgtDtCustomerContactRecordController{
        @Resource
        private DtCustomerContactRecordService customerContactRecordService;


        @Operation(summary="管理端-联络记录分页列表查询")
        @GetMapping(value = "/list")
        public Result<IPage<DtCustomerContactRecord>> queryPageList(DtCustomerContactRecord customerContactRecord,
                @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
            QueryWrapper<DtCustomerContactRecord> queryWrapper = WebQueryUtil.buildQuery(customerContactRecord);
            Page<DtCustomerContactRecord> page = new Page<>(pageNo, pageSize);
            IPage<DtCustomerContactRecord> pageList = customerContactRecordService.page(page, queryWrapper);
            return Result.ok(pageList);
        }

        @Operation(summary="管理端-联络记录添加")
        @PostMapping(value = "/add")
        public Result<String> add(@RequestBody DtCustomerContactRecord customerContactRecord) {

                customerContactRecordService.save(customerContactRecord);
            return Result.ok("添加成功！");
        }

        @Operation(summary="管理端-联络记录编辑")
        @PostMapping(value = "/edit")
        public Result<String> edit(@RequestBody DtCustomerContactRecord customerContactRecord) {
                customerContactRecordService.updateById(customerContactRecord);
            return Result.ok("编辑成功!");
        }


        @Operation(summary="管理端-联络记录批量删除")
        @PostMapping(value = "/delete")
        public Result<String> deleteBatch(@RequestBody Collection<String> list) {
            this.customerContactRecordService.removeByIds(list);
            return Result.ok("批量删除成功!");
        }

        @Operation(summary="管理端-联络记录通过id查询")
        @GetMapping(value = "/queryById")
        public Result<DtCustomerContactRecord> queryById(@RequestParam(name="id") String id) {
            DtCustomerContactRecord customerContactRecord = customerContactRecordService.getById(id);
            return Result.ok(customerContactRecord);
        }

}