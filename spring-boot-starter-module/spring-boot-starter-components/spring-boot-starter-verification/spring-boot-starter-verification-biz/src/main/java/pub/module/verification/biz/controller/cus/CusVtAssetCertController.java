package pub.module.verification.biz.controller.cus;

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
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;
import pub.module.verification.api.dto.VtAssetCertRecordDTO;
import pub.module.verification.api.dto.VtAssetCertSubmitVO;
import pub.module.verification.api.service.ApiVtAssetCertService;

/**
 * 用户端-资产认证（爱与诚辅助认证）
 */
@Tag(name = "用户端-资产认证")
@RestController
@RequestMapping("/cus/verification/vtAssetCert")
@Slf4j
public class CusVtAssetCertController {

    @Resource
    private ApiVtAssetCertService apiVtAssetCertService;

    @Operation(summary = "红娘提交客户资产认证申请")
    @PostMapping("/submitApply")
    public Result<VtAssetCertRecordDTO> submitApply(@RequestBody VtAssetCertSubmitVO vo) {
        UserDTO user = UserUtil.getCurrentSysUser();
        if (user == null || user.getUserCode() == null) {
            return Result.error("请先登录");
        }
        VtAssetCertRecordDTO record = apiVtAssetCertService.submitApply(user.getUserCode(), vo);
        return Result.ok(record);
    }

    @Operation(summary = "查询客户最新资产认证申请")
    @GetMapping("/getLatestByCusCode")
    public Result<VtAssetCertRecordDTO> getLatestByCusCode(@RequestParam("cusCode") String cusCode) {
        UserDTO user = UserUtil.getCurrentSysUser();
        if (user == null || user.getUserCode() == null) {
            return Result.error("请先登录");
        }
        VtAssetCertRecordDTO record = apiVtAssetCertService.getLatestByCusCode(user.getUserCode(), cusCode);
        return Result.ok(record);
    }
}
