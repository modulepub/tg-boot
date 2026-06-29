package pub.module.dating.biz.controller.cus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;
import pub.module.dating.api.service.ApiDtContactService;
import pub.module.dating.crud.entity.DtContact;
import pub.module.dating.crud.service.DtContactService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;


/**
 * 联系人
 *
 * @author tg
 * 2026-05-01 23:01:09
 */
@Tag(name = "用户端-联系人")
@RestController
@RequestMapping("/cus/dating/dtContact")
@Slf4j
public class CusDtContactController {
    @Resource
    private DtContactService dtContactService;
    @Resource
    ApiDtContactService apiDtContactService;

    @Operation(summary = "用户端-联系人-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<DtContact>> queryPageList(DtContact dtContact,
                                                  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<DtContact> queryWrapper = WebQueryUtil.buildQuery(dtContact);
        Page<DtContact> page = new Page<>(pageNo, pageSize);
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        queryWrapper.lambda().eq(DtContact::getUserCode,userDTO.getUserCode());
        IPage<DtContact> pageList = dtContactService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @Operation(summary = "用户端-联系人-移除")
    @PostMapping(value = "/remove")
    public Result<String> reject(@RequestBody ApiDtContactService.RemoveDTO removeDTO) {
        apiDtContactService.remove(removeDTO);
        return Result.ok("移除成功!");
    }

    @Operation(summary = "用户端-联系人-是否与对方仍为好友")
    @GetMapping(value = "/checkPeer")
    public Result<Boolean> checkPeer(@RequestParam(name = "peerUserCode") String peerUserCode) {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        boolean mutual = apiDtContactService.isMutualContact(userDTO.getUserCode(), peerUserCode);
        return Result.ok(mutual);
    }


}