package pub.module.dating.biz.controller.cus;

import java.time.LocalDateTime;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.common.model.vo.Result;
import pub.module.dating.api.constants.DatingRecommendConstants;
import pub.module.dating.api.service.ApiDtCustomerService;
import pub.module.dating.api.service.ApiDtIntentionService;
import pub.module.dating.api.service.ApiDtRecommendedService;
import pub.module.dating.api.service.dto.DtCustomerDTO;
import pub.module.dating.api.service.dto.DtIntentionDTO;
import pub.module.dating.crud.entity.DtIntention;
import pub.module.dating.crud.service.DtIntentionService;
import pub.module.system.api.util.UserUtil;


/**
 * 用户端-交友意向
 *
 * @author tg
 * 2026-01-07 23:30:24
 */
@Tag(name = "用户端-交友意向")
@RestController
@RequestMapping("/cus/dating/dtIntention")
@Slf4j
public class CusDtIntentionController {
    @Resource
    private DtIntentionService dtIntentionService;
    @Resource
    private ApiDtIntentionService apiDtIntentionService;
    @Resource
    private ApiDtRecommendedService apiDtRecommendedService;
    @Resource
    private ApiDtCustomerService apiDtCustomerService;


    @Operation(summary = "用户端-交友意向编辑")
    @PostMapping(value = "/edit")
    public Result<String> edit(@RequestBody DtIntention body) {
        if (StrUtil.isBlank(body.getIntentionCode())) {
            return Result.error("intentionCode不能为空");
        }
        String userCode = UserUtil.getCurrentSysUser().getUserCode();
        if (StrUtil.isBlank(userCode)) {
            return Result.error("未登录");
        }
        boolean exists = dtIntentionService.lambdaQuery()
                .eq(DtIntention::getIntentionCode, body.getIntentionCode())
                .eq(DtIntention::getIntentionUserCode, userCode)
                .exists();
        if (!exists) {
            return Result.error("意向不存在或无权编辑");
        }
        LambdaUpdateWrapper<DtIntention> uw = new LambdaUpdateWrapper<>();
        uw.eq(DtIntention::getIntentionCode, body.getIntentionCode())
                .eq(DtIntention::getIntentionUserCode, userCode);
        uw.set(DtIntention::getIntentionName, body.getIntentionName())
                .set(DtIntention::getIntentionMinAge, body.getIntentionMinAge())
                .set(DtIntention::getIntentionMaxAge, body.getIntentionMaxAge())
                .set(DtIntention::getIntentionCityCode, body.getIntentionCityCode())
                .set(DtIntention::getIntentionSexCode, body.getIntentionSexCode())
                .set(DtIntention::getIntentionHaveHouseCode, body.getIntentionHaveHouseCode())
                .set(DtIntention::getIntentionHaveCarCode, body.getIntentionHaveCarCode())
                .set(DtIntention::getIntentionAgreeStatusCode,body.getIntentionAgreeStatusCode())
                .set(DtIntention::getIntentionDisabledStatusCode, body.getIntentionDisabledStatusCode())
                .set(DtIntention::getUpdateTime, LocalDateTime.now());
        if (body.getCusKinshipCode() != null) {
            uw.set(DtIntention::getCusKinshipCode, body.getCusKinshipCode());
        }
        if (body.getIntentionLdrStatusCode() != null) {
            uw.set(DtIntention::getIntentionLdrStatusCode, body.getIntentionLdrStatusCode());
        }
        if (body.getIntentionHigherEducationStatusCode() != null) {
            uw.set(DtIntention::getIntentionHigherEducationStatusCode, body.getIntentionHigherEducationStatusCode());
        }
        if (body.getIntentionSupportStatusCode() != null) {
            uw.set(DtIntention::getIntentionSupportStatusCode, body.getIntentionSupportStatusCode());
        }
        dtIntentionService.update(uw);
        // 保存意向后同步免费推荐（按每日推荐额度）
        DtIntentionDTO dtIntentionDTO = BeanUtil.copyProperties(body, DtIntentionDTO.class);
        DtCustomerDTO customer = apiDtCustomerService.getCusByUserCode(userCode);
        int recommendCount = DatingRecommendConstants.resolveDailyRecommendCount(customer);
        apiDtRecommendedService.synFreeRecommend(
                dtIntentionDTO,
                userCode,
                recommendCount,
                StrUtil.trim(body.getGuestPreviewCusCode()));
        return Result.ok("编辑成功!");
    }


    @Operation(summary = "用户端-获得推荐意向")
    @GetMapping(value = "/getDtIntention")
    public Result<DtIntentionDTO> getDtIntention() {
        DtIntentionDTO dtIntentionDTO = apiDtIntentionService.getDtIntention(UserUtil.getCurrentSysUser().getUserCode());
        return Result.ok(dtIntentionDTO);
    }

}