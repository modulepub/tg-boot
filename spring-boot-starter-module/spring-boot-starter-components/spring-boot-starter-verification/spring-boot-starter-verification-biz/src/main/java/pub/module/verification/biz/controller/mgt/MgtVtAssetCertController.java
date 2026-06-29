package pub.module.verification.biz.controller.mgt;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;
import pub.module.verification.api.dto.VtAssetCertRejectVO;
import pub.module.verification.api.service.ApiVtAssetCertMgtService;
import pub.module.verification.crud.entity.VtAssetCertRecord;
import pub.module.verification.crud.service.VtAssetCertRecordService;

/**
 * 管理端-资产认证审核
 */
@Tag(name = "管理端-资产认证")
@RestController
@RequestMapping("/mgt/verification/vtAssetCert")
@Slf4j
public class MgtVtAssetCertController {

    @Resource
    private VtAssetCertRecordService vtAssetCertRecordService;
    @Resource
    private ApiVtAssetCertMgtService apiVtAssetCertMgtService;

    @Operation(summary = "管理端-资产认证分页列表")
    @GetMapping("/list")
    public Result<IPage<VtAssetCertRecord>> queryPageList(
            VtAssetCertRecord vtAssetCertRecord,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<VtAssetCertRecord> queryWrapper = WebQueryUtil.buildQuery(vtAssetCertRecord);
        queryWrapper.orderByDesc("create_time");
        Page<VtAssetCertRecord> page = new Page<>(pageNo, pageSize);
        IPage<VtAssetCertRecord> pageList = vtAssetCertRecordService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @Operation(summary = "管理端-资产认证详情")
    @GetMapping("/queryById")
    public Result<VtAssetCertRecord> queryById(@RequestParam(name = "id") String id) {
        VtAssetCertRecord record = vtAssetCertRecordService.getById(id);
        return Result.ok(record);
    }

    @Operation(summary = "管理端-审核通过")
    @PostMapping("/approve")
    public Result<String> approve(@RequestParam("id") String id) {
        UserDTO user = UserUtil.getCurrentSysUser();
        String auditBy = user != null ? user.getUserCode() : null;
        apiVtAssetCertMgtService.approve(id, auditBy);
        return Result.ok("审核通过");
    }

    @Operation(summary = "管理端-审核驳回")
    @PostMapping("/reject")
    public Result<String> reject(@RequestBody VtAssetCertRejectVO vo) {
        UserDTO user = UserUtil.getCurrentSysUser();
        String auditBy = user != null ? user.getUserCode() : null;
        apiVtAssetCertMgtService.reject(vo.getId(), vo.getRejectReason(), auditBy);
        return Result.ok("已驳回");
    }
}
