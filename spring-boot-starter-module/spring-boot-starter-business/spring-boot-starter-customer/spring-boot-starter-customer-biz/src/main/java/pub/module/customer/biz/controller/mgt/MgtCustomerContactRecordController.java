package pub.module.customer.biz.controller.mgt;

import java.util.Collection;

import pub.module.web.vo.Result;
import pub.module.web.util.WebQueryUtil;

import pub.module.customer.curd.entity.CustomerContactRecord;
import pub.module.customer.curd.service.CustomerContactRecordService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;



/**
 * 联络记录 Controller
 *
 * @author tg
 *  2026-02-01 10:25:44
 */
@Tag(name="联络记录 CURD 处理器")
@RestController
@RequestMapping("/mgt/customer/customerContactRecord")
@Slf4j
public class MgtCustomerContactRecordController{
        @Resource
        private CustomerContactRecordService customerContactRecordService;


        @Operation(summary="联络记录 - 分页列表查询")
        @GetMapping(value = "/list")
        public Result<IPage<CustomerContactRecord>> queryPageList(CustomerContactRecord customerContactRecord,
                @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
            QueryWrapper<CustomerContactRecord> queryWrapper = WebQueryUtil.buildQuery(customerContactRecord);
            Page<CustomerContactRecord> page = new Page<>(pageNo, pageSize);
            IPage<CustomerContactRecord> pageList = customerContactRecordService.page(page, queryWrapper);
            return Result.ok(pageList);
        }

        @Operation(summary="联络记录 - 添加")
        @PostMapping(value = "/add")
        public Result<String> add(@RequestBody CustomerContactRecord customerContactRecord) {

                customerContactRecordService.save(customerContactRecord);
            return Result.ok("添加成功！");
        }

        @Operation(summary="联络记录 - 编辑")
        @PostMapping(value = "/edit")
        public Result<String> edit(@RequestBody CustomerContactRecord customerContactRecord) {
                customerContactRecordService.updateById(customerContactRecord);
            return Result.ok("编辑成功!");
        }


        @Operation(summary="联络记录 - 批量删除")
        @PostMapping(value = "/delete")
        public Result<String> deleteBatch(@RequestBody Collection<String> list) {
            this.customerContactRecordService.removeByIds(list);
            return Result.ok("批量删除成功!");
        }

        @Operation(summary="联络记录 - 通过id查询")
        @GetMapping(value = "/queryById")
        public Result<CustomerContactRecord> queryById(@RequestParam(name="id") String id) {
            CustomerContactRecord customerContactRecord = customerContactRecordService.getById(id);
            return Result.ok(customerContactRecord);
        }

}