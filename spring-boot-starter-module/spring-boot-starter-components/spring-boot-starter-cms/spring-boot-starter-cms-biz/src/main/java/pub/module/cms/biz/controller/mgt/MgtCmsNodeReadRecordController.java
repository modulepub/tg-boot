package pub.module.cms.biz.controller.mgt;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pub.module.cms.crud.entity.CmsNodeReadRecord;
import pub.module.cms.crud.service.CmsNodeReadRecordService;
import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.service.dto.UserDTO;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "管理端-CMS-文章阅读记录")
@RestController
@RequestMapping("/mgt/cms/cmsNodeReadRecord")
@Slf4j
public class MgtCmsNodeReadRecordController {

    @Resource
    private CmsNodeReadRecordService cmsNodeReadRecordService;
    @Resource
    private ApiSysUserService apiSysUserService;

    @Operation(summary = "管理端-CMS-文章阅读记录分页列表")
    @GetMapping("/list")
    public Result<IPage<MgtCmsNodeReadRecordVO>> queryPageList(
            CmsNodeReadRecord cmsNodeReadRecord,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<CmsNodeReadRecord> queryWrapper = WebQueryUtil.buildQuery(cmsNodeReadRecord);
        queryWrapper.orderByDesc("create_time");
        Page<CmsNodeReadRecord> page = new Page<>(pageNo, pageSize);
        IPage<CmsNodeReadRecord> pageList = cmsNodeReadRecordService.page(page, queryWrapper);
        return Result.ok(enrichPage(pageList));
    }

    private IPage<MgtCmsNodeReadRecordVO> enrichPage(IPage<CmsNodeReadRecord> pageList) {
        List<String> userCodes = pageList.getRecords().stream()
                .map(CmsNodeReadRecord::getNodeReadRecordUserCode)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .toList();
        Map<String, UserDTO> userMap = userCodes.isEmpty()
                ? Collections.emptyMap()
                : userCodes.stream()
                .map(apiSysUserService::getUserByUserCode)
                .filter(user -> user != null && StrUtil.isNotBlank(user.getUserCode()))
                .collect(Collectors.toMap(UserDTO::getUserCode, user -> user, (left, right) -> left));

        Page<MgtCmsNodeReadRecordVO> voPage = new Page<>(pageList.getCurrent(), pageList.getSize(), pageList.getTotal());
        voPage.setRecords(pageList.getRecords().stream().map(record -> toVo(record, userMap)).toList());
        return voPage;
    }

    private static MgtCmsNodeReadRecordVO toVo(CmsNodeReadRecord record, Map<String, UserDTO> userMap) {
        MgtCmsNodeReadRecordVO vo = BeanUtil.copyProperties(record, MgtCmsNodeReadRecordVO.class);
        UserDTO user = userMap.get(record.getNodeReadRecordUserCode());
        if (user != null) {
            vo.setUserNickName(user.getUserNickName());
            vo.setUserPhone(user.getUserPhone());
            vo.setUserRealName(user.getUserRealName());
        }
        return vo;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @Schema(description = "管理端-CMS-文章阅读记录")
    public static class MgtCmsNodeReadRecordVO extends CmsNodeReadRecord {

        @Schema(description = "用户昵称")
        private String userNickName;

        @Schema(description = "用户手机号")
        private String userPhone;

        @Schema(description = "用户姓名")
        private String userRealName;
    }
}
