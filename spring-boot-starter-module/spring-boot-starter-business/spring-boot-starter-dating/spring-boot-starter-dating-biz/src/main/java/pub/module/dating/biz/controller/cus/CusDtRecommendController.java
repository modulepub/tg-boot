package pub.module.dating.biz.controller.cus;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pub.module.dating.curd.entity.DtRecommended;
import pub.module.dating.curd.service.DtRecommendedService;
import pub.module.system.api.service.BizSysUserService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;
import pub.module.web.vo.Result;

import jakarta.annotation.Resource;
import java.util.*;

@Tag(name = "客户")
@RestController
@RequestMapping("/cus/dating/recommend")
@Slf4j
public class CusDtRecommendController {
    @Resource
    DtRecommendedService dtRecommendedService;
    @Resource
    BizSysUserService bizSysUserService;

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class RecommendedVO extends UserDTO {
    }

    @Operation(summary = "推荐查询-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<RecommendedVO>> recommendList(UserDTO sysUserDTO,
                                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        List<DtRecommended> recommendedList = dtRecommendedService.list(
                new QueryWrapper<DtRecommended>().lambda()
                        .eq(DtRecommended::getRcSysUserCode,
                                UserUtil.getCurrentSysUser().getUserCode()));
        Map<String, DtRecommended> recommendedMap = recommendedList.stream().collect(HashMap::new, (m, e) -> m.put(e.getRcSysUserCode(), e), HashMap::putAll);
        HashSet<String> userCodes = new HashSet<>();
        userCodes.add("-");
        userCodes.addAll(recommendedMap.keySet());
        IPage<UserDTO> page = bizSysUserService.page(sysUserDTO,pageNo, pageSize);
        IPage<RecommendedVO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<RecommendedVO> recommendedVOList = new ArrayList<>();
        for(UserDTO userDTO:page.getRecords()){
            RecommendedVO dtRecommended = BeanUtil.copyProperties(userDTO,RecommendedVO.class);
            recommendedVOList.add(dtRecommended);
        }
        result.setRecords(recommendedVOList);

        return Result.ok(result);
    }

}
