package pub.module.dating.biz.controller.pub;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import cn.hutool.core.util.StrUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pub.module.dating.api.service.ApiDtCustomerService;
import pub.module.dating.api.service.dto.DtCustomerDTO;
import pub.module.dating.crud.entity.DtCusMatchmakerRel;
import pub.module.dating.crud.service.DtCusMatchmakerRelService;
import pub.module.dating.biz.service.impl.ApiDtCusMatchmakerRelServiceImpl;
import pub.module.common.util.WebQueryUtil;
import pub.module.common.model.vo.Result;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;


/**
 * 公开-客户红娘关系
 *
 * @author tg
 *  2026-03-25 00:36:20
 */
@Tag(name="公开-客户红娘关系")
@RestController
@RequestMapping("/pub/dating/dtCusMatchmakerRel")
@Slf4j
public class PubDtCusMatchmakerRelController {
        @Resource
        private DtCusMatchmakerRelService dtCusMatchmakerRelService;
        @Resource
        private ApiDtCustomerService apiDtCustomerService;

        @Operation(summary="公开-红娘主页-ta推荐的男女嘉宾")
        @GetMapping(value = "/listByMkCode")
        public Result<IPage<DtCusMatchmakerRel>> listByMkCode(DtCusMatchmakerRel dtCusMatchmakerRel,
                @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
            QueryWrapper<DtCusMatchmakerRel> queryWrapper = WebQueryUtil.buildQuery(dtCusMatchmakerRel);
            Assert.notBlank(dtCusMatchmakerRel.getMkCode(),"警报：mkCode is null");
            ApiDtCusMatchmakerRelServiceImpl.excludeHiddenCusMatchmakerRel(queryWrapper);
            excludeViewerSelfFromGuestList(queryWrapper);
            Page<DtCusMatchmakerRel> page = new Page<>(pageNo, pageSize);
            IPage<DtCusMatchmakerRel> pageList = dtCusMatchmakerRelService.page(page, queryWrapper);
            return Result.ok(pageList);
        }

        /** 已登录访客不在 TA 推荐列表中看到自己 */
        private void excludeViewerSelfFromGuestList(QueryWrapper<DtCusMatchmakerRel> queryWrapper) {
            String myCusCode = resolveOptionalViewerCusCode();
            if (StrUtil.isNotBlank(myCusCode)) {
                queryWrapper.lambda().ne(DtCusMatchmakerRel::getCusCode, myCusCode.trim());
            }
        }

        private String resolveOptionalViewerCusCode() {
            try {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth == null || !auth.isAuthenticated()) {
                    return null;
                }
                String principal = StrUtil.trimToNull(auth.getName());
                if (principal == null || "anonymousUser".equalsIgnoreCase(principal)) {
                    return null;
                }
                UserDTO userDTO = UserUtil.getCurrentSysUser();
                if (userDTO == null || StrUtil.isBlank(userDTO.getUserCode())) {
                    return null;
                }
                DtCustomerDTO customer = apiDtCustomerService.getCusByUserCode(userDTO.getUserCode());
                return customer == null ? null : StrUtil.trimToNull(customer.getCusCode());
            }
            catch (Exception e) {
                log.debug("[listByMkCode] skip self exclude: {}", e.getMessage());
                return null;
            }
        }


    @Operation(summary="公开-嘉宾主页Ta的专属情感顾问")
    @GetMapping(value = "/listByCusCode")
    public Result<IPage<DtCusMatchmakerRel>> listByCusCode(DtCusMatchmakerRel dtCusMatchmakerRel,
                                                           @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                           @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
        QueryWrapper<DtCusMatchmakerRel> queryWrapper = WebQueryUtil.buildQuery(dtCusMatchmakerRel);
        Assert.notBlank(dtCusMatchmakerRel.getCusCode(),"警报：cusCode is null");
        Page<DtCusMatchmakerRel> page = new Page<>(pageNo, pageSize);
        IPage<DtCusMatchmakerRel> pageList = dtCusMatchmakerRelService.page(page, queryWrapper);
        return Result.ok(pageList);
    }
}